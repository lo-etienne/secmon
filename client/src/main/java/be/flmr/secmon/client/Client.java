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

public class Client implements IProtocolPacketSender{
    private PrintStream stream;
    private PrintWriter writer;
    private BufferedReader buffered;
    private ProtocolClient pc;
    private SSLSocket socket;

    public Client(PrintStream stream, String host, String port){
        this.stream = stream;
        this.pc = new ProtocolClient(stream);
        createSSLSocket(host, port);
    }

    private void createSSLSocket(String host, String port) {
        try {
            char[] pwd = "group5".toCharArray();

            InputStream is = new FileInputStream("C:\\Users\\Robin\\Desktop\\cours reseaux\\GodSwilaTrustMeIntermediateCA.crt");
            InputStream ist = new FileInputStream("C:\\Users\\Robin\\Desktop\\cours reseaux\\GodSwilaTrustMeRootCA.crt");

            X509Certificate ca = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new BufferedInputStream(is));
            X509Certificate ce = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new BufferedInputStream(ist));

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

    public void addSrvReq(String group){
        IProtocolPacket packet = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.ADD_SERVICE_REQ)
                .withGroup(PatternGroup.AUGMENTEDURL,group)
                .build();
        send(packet);
        receive();
    }

    public void listSrvReq(){
        IProtocolPacket packet = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.LIST_SERVICE_REQ)
                .build();
        send(packet);
        receive();
    }

    public void stateSrvReq(String group){
        IProtocolPacket packet = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.STATE_SERVICE_REQ)
                .withGroup(PatternGroup.ID,group)
                .build();
        send(packet);
        receive();
    }

    private void receive(){
        try {
            String str = buffered.readLine() + "\r\n";
            IProtocolPacket packet = ProtocolPacket.from(str);
            pc.execute(this,packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(IProtocolPacket packet) {
        writer.print(packet.buildMessage());
        writer.flush();
    }
}
