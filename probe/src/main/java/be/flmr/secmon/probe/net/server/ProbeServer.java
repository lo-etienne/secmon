package be.flmr.secmon.probe.net.server;

import be.flmr.secmon.core.net.IIntervalProtocolPacketSender;
import be.flmr.secmon.core.net.IServer;
import be.flmr.secmon.core.net.Service;
import be.flmr.secmon.core.pattern.*;
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
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.*;

public class ProbeServer extends AbstractRouter implements IServer, Runnable {
    private IIntervalProtocolPacketSender multicastSender;
    private Map<ProbeClient, Future<?>> clients;

    private ProbeServiceCommunicator communicator;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    private IProtocolPacket announceMessage;
    private ServerSocket socket;
    private int aliveInterval;

    private static final Logger log = LoggerFactory.getLogger(ProbeServer.class);

    public ProbeServer(ProbeJSONConfigurationReader reader, IIntervalProtocolPacketSender multicastSender, ServiceProber prober) {
        try {
            socket = new ServerSocket(Integer.parseInt(reader.getMulticastPort()));
        } catch(IOException e) {
            e.printStackTrace();
        }
        clients = new ConcurrentHashMap<>();

        aliveInterval = reader.getAliveInterval();
        this.communicator = new ProbeServiceCommunicator(prober);

        announceMessage = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.ANNOUNCE)
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
                ProbeClient newClient = new ProbeClient(client, this, this);

                var task = executor.submit(newClient);

                clients.put(newClient, task);
                log.debug("Nouveau client ajouté");
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isShuttingDown() {
        return false;
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

        services.forEach((service) -> {
            this.communicator.addService(service);
        });

    }

    @Protocol(pattern = ProtocolPattern.STATE_REQ)
    private void getServiceState(Object sender, IProtocolPacket packet) {
        var id = packet.getValue(PatternGroup.ID);
        var state = communicator.getServiceState(id);

        var client = (ProbeClient) sender;
        var stateMessage = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.STATE_RESP)
                .withGroup(PatternGroup.ID, id)
                .withGroup(PatternGroup.STATE, state)
                .build();

        client.send(stateMessage);
    }
}
