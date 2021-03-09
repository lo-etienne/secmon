package be.flmr.secmon.client;

import be.flmr.secmon.core.net.IProtocolPacketSender;
import be.flmr.secmon.core.pattern.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Class qui creer la connection entre lui-meme et un serveur, et permet de creer et envoie des ProtocolPacket au serveur, et attend une reponse precise du serveur
 */
public class Client implements IProtocolPacketSender{
    private PrintStream stream;
    private PrintWriter writer;
    private BufferedReader buffered;
    private ProtocolClient pc;
    private SSLSocket socket;

    /**
     * Constructeur de Client
     * @param stream permetant de creer la class qui affichera les reponse du serveur
     * @param host hote pour creer un socket
     * @param port port pour creer un socket
     */
    public Client(PrintStream stream, String host, String port){
        this.stream = stream;
        this.pc = new ProtocolClient(stream);
        createSSLSocket(host, port);
    }

    /**
     * Methode qui crée un SSLSocket avec les certificat root et intermediate
     * @param host l'hote du SSLSocket
     * @param port le port du SSLSocket
     */
    private void createSSLSocket(String host, String port) {
        try {
            char[] pwd = "group5".toCharArray();

            InputStream is = getClass().getClassLoader().getResourceAsStream("GodSwilaTrustMeIntermediateCA.crt");
            InputStream ist = getClass().getClassLoader().getResourceAsStream("GodSwilaTrustMeRootCA.crt");

            X509Certificate ca = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);
            X509Certificate ce = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(ist);

            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null,null);
            ks.setCertificateEntry("1", ca);
            ks.setCertificateEntry("2", ce);

            TrustManagerFactory trust = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trust.init(ks);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null,trust.getTrustManagers(),new SecureRandom());

            SSLSocketFactory factory = context.getSocketFactory();
            socket = (SSLSocket) factory.createSocket(host, Integer.parseInt(port));
            writer = new PrintWriter(socket.getOutputStream());
            buffered = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException | KeyStoreException e) {
            throw new RuntimeException("Connection SSl non reussit",e);
        } catch (CertificateException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur non traiter",e);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    /**
     * Methode qui crée et envoie un ProtocolPacket(configure pour le contenu pour que le packet ai le type ADD_SERVICE_REQ et
     * que le group du packet soit le contenue du paramettre) via la methode send et ensuite attend la reponse
     * @param group Un String representant le contenu du ProtocolPacket
     */
    public void addSrvReq(String group){
        IProtocolPacket packet = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.ADD_SERVICE_REQ)
                .withGroup(PatternGroup.AUGMENTEDURL,group)
                .build();
        send(packet);
        receive();
    }

    /**
     * Methode qui crée et envoie un ProtocolPacket(configure pour le contenu pour que le packet ai le type LIST_SERVICE_REQ) via la methode send
     * et ensuite attend la reponse
     */
    public void listSrvReq(){
        IProtocolPacket packet = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.LIST_SERVICE_REQ)
                .build();
        send(packet);
        receive();
    }

    /**
     * Methode qui crée et envoie un ProtocolPacket(configure pour le contenu pour que le packet ai le type STATE_SERVICE_REQ et
     * que le group du packet soit le contenue du paramettre) via la methode send et ensuite attend la reponse
     * @param group Un String representant le contenu du ProtocolPacket
     */
    public void stateSrvReq(String group){
        IProtocolPacket packet = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.STATE_SERVICE_REQ)
                .withGroup(PatternGroup.ID,group)
                .build();
        send(packet);
        receive();
    }

    /**
     * Methode permetant de recupere une reponse d'un buffered et de l'afficher via la Methode execute de ProtocolClient
     */
    private void receive(){
        try {
            String str = buffered.readLine() + "\r\n";
            IProtocolPacket packet = ProtocolPacket.from(str);
            pc.execute(this,packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche le ProtocolPacket du paramettre au writer de la class et flush
     * @param packet
     */
    @Override
    public void send(IProtocolPacket packet) {
        writer.print(packet.buildMessage());
        writer.flush();
    }
}
