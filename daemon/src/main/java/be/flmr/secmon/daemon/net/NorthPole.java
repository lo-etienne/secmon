package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.multicast.ConnectionBroadcaster;
import be.flmr.secmon.core.net.*;
import be.flmr.secmon.core.pattern.*;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Protocol;
import be.flmr.secmon.core.security.AESUtils;
import be.flmr.secmon.core.security.Base64AesUtils;
import be.flmr.secmon.daemon.config.DaemonJSONConfig;
import be.flmr.secmon.daemon.config.IDaemonConfigurationReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class NorthPole extends AbstractRouter implements INorthPole {
    private static final Logger log = LoggerFactory.getLogger(NorthPole.class);
    private final ServiceStateStack stateStack;

    private IProtocolPacketReceiver multicast;
    private DaemonJSONConfig daemonJSONConfig;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    private boolean running = true;

    public NorthPole(DaemonJSONConfig config, ServiceStateStack stateStack) {
        super();
        this.stateStack = stateStack;
        this.multicast = new ConnectionBroadcaster(config.getMulticastAddress(), Integer.parseInt(config.getMulticastPort()), executor);
        this.daemonJSONConfig = config;
    }

    @Override
    public void run() {
        log.info("Lancement du pôle nord");
        while(running) {
            IProtocolPacket receivedPacket = multicast.receive();
            executor.execute(() -> execute(receivedPacket.getSourceAddress(), receivedPacket));
        }
    }

    @Protocol(pattern = ProtocolPattern.ANNOUNCE)
    private void onAnnounce(Object sender, IProtocolPacket packet) {
        log.info("Réception d'un message ANNOUNCE de {}", sender);
        List<IService> services = daemonJSONConfig.getServices();
        String strServices = services.stream()
                .filter(service -> service.getURL().contains(packet.getValue(PatternGroup.PROTOCOL)))
                .map(IService::getAugmentedURL)
                .reduce("", (a, b) -> a + (a.isEmpty() ? "" : " ") + b);

        IProtocolPacket config = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.CONFIG)
                .withGroup(PatternGroup.CONFIG, strServices)
                .build();

        String host = ((InetAddress) sender).getHostAddress();
        int port = Integer.parseInt(packet.getValue(PatternGroup.PORT));

        try (Socket socket = new Socket(host, port)) {
            write(config, socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Protocol(pattern = ProtocolPattern.NOTIFICATION)
    private void onNotify(Object sender, IProtocolPacket packet) {
        log.info("Réception d'un NOTIFY de {}", sender);
        String protocol = packet.getValue(PatternGroup.PROTOCOL);
        List<IService> services = daemonJSONConfig.getServices()
                .stream().filter(s -> s.getURL().contains(protocol))
                .collect(Collectors.toList());

        String host = ((InetAddress) sender).getHostAddress();
        int port = Integer.parseInt(packet.getValue(PatternGroup.PORT));

        try (Socket socket = new Socket(host, port)) {

            for (IService service : services) {
                write(newStateReq(service.getID()), socket);
                execute(socket, read(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Protocol(pattern = ProtocolPattern.STATE_RESP)
    private void onStateResponse(Object sender, IProtocolPacket packet) {
        log.info("Réception d'un STATEREQ de {}", sender);
        List<IService> services = Service.from(packet);
        services.forEach(service -> {
            if (!stateStack.hasService(service)) stateStack.registerService(service);
            stateStack.pushState(service, ServiceState.valueOf(packet.getValue(PatternGroup.STATE)));
        });
    }

    private IProtocolPacket read(Socket s) {
        try {
            var in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line = in.readLine();
            String decrypted = Base64AesUtils.decrypt(line, daemonJSONConfig.getAesKey());
            return ProtocolPacket.from(decrypted);
        } catch (IOException e) {
            log.error("Le message n'as pas pû être reçu de {}", s.getInetAddress());
            throw new IllegalArgumentException();
        }
    }

    private void write(IProtocolPacket packet, Socket s) {
        try {
            var out = new PrintWriter(s.getOutputStream());
            String encrypted = Base64AesUtils.encrypt(packet.buildMessage(), daemonJSONConfig.getAesKey());
            out.print(encrypted);
            out.flush();
        } catch (IOException e) {
            log.error("Le message n'as pas pû être envoyé vers {}", s.getInetAddress());
        }
    }

    private IProtocolPacket newStateReq(String id) {
        return new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.STATE_REQ)
                .withGroup(PatternGroup.ID, id)
                .build();
    }

    @Override
    public void close() {
        running = false;
        executor.shutdown();
    }
}
