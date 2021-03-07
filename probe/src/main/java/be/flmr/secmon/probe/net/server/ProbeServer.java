package be.flmr.secmon.probe.net.server;

import be.flmr.secmon.core.multicast.ConnectionBroadcaster;
import be.flmr.secmon.core.net.IServer;
import be.flmr.secmon.core.net.Service;
import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.PatternGroup;
import be.flmr.secmon.core.pattern.ProtocolPacketBuilder;
import be.flmr.secmon.core.pattern.ProtocolPattern;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Protocol;
import be.flmr.secmon.probe.config.ProbeJSONConfigurationReader;
import be.flmr.secmon.probe.net.client.ProbeClient;
import be.flmr.secmon.probe.service.ServiceProber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.*;

public class ProbeServer extends AbstractRouter implements IServer, Runnable, AutoCloseable {
    private final ConnectionBroadcaster multicastSender;
    private final Map<ProbeClient, Future<?>> clients;

    private ProbeServiceCommunicator communicator;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    private final IProtocolPacket announceMessage;
    private final IProtocolPacket notificationMessage;

    private ServerSocket socket;
    private int aliveInterval;

    private String aesKey;

    private static final Logger log = LoggerFactory.getLogger(ProbeServer.class);
    private boolean shutdown = false;

    public ProbeServer(ProbeJSONConfigurationReader reader, ConnectionBroadcaster multicastSender, ServiceProber prober) {
        super();
        try {
            socket = new ServerSocket(Integer.parseInt(reader.getMulticastPort()));
        } catch(IOException e) {
            e.printStackTrace();
        }
        clients = new ConcurrentHashMap<>();

        this.aesKey = reader.getAesKey();

        aliveInterval = reader.getAliveInterval();
        this.communicator = new ProbeServiceCommunicator(prober);
        this.communicator.setOnNewValue(this::onNewValue);

        announceMessage = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.ANNOUNCE)
                .withGroup(PatternGroup.PROTOCOL, reader.getProtocol())
                .withGroup(PatternGroup.PORT, reader.getMulticastPort())
                .build();
        notificationMessage = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.NOTIFICATION)
                .withGroup(PatternGroup.PROTOCOL, reader.getProtocol())
                .withGroup(PatternGroup.PORT, reader.getMulticastPort())
                .build();

        this.multicastSender = multicastSender;
    }

    @Override
    public void listenForConnections() {
        while(!(isShuttingDown())) {
            try {
                Socket client = socket.accept();

                ProbeClient newClient = new ProbeClient(client, this, this, this.aesKey);
                var future = executor.submit(newClient);
                clients.put(newClient, future);

                log.debug("Client connecté - {}", client.getInetAddress());
            } catch(IOException e) {
                log.warn("Le socket a été terminé, extinction du serveur...");
                if (socket.isClosed()) shutdown = true;
            }
        }
    }

    @Override
    public boolean isShuttingDown() {
        return shutdown;
    }

    @Override
    public void run() {
        log.info("Démarrage du Multicast");
        multicastSender.sendWithInterval(announceMessage, aliveInterval, TimeUnit.SECONDS);

        executor.execute(this::listenForConnections);
    }

    @Protocol(pattern = ProtocolPattern.CONFIG)
    private void updateServices(Object sender, IProtocolPacket packet) {
        var services = Service.from(packet);

        log.info("Réception d'une nouvelle configuration: {}", services);

        services.forEach((service) -> this.communicator.addService(service));
    }

    @Protocol(pattern = ProtocolPattern.STATE_REQ)
    private void getServiceState(Object sender, IProtocolPacket packet) {
        var id = packet.getValue(PatternGroup.ID);

        var client = (ProbeClient) sender;

        try {
            var state = communicator.getServiceState(id);

            log.info("Réception d'une requête d'état pour le service {}", id);

            var stateMessage = new ProtocolPacketBuilder()
                    .withPatternType(ProtocolPattern.STATE_RESP)
                    .withGroup(PatternGroup.ID, id)
                    .withGroup(PatternGroup.STATE, state)
                    .build();

            log.info("Envoi d'une réponse de statut ({}: {})", id, state);
            client.send(stateMessage);
        } catch(NoSuchElementException ex) {
            log.warn(ex.getMessage(), ex);
        }
    }

    private void onNewValue() {
        multicastSender.send(notificationMessage);
    }

    @Override
    public void close() throws Exception {
        for (Map.Entry<ProbeClient, Future<?>> entry : this.clients.entrySet()) {
            entry.getKey().close();
        }
        this.executor.shutdown();
        this.socket.close();
        multicastSender.close();
    }
}
