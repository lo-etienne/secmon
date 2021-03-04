package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.*;
import be.flmr.secmon.core.pattern.*;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Protocol;
import be.flmr.secmon.core.security.AESUtils;
import be.flmr.secmon.daemon.config.IDaemonConfigurationReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public NorthPole(IProtocolPacketReceiver multicast, IDaemonConfigurationReader daemonConfigurationReader, ServiceStateStack stateStack) {
        super();
        this.stateStack = stateStack;
        this.multicast = multicast;
        this.daemonConfigurationReader = daemonConfigurationReader;
    }

    @Override
    public void run() {
        IProtocolPacket receivedPacket = multicast.receive();
        execute(null, receivedPacket);
    }

    @Protocol(pattern = ProtocolPattern.ANNOUNCE)
    private void onAnnounce(Object sender, IProtocolPacket packet) {
        List<IService> services = daemonConfigurationReader.getServices();
        String strServices = services.stream()
                .filter(service -> service.getURL().contains(packet.getValue(PatternGroup.PROTOCOL)))
                .map(IService::getAugmentedURL)
                .reduce("", (a, b) -> a + (a.isEmpty() ? "" : " ") + b);

        IProtocolPacket config = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.CONFIG)
                .withGroup(PatternGroup.CONFIG, strServices)
                .build();

        String host = packet.getValue(PatternGroup.HOST);
        int port = Integer.parseInt(packet.getValue(PatternGroup.PORT));

        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            writeEncryptedPacket(out, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Protocol(pattern = ProtocolPattern.NOTIFICATION)
    private void onNotify(Object sender, IProtocolPacket packet) {
        String protocol = packet.getValue(PatternGroup.PROTOCOL);
        List<String> ids = daemonConfigurationReader.getServices()
                .stream().filter(s -> s.getAugmentedURL().contains(protocol))
                .map(IService::getID)
                .collect(Collectors.toList());

        String host = packet.getValue(PatternGroup.HOST);
        int port = Integer.parseInt(packet.getValue(PatternGroup.PORT));

        try(Socket socket = new Socket(host, port);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream())) {

            for (String id : ids) {
                writeEncryptedPacket(out, newStateReq(id));
                execute(socket, readEncryptedPacket(in));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Protocol(pattern = ProtocolPattern.STATE_RESP)
    private void onStateResponse(Object sender, IProtocolPacket packet) {
        List<IService> services = Service.from(packet);
        services.forEach(service -> {
            if (!stateStack.hasService(service)) stateStack.registerService(service);
            stateStack.getStates(service).push(ServiceState.valueOf(packet.getValue(PatternGroup.STATE)));
        });
    }

    private void writeEncryptedPacket(DataOutputStream out, IProtocolPacket packet) throws IOException {
        out.write(AESUtils.encrypt(packet.buildMessage(), daemonConfigurationReader.getAesKey()));
    }

    private IProtocolPacket readEncryptedPacket(DataInputStream in) throws IOException {
        byte[] buffer = new byte[1024];

        byte b;
        int i = 0;
        do {
            b = in.readByte();
            buffer[i] = b;
            ++i;
        } while (b != '\n');

        return ProtocolPacket.from(AESUtils.decrypt(buffer, daemonConfigurationReader.getAesKey()).trim() + "\r\n");
    }

    private IProtocolPacket newStateReq(String id) {
        return new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.STATE_REQ)
                .withGroup(PatternGroup.ID, id)
                .build();
    }
}
