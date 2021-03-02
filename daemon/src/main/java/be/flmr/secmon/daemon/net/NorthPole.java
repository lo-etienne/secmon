package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.IProtocolPacketReceiver;
import be.flmr.secmon.core.net.IProtocolPacketSender;
import be.flmr.secmon.core.pattern.*;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Protocol;
import be.flmr.secmon.daemon.config.IDaemonConfigurationReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class NorthPole extends AbstractRouter implements INorthPole {
    private static final Logger log = LoggerFactory.getLogger(NorthPole.class);

    private IProtocolPacketReceiver multicast;
    private IDaemonConfigurationReader daemonConfigurationReader;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public NorthPole(IProtocolPacketReceiver multicast, IDaemonConfigurationReader daemonConfigurationReader) {
        super();
        this.multicast = multicast;
        this.daemonConfigurationReader = daemonConfigurationReader;
    }

    boolean isConnected() {
        return true;
    }

    @Override
    public void run() {
        IProtocolPacket receivedPacket = multicast.receive();
        executor.execute(() -> execute(null, receivedPacket));
    }

    @Protocol(pattern = ProtocolPattern.ANNOUNCE)
    private void onAnnounce(Object sender, IProtocolPacket packet) {
        List<String> services = daemonConfigurationReader.getServices();
        String strServices = services.stream()
                .filter(service -> service.contains(packet.getValue(PatternGroup.PROTOCOL)))
                .reduce("", (a, b) -> a + (a.isEmpty() ? "" : " ") + b);

        IProtocolPacket config = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.CONFIG)
                .withGroup(PatternGroup.CONFIG, strServices)
                .build();

        String host = packet.getValue(PatternGroup.HOST);
        int port = Integer.parseInt(packet.getValue(PatternGroup.PORT));

        try (Socket socket = new Socket(host, port);
             PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
            writer.print(config.buildMessage());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Protocol(pattern = ProtocolPattern.NOTIFICATION)
    private void onNotify(Object sender, IProtocolPacket packet) {
        String protocol = packet.getValue(PatternGroup.PROTOCOL);
        List<String> ids = daemonConfigurationReader.getServices()
                .stream().filter(s -> s.contains(protocol))
                .map(s -> PatternUtils.extractGroup(s, PatternGroup.AUGMENTEDURL, "ID"))
                .collect(Collectors.toList());

        String host = packet.getValue(PatternGroup.HOST);
        int port = Integer.parseInt(packet.getValue(PatternGroup.PORT));

        try(Socket socket = new Socket(host, port);
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            for (String id : ids) {
                writer.write(newStateReq(id).buildMessage());
                writer.flush();

                execute(socket, ProtocolPacket.from(reader.readLine() + "\r\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Protocol(pattern = ProtocolPattern.STATE_RESP)
    private void onStateResponse(Object sender, IProtocolPacket packet) {
        // TODO: Interprêter la réponse
        System.out.println(packet);
    }

    private IProtocolPacket newStateReq(String id) {
        return new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.STATE_REQ)
                .withGroup(PatternGroup.ID, id)
                .build();
    }
}
