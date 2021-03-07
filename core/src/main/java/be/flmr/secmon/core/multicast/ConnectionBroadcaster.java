package be.flmr.secmon.core.multicast;

import be.flmr.secmon.core.net.IIntervalProtocolPacketSender;
import be.flmr.secmon.core.net.IProtocolPacketReceiver;
import be.flmr.secmon.core.net.IProtocolPacketSender;
import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.ProtocolPacket;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class Test {
    public static void main(String[] args) {
        ConnectionBroadcaster connectionBroadcasterSender = new ConnectionBroadcaster("224.50.50.50", 60150);
        ProtocolPacket packet = ProtocolPacket.from("NOTIFY https 60150\r\n");
        connectionBroadcasterSender.send(packet);
    }
}

public class ConnectionBroadcaster implements IProtocolPacketReceiver, IProtocolPacketSender, IIntervalProtocolPacketSender, AutoCloseable {


    private final static Logger log = LoggerFactory.getLogger(ConnectionBroadcaster.class);

    private final InetAddress group;
    private final MulticastSocket socket;
    private ScheduledExecutorService executor;

    public static void main(String[] args) {
        ConnectionBroadcaster connectionBroadcasterReceiver = new ConnectionBroadcaster("224.50.50.50", 60150);
        IProtocolPacket packet = connectionBroadcasterReceiver.receive();
        System.out.println(packet);
    }

    public ConnectionBroadcaster(final String multicastAddress, final int multicastPort) {
        this(multicastAddress, multicastPort, 1);
    }

    /**
     * Constructeur de broadcaster multicast avec un paramètre ScheduledExecutorService
     * @param multicastAddress correspond à l'adresse du multicast
     * @param multicastPort correspond au port du multicast
     * @param executor ScheduledExecutorService
     */
    public ConnectionBroadcaster(final String multicastAddress, final int multicastPort, final ScheduledExecutorService executor) {
        try {
            group = InetAddress.getByName(multicastAddress);
            socket = new MulticastSocket(multicastPort);
        } catch (IOException ex) {
            throw new RuntimeException("ConnectionBroadcaster : une erreur est survenue pendant la création d'un broadcaster");
        }
    }


    /**
     * Constructeur d'un broadcaster multicast sans paramètre ScheduledExecutorService, ce dernier sera initialisé dans le constructeur
     * @param multicastAddress correspond à l'addresse du multicast
     * @param multicastPort correspond au port du multicast
     * @param threadsNumber correspond au nombre de thread nécessaire, si =1 alors un seul thread sinon une piscine de thread
     */
    public ConnectionBroadcaster(final String multicastAddress, final int multicastPort, final int threadsNumber) {
        try {
            group = InetAddress.getByName(multicastAddress);
            socket = new MulticastSocket(multicastPort);
            executor = (threadsNumber == 1) ? Executors.newSingleThreadScheduledExecutor() : Executors.newScheduledThreadPool(threadsNumber);
        } catch (IOException ex) {
            throw new RuntimeException("ConnectionBroadcaster : une erreur est survenue pendant la création d'un broadcaster");
        }
    }

    /**
     * Méthode qui permet de recevoir un ProtocolPacket contenant un message fournit par un DatagramPacket
     * @return un ProtocolPacket
     */
    @Override
    public IProtocolPacket receive() {
            try {
                final byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.joinGroup(group);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                socket.leaveGroup(group);
                return ProtocolPacket.from(new String(packet.getData(), StandardCharsets.UTF_8).trim() + "\r\n", address);
            } catch (IOException ioException) {
                log.warn("MulticastPacket : le message n'a pas été reçu", ioException);
                throw new RuntimeException("MulticastPacket : le message n'a pas été reçu", ioException);
            }
    }

    /**
     * Méthode qui permet d'envoyer un message à l'aide d'un DatagramPacket et un MulticastSocket
     * @param message correspond au message à envoyer
     */
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

    /**
     * Méthode qui permet d'envoyer le message que contient un packet
     * @param packet objet IProtocolPacket qui contient le message
     */
    @Override
    public void send(IProtocolPacket packet) {
        sendMessage(packet.buildMessage());
    }

    /**
     * Méthode qui permet d'envoyer le message que contient un packet avec une intervalle donnée
     * @param packet objet IProtocolPacket qui contient le message
     * @param timeOut temps d'attente exprimé en long
     * @param unit unité de temps qui sera utilisé pour le délai
     * @return un ScheduledFuture, nécessaire pour cancel les threads si une probe s'arrête
     */
    @Override
    public ScheduledFuture<?> sendWithInterval(IProtocolPacket packet, long timeOut, TimeUnit unit) {
        return executor.scheduleWithFixedDelay(() -> {
            sendMessage(packet.buildMessage());
        }, 0, timeOut, unit);
    }

    @Override
    public void close() {
        executor.shutdown();
    }
}