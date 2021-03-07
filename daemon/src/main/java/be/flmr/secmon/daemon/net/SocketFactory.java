package be.flmr.secmon.daemon.net;

import be.flmr.secmon.daemon.config.DaemonJSONConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.*;
import java.security.cert.CertificateException;

public class SocketFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SocketFactory.class);

    public static ServerSocket securedSocket(final DaemonJSONConfig config) {
        try (FileInputStream inputStream = new FileInputStream(config.getCertificatePath())) {
            final char[] certificate_password = config.getCertificatePassword().toCharArray();

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(inputStream, certificate_password);

            String algorithm = KeyManagerFactory.getDefaultAlgorithm();

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(algorithm);
            keyManagerFactory.init(keyStore, certificate_password);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

            return (SSLServerSocket) sslServerSocketFactory.createServerSocket(Integer.parseInt(config.getClientPort()));
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ServerSocket unsecuredSocket(final DaemonJSONConfig config) {
        try {
            return new ServerSocket(Integer.parseInt(config.getClientPort()));
        } catch (IOException ioException) {
            LOG.error("Problème pendant la création du socket", ioException);
        }
        return null;
    }

    public static ServerSocket createSocketByConfig(final DaemonJSONConfig config) {
        return config.isTls() ? securedSocket(config) : unsecuredSocket(config);
    }

}
