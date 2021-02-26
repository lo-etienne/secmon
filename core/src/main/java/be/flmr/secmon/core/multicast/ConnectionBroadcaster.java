package be.flmr.secmon.core.multicast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;


public class ConnectionBroadcaster implements IBroadcasterReceiver, IBroadcasterSender {


    private final static Logger log = LoggerFactory.getLogger(ConnectionBroadcaster.class);

    private final InetAddress group;
    private final MulticastSocket socket;
    private final ExecutorService executor;

    public static void main(String[] args) {
        ConnectionBroadcaster broadcaster = new ConnectionBroadcaster("224.50.50.50", 60150, 2);
        broadcaster.send("Hello!");
    }

    public ConnectionBroadcaster(final String multicastAddress, final int multicastPort) {
        this(multicastAddress, multicastPort, 1);
    }

    public ConnectionBroadcaster(final String multicastAddress, final int multicastPort, final int threadsNumber) {
        try {
            group = InetAddress.getByName(multicastAddress);
            socket = new MulticastSocket(multicastPort);
            executor = (threadsNumber == 1) ? Executors.newSingleThreadExecutor() : Executors.newFixedThreadPool(threadsNumber);
        } catch (IOException ex) {
            throw new RuntimeException("ConnectionBroadcaster : une erreur est survenue pendant la création d'un broadcaster");
        }
    }

    @Override
    public Future<String> receive() {
        return executor.submit(() -> {
            try {
                log.info("Début de la réception");
                final byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 60150);
                log.info("Entrée dans socket.receive");
                socket.receive(packet);
                log.info("Message reçu");
                return new String(packet.getData(), StandardCharsets.UTF_8);
            } catch (IOException ioException) {
                log.warn("MulticastPacket : le message n'a pas été reçu", ioException);
                throw new RuntimeException("MulticastPacket : le message n'a pas été reçu", ioException);
            }
        });
    }

    @Override
    public void send(final String message) {
        executor.execute(() -> sendMessage(message));
    }

    @Override
    public void sendWithInterval(final String message, final long timeOut, final TimeUnit unit) {
        executor.execute(() -> {
            try {
                while (!Thread.interrupted()) {
                    sendMessage(message);
                    executor.awaitTermination(timeOut, unit);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void sendMessage(final String message) {
        try {
            final byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, group, 60150);
            packet.setAddress(group);
            socket.send(packet);
            log.info("Le message a été envoyé");
        } catch (IOException ex) {
            log.error("Erreur Datagram : le message n'a pas été envoyé", ex);
        }
    }
}