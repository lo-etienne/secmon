package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.multicast.ConnectionBroadcaster;
import be.flmr.secmon.core.net.IProtocolPacketReceiver;
import be.flmr.secmon.core.net.IService;
import be.flmr.secmon.core.net.ServiceState;
import be.flmr.secmon.core.pattern.*;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Protocol;
import be.flmr.secmon.core.security.Base64AesUtils;
import be.flmr.secmon.daemon.config.DaemonJSONConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Le probe communicator est la partie du Daemon qui communique avec les différentes probes. Lorsqu'une probe envoie
 * un message en multicast, le communicator s'occupe de répondre a ce message (ANNOUNCE -> CURCONFIG ou NOTIFY -> STATEREQ).
 * Dans le premier cas, le communicator ira chercher dans la configuration du daemon les différents services à passer
 * au probe. Dans le second, il enverra une requête de statut et attendra une réponse de la probe. Lorsque cette dernière
 * a envoyé sa réponse, le communicator stock alors l'état d'un des services dans un stack
 */
public class ProbeCommunicator extends AbstractRouter implements INorthPole {
    private static final Logger log = LoggerFactory.getLogger(ProbeCommunicator.class);
    private final ServiceStateStack stateStack;

    private IProtocolPacketReceiver multicast;
    private DaemonJSONConfig daemonJSONConfig;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    private boolean running = true;

    private Socket socket;

    /**
     * Créé une instance de {@code ProbeCommunicator} avec la configuration du daemon et le stack de services
     * @param config la configuration du daemon
     * @param stateStack les stacks des états de services
     */
    public ProbeCommunicator(DaemonJSONConfig config, ServiceStateStack stateStack) {
        super();
        socket = new Socket();
        this.stateStack = stateStack;
        this.multicast = new ConnectionBroadcaster(config.getMulticastAddress(), Integer.parseInt(config.getMulticastPort()), executor);
        this.daemonJSONConfig = config;
    }

    /**
     * Démarre le {@code ProbeCommunicator} en écoutant les différents messages envoyé depuis le Multicast
     */
    @Override
    public void run() {
        log.info("Lancement du pôle nord");
        while (running) {
            IProtocolPacket receivedPacket = multicast.receive();
            executor.execute(() -> execute(receivedPacket.getSourceAddress(), receivedPacket));
        }
    }

    /**
     * Méthode exéctuée par {@link AbstractRouter::execute} lorsqu'un packet de type {@link ProtocolPacket.ANNOUNCE}
     * est passé en paramètre de cette dernière. Celle-ci en particulier traite les message d'annonce des probes. Elle
     * envoie donc la configuration des services à ces dites probes.
     * @param sender l'envoyeur, ici, une instance de {@code InetAddress}
     * @param packet le packet (ANNOUNCE) à traiter
     */
    @Protocol(pattern = ProtocolPattern.ANNOUNCE)
    private void onAnnounce(Object sender, IProtocolPacket packet) {
        log.info("Réception d'un message ANNOUNCE de {}", sender);
        List<IService> services = daemonJSONConfig.getServices();
        String strServices = services.stream()
                .filter(service -> service.getURL().contains(packet.getValue(PatternGroup.PROTOCOL)))
                .map(IService::getAugmentedURL)
                .reduce("", (a, b) -> a + (a.isEmpty() ? "" : " ") + b);

        IProtocolPacket config = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.CONFIG)
                .withGroup(PatternGroup.CONFIG, strServices)
                .build();

        String host = ((InetAddress) sender).getHostAddress();
        int port = Integer.parseInt(packet.getValue(PatternGroup.PORT));

        try (Socket socket = new Socket(host, port)) {
            log.info("Envoi de la configuration {}", config.buildMessage());
            write(config, socket);
        } catch (IOException e) {
            log.error("Erreur lors de l'envoi d'une réponse à ANNOUNCE (CURCONFIG)", e);
        } finally {
            log.info("Déconnexion du probe...");
        }
    }

    /**
     * Méthode exéctuée par {@link AbstractRouter::execute} lorsqu'un packet de type {@link ProtocolPacket.NOTIFICATION}
     * est passé en paramètre de cette dernière. Celle-ci en particulier traite les message de notification des probes.
     * Elle renvoie alors des requêtes d'état vers les probes et attends une réponse d'état de la part des probes.
     * @param sender l'envoyeur, ici, une instance de {@code InetAddress}
     * @param packet le packet (NOTIFICATION) à traiter
     */
    @Protocol(pattern = ProtocolPattern.NOTIFICATION)
    private void onNotify(Object sender, IProtocolPacket packet) {
        log.info("Réception d'un NOTIFY de {}", sender);
        String protocol = packet.getValue(PatternGroup.PROTOCOL);
        List<IService> services = daemonJSONConfig.getServices()
                .stream().filter(s -> s.getURL().contains(protocol))
                .collect(Collectors.toList());

        String host = ((InetAddress) sender).getHostAddress();
        int port = Integer.parseInt(packet.getValue(PatternGroup.PORT));

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout((int) TimeUnit.SECONDS.toMillis(10));
            for (IService service : services) {
                var req = newStateReq(service.getID());
                log.info("Envoi de la requête de statut {}", req.buildMessage());
                write(req, socket);
                execute(socket, read(socket));
            }
        } catch (SocketTimeoutException e) {
            log.warn("La requête d'était n'as pas été répondue par le probe {}: {}", protocol, socket.getInetAddress());
        } catch (IOException e) {
            log.warn("Une erreur s'est produite lors de l'envoi de la requête d'état", e);
        }
    }

    /**
     * Méthode exéctuée par {@link AbstractRouter::execute} lorsqu'un packet de type {@link ProtocolPacket.STATE_RESP}
     * est passé en paramètre de cette dernière. Celle-ci en particulier traite les message de réponse de requête des probes.
     * Lorsqu'un STATE_RESP est renvoyé, alors le {@code ProbeCommunicator} push les états reçu dans le stack du service
     * en question.
     * @param sender l'envoyeur, ici, une instance de {@code Socket}
     * @param packet le packet (STATE_REQ) à traiter
     */
    @Protocol(pattern = ProtocolPattern.STATE_RESP)
    private void onStateResponse(Object sender, IProtocolPacket packet) {
        log.info("Réception d'un STATEREQ de {}", sender);
        daemonJSONConfig.getServices().stream()
                .filter(service -> service.getID().equals(packet.getValue(PatternGroup.ID)))
                .forEach(service -> {
                    if (!stateStack.hasService(service)) stateStack.registerService(service);
                    stateStack.pushState(service, ServiceState.valueOf(packet.getValue(PatternGroup.STATE)));
                });
    }

    /**
     * Lis un {@code IProtocolPacket} depuis un socket et décrypte le message encrypté par AES/GCM
     * @param s le socket censé envoyer le {@code IProtocolPacket}
     * @return le packet lu
     */
    private synchronized IProtocolPacket read(Socket s) {
        try {
            var in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line = in.readLine();
            String decrypted = Base64AesUtils.decrypt(line, daemonJSONConfig.getAesKey());
            return ProtocolPacket.from(decrypted);
        } catch (IOException e) {
            log.error("Le message n'as pas pû être reçu");
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Envoie un {@code IProtocolPacket} vers un socket d'une façon encryptée par AES/GCM
     * @param packet le packet à envoyer
     * @param s le socket en question
     */
    private synchronized void write(IProtocolPacket packet, Socket s) {
        try {
            var out = new PrintWriter(s.getOutputStream());
            String encrypted = Base64AesUtils.encrypt(packet.buildMessage(), daemonJSONConfig.getAesKey());
            out.print(encrypted);
            out.flush();
        } catch (IOException e) {
            log.error("Le message {} n'as pas pû être envoyé", packet.buildMessage());
        }
    }

    /**
     * Méthode helper pour créer une {@code ProtocolPatter.STATE_REQ} facilement
     * @param id l'id du service à "requeter"
     * @return le protocol packet créé
     */
    private IProtocolPacket newStateReq(String id) {
        return new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.STATE_REQ)
                .withGroup(PatternGroup.ID, id)
                .build();
    }

    /**
     * Ferme les différentes ressources du {@code ProbeCommunicator}
     */
    @Override
    public void close() {
        try {
            running = false;
            multicast.close();
            executor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
