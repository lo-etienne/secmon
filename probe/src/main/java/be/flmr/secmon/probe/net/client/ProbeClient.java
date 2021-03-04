package be.flmr.secmon.probe.net.client;

import be.flmr.secmon.core.net.IClient;
import be.flmr.secmon.core.net.IProtocolPacketSender;
import be.flmr.secmon.core.net.IServer;
import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.ProtocolPacket;
import be.flmr.secmon.core.router.AbstractRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ProbeClient implements IClient, IProtocolPacketSender, AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(ProbeClient.class);

    private final AbstractRouter router;
    private Socket socket;
    private IServer server;

    private PrintWriter out;
    private BufferedReader in;

    public ProbeClient(Socket socket, IServer server, AbstractRouter router) {
        this.server = server;
        this.socket = socket;
        this.router = router;

        try {
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private IProtocolPacket receive() throws IOException {
        String line = in.readLine() + "\r\n";
        return ProtocolPacket.from(line);
    }

    @Override
    public void send(IProtocolPacket packet) {
        out.print(packet.buildMessage());
        out.flush();
    }

    @Override
    public void run() {
        while (!server.isShuttingDown()) {
            try {
                var packet = receive();
                router.execute(this, packet);
            } catch(IllegalArgumentException e) {
                log.error("Le packet reçu ne corresponds à aucun packet", e);
            } catch(SocketException e) {
                log.warn("La connexion du client a été fermée", e);
                break;
            } catch(IOException e) {
                log.error("Erreur lors de la réception de données");
                break;
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }
}