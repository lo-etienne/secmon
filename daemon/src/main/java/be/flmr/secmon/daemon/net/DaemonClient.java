package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.IClient;
import be.flmr.secmon.core.net.IProtocolPacketReceiver;
import be.flmr.secmon.core.net.IProtocolPacketSender;
import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.ProtocolPacket;
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


    /**
     * Crée une instance de {@code DaemonClient} avec un socket et un AbstractRouter
     * @param socket le socket qui permettra la communication
     * @param abstractRouter permet l'utiliser des annotations Protocol
     */
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

    /**
     * Méthode qui permet d'envoyer le message que contient un {@code IProcotolPacket} en
     * utilisant {@link PrintWriter::print}
     * @param packet objet IProtocolPacket
     */
    @Override
    public void send(IProtocolPacket packet) {
        printWriter.print(packet.buildMessage());
        printWriter.flush();
    }

    /**
     * Méthode qui reçoit un {@code String} depuis un BufferedReader. Si ce dernier est null, alors
     * cela veut dire que le client est déconnecté. S'il ne l'est pas on construit, à l'aide du {@code String}
     * et du {@code Socket}, un {@code ProtocolPacket} que l'on renvoie
     * @return le packet reçu
     */
    @Override
    public IProtocolPacket receive() {
        try {
            final String message = bufferedReader.readLine();
            if (message == null) {
                LOG.info("Client déconnecté");
                disconnected = true;
            } else {
                LOG.info("Message reçu de {} : {}", socket.getInetAddress(), message);
                return ProtocolPacket.from(message + "\r\n");
            }
        } catch (SocketException | SSLException socketException) {
            LOG.info("Client déconnecté");
            disconnected = true;
        } catch (Exception exception) {
            LOG.warn("Problème à la réception du message", exception);
        }
        return null;
    }

    /**
     * Méthode qui permet, tant que le client n'est pas déconnecté, d'exécuter {@link AbstractRouter::execute}
     */
    @Override
    public void run() {
        while(!disconnected) {
            IProtocolPacket packet = this.receive();
            if(!(packet == null)) {
                abstractRouter.execute(this, packet);
            }
        }
    }

    /**
     * Méthode qui permet de fermer le {@code Socket}
     */
    @Override
    public void close() {
        try {
            this.socket.close();
        } catch (Exception ignored) {}

    }
}
