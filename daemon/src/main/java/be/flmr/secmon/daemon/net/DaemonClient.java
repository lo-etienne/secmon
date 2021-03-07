package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.IClient;
import be.flmr.secmon.core.net.IProtocolPacketReceiver;
import be.flmr.secmon.core.net.IProtocolPacketSender;
import be.flmr.secmon.core.pattern.*;
import be.flmr.secmon.core.router.AbstractRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;
import java.net.SocketException;


public class DaemonClient implements AutoCloseable, IClient, IProtocolPacketSender, IProtocolPacketReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(DaemonClient.class);

    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private AbstractRouter abstractRouter;
    private boolean disconnected;



    public DaemonClient(final Socket socket, final AbstractRouter abstractRouter)  {
        try {
            this.socket = socket;
            printWriter = new PrintWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.abstractRouter = abstractRouter;
        } catch (IOException ioException) {
            LOG.error("Problème lors de la création du client Daemon");
            throw new RuntimeException(ioException);
        }
    }

    @Override
    public void send(IProtocolPacket packet) {
        printWriter.print(packet.buildMessage());
        printWriter.flush();
    }

    @Override
    public IProtocolPacket receive() {
        try {
            final String message = bufferedReader.readLine() + "\r\n";
            LOG.info("Message reçu de {} : {}", socket.getInetAddress(), message);
            IProtocolPacket protocolPacket = ProtocolPacket.from(message);
            return protocolPacket;
        } catch (SocketException | SSLException socketException) {
            LOG.info("Client déconnecté");
            disconnected = true;
        } catch (Exception exception) {
            LOG.warn("Problème à la réception du message", exception);
        }
        return null;
    }

    @Override
    public void run() {
        while(!disconnected) {
            IProtocolPacket packet = this.receive();
            if(!(packet == null)) {
                abstractRouter.execute(this, packet);
            }
        }
    }

    @Override
    public void close() {
        try {
            this.socket.close();
        } catch (Exception e) {}

    }
}
