package be.flmr.secmon.probe.net.client;

import be.flmr.secmon.core.net.IClient;
import be.flmr.secmon.core.net.IProtocolPacketSender;
import be.flmr.secmon.core.net.IServer;
import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.ProtocolPacket;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.security.Base64AesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * Client chargé de communiquer avec le Moniteur Daemon de manière chiffrée avec AES
 */
public class ProbeClient implements IClient, IProtocolPacketSender, AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(ProbeClient.class);

    private final AbstractRouter router;
    private Socket socket;
    private IServer server;

    private String aesKey;

    private PrintWriter out;
    private BufferedReader in;

    public ProbeClient(Socket socket, IServer server, AbstractRouter router, String aes) {
        this.server = server;
        this.socket = socket;
        this.router = router;

        this.aesKey = aes;

        try {
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recoit un message chiffré avec AES et renvoie un {@code ProtocolPaket} à partir de son texte déchiffré
     * @return Un ProtocolPacket
     * @throws IOException si une erreur survient lors de l'I/O
     */
    private IProtocolPacket receive() throws IOException {
        String line = in.readLine();
        log.debug("Réception du message {}", line);
        return ProtocolPacket.from(Base64AesUtils.decrypt(line, aesKey));
    }

    /**
     * Construit un message à partir d'un {@code ProtocolPacket}, le chiffre avec AES et l'envoie ensuite
     * @param packet Un ProtocolPacket
     */
    @Override
    public void send(IProtocolPacket packet) {
        out.print(Base64AesUtils.encrypt(packet.buildMessage(), aesKey) + "\r\n");
        out.flush();
    }

    /**
     *  Essaye de lire un {@code ProtocolPacket} pour ensuite exécuter la méthode qui lui est liée
     */
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
        if (!socket.isClosed()) {
            log.info("Déconnexion du client - {}", socket.getInetAddress());
            this.socket.close();
        }
    }
}