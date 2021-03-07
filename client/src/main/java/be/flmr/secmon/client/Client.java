package be.flmr.secmon.client;

import be.flmr.secmon.core.net.IProtocolPacketReceiver;
import be.flmr.secmon.core.net.IProtocolPacketSender;
import be.flmr.secmon.core.pattern.*;
import be.flmr.secmon.core.router.AbstractRouter;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

public class Client implements IProtocolPacketSender{
    private PrintStream stream;
    private PrintWriter writer;
    private BufferedReader buffered;
    private ProtocolClient pc;

    public Client(PrintStream stream, String host, String port){
        this.stream = stream;
        this.pc = new ProtocolClient(stream);
        createSSLSocket(host, port);
    }

    private void createSSLSocket(String host, String port) {
        try {
            char[] pwd = "group5".toCharArray();

            KeyStore ks = KeyStore.getInstance("PKCS12");
            InputStream is = new FileInputStream("C:\\Users\\Robin\\Desktop\\cours reseaux\\group5.monitor.p12");
            ks.load(is, pwd);

            String algo = KeyManagerFactory.getDefaultAlgorithm();
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algo);
            kmf.init(ks, pwd);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(algo);
            tmf.init(ks);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

            SSLSocketFactory sslSocketFactory = context.getSocketFactory();
            SSLSocket socket  = (SSLSocket) sslSocketFactory.createSocket(host,Integer.parseInt(port));

            socket.startHandshake();

            this.writer = new PrintWriter(socket.getOutputStream());
            this.buffered = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            /*SocketFactory basicSocketFactory = SocketFactory.getDefault();
            Socket s = basicSocketFactory.createSocket(host,Integer.parseInt(port));
            SSLSocketFactory tlsSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            s = tlsSocketFactory.createSocket(s, host, Integer.parseInt(port), true);
            return (SSLSocket) s;*/
        } catch (IOException | KeyStoreException e) {
            throw new RuntimeException("Connection SSl non reussit",e);
        } catch (CertificateException | KeyManagementException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new RuntimeException("Erreur non traiter",e);
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
            String str = buffered.readLine();
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
