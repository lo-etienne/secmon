package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.*;
import be.flmr.secmon.core.pattern.*;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Protocol;
import be.flmr.secmon.core.security.AESUtils;
import be.flmr.secmon.core.security.Base64AesUtils;
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
import java.util.stream.Collectors;

public class NorthPole extends AbstractRouter implements INorthPole {
    private static final Logger log = LoggerFactory.getLogger(NorthPole.class);
    private final ServiceStateStack stateStack;

    private IProtocolPacketReceiver multicast;
    private IDaemonConfigurationReader daemonConfigurationReader;

    private ExecutorService executor = Executors.newFixedThreadPool(3);

    public NorthPole(IProtocolPacketReceiver multicast, IDaemonConfigurationReader daemonConfigurationReader, ServiceStateStack stateStack) {
        super();
        this.stateStack = stateStack;
        this.multicast = multicast;
        this.daemonConfigurationReader = daemonConfigurationReader;
    }

    @Override
    public void run() {
        log.info("Lancement du pôle nord");
        IProtocolPacket receivedPacket = multicast.receive();
        executor.execute(() -> execute(receivedPacket.getSourceAddress(), receivedPacket));
    }

    @Protocol(pattern = ProtocolPattern.ANNOUNCE)
    private void onAnnounce(Object sender, IProtocolPacket packet) {
        log.info("Réception d'un message ANNOUNCE de {}", sender);
        List<IService> services = daemonConfigurationReader.getServices();
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
        log.info("Réception d'un message de {}", sender);
        String protocol = packet.getValue(PatternGroup.PROTOCOL);
        List<String> ids = daemonConfigurationReader.getServices()
                .stream().filter(s -> s.getAugmentedURL().contains(protocol))
                .map(IService::getID)
                .collect(Collectors.toList());

        String host = ((InetAddress) sender).getHostAddress();
        int port = Integer.parseInt(packet.getValue(PatternGroup.PORT));

        try (Socket socket = new Socket(host, port)) {

            for (String id : ids) {
                write(newStateReq(id), socket);
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
            stateStack.getStates(service).push(ServiceState.valueOf(packet.getValue(PatternGroup.STATE)));
        });
    }

    private IProtocolPacket read(Socket s) {
        try {
            var in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line = in.readLine();
            String decrypted = Base64AesUtils.decrypt(line, daemonConfigurationReader.getAesKey());
            return ProtocolPacket.from(decrypted);
        } catch (IOException e) {
            log.error("Le message n'as pas pû être reçu de {}", s.getInetAddress());
            throw new IllegalArgumentException();
        }
    }

    private void write(IProtocolPacket packet, Socket s) {
        try {
            var out = new PrintWriter(s.getOutputStream());
            String encrypted = Base64AesUtils.encrypt(packet.buildMessage(), daemonConfigurationReader.getAesKey());
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
    public void close() throws Exception {
        executor.shutdown();
    }
}
