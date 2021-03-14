package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.*;
import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.PatternGroup;
import be.flmr.secmon.core.pattern.ProtocolPacketBuilder;
import be.flmr.secmon.core.pattern.ProtocolPattern;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Protocol;
import be.flmr.secmon.daemon.config.DaemonJSONConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * Le SouthPole est la partie du Daemon qui communique avec les différents clients. Lorsqu'un client
 * envoie un message au Daemon, le SouthPole va répondre à ce message.
 * Le SouthPole s'occupe de répondre à 3 types de message :
 * 1) ADD_SERVICE_REQ
 * Permettra d'ajouter un service à la configuration du daemon
 * 2) STATE_SERVICE_REQ
 * Permettra d'obtenir l'état du service demandé par le client
 * 3) LIST_SERVICE_REQ
 * Permettra de lister les différents services de la configuration du Daemon
 */
public class ClientCommunicator extends AbstractRouter implements ISouthPole, AutoCloseable, IServer {

    private static final Logger log = LoggerFactory.getLogger(ClientCommunicator.class);

    private DaemonJSONConfig config;
    private ServiceStateStack stateStack;
    private ServerSocket serverSocket;
    private ExecutorService executorService;


    private Map<DaemonClient, Future<?>> clients = new HashMap<>();

    /**
     * Crée une instance de {@code SouthPole} avec la configuration du daemon,
     * le stack de services, et une interface fonctionnelle
     * @param config configuration du daemon
     * @param stateStack les stacks des états des services
     * @param serverSocketSupplier fonction qui accepte un argument et qui produit un résultat grâce à {@link Function::apply}
     */
    public ClientCommunicator(final DaemonJSONConfig config,
                              final ServiceStateStack stateStack,
                              final Function<DaemonJSONConfig, ? extends ServerSocket> serverSocketSupplier) {
        super();
        this.config = config;
        this.stateStack = stateStack;
        for (var service : this.config.getServices()) {
            stateStack.registerService(service);
        }
        this.serverSocket = serverSocketSupplier.apply(this.config);
        executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * Méthode qui permet d'exécuter le SouthPole grâce à {@link ExecutorService::execute},
     * qui exécutera la méthode {@link ClientCommunicator ::listenForConnections}
     */
    @Override
    public void run() {
        log.info("Southpole lancé");
        executorService.execute(this::listenForConnections);
    }

    /**
     * Méthode qui permet de déconnecter les clients, de fermer les threads et
     * de fermer le serveur
     */
    @Override
    public void close() {
        try {
            log.info("Déconnexion des clients");
            clients.keySet().forEach(DaemonClient::close);
            log.info("Fermeture des threads");
            executorService.shutdown();
            log.info("Fermeture du serveur");
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui permet d'ajouter un {@link Service} à la configuration du daemon lorsqu'un
     * packet de type {@code ProtocolPattern.ADD_SERVICE_REQ} est passé en paramètre.
     * Si une erreur se produit (ici, si aucun service n'est fournit par le paclet), un message
     * d'erreur sera affiché.
     * Sinon, le service sera ajouté (ou remplacé si ce dernier existait déjà dans la configuration du daemeon)
     *
     * @param sender n'est pas utilisé dans cette méthode
     * @param packet contient les données du service à ajouter
     */
    @Protocol(pattern = ProtocolPattern.ADD_SERVICE_REQ)
    private void onAddServiceRequest(final Object sender, final IProtocolPacket packet) {
        IProtocolPacketSender packetSender = (IProtocolPacketSender) sender;
        List<IService> services = Service.from(packet);
        if (services.isEmpty()) {
            createAddRespond(ProtocolPattern.ADD_SERVICE_RESP_ERR, "Erreur durant l'ajout");
        }
        for (IService service : services) {
            log.info("Service reçu");
            if (config.hasService(service)) {
                config.removeService(service);
            }
            config.addService(service);
            stateStack.registerService(service);
            IProtocolPacket packetRespond = createAddRespond(ProtocolPattern.ADD_SERVICE_RESP_OK, "Ajout effectué");
            packetSender.send(packetRespond);

        }
    }

    /**
     * Méthode qui permet, en fonction d'un ProtocolPattern et d'un String, de construire
     * un ProtocolPacket et de le renvoyer
     * @param addServiceRespond pattern du protocol
     * @param message message que contiendra le ProtocolPacket
     * @return un ProtocolPacket contenant le message
     */
    private IProtocolPacket createAddRespond(final ProtocolPattern addServiceRespond, final String message) {
        return new ProtocolPacketBuilder()
                .withPatternType(addServiceRespond)
                .withGroup(PatternGroup.OPTIONALMESSAGE, message)
                .build();
    }

    /**
     * Méthode qui, une fois que l'état d'un service est demandé, récupère le service en question depuis la configuration du daemon et, si l'historique des états contient
     * le dit service, renvoie son dernier état connu.
     *
     * @param sender correspond au client qui demande l'état du service
     * @param packet correspond au packet qui contient l'id du service
     */
    @Protocol(pattern = ProtocolPattern.STATE_SERVICE_REQ)
    private void onStateRequest(final Object sender, final IProtocolPacket packet) {
        String id = packet.getValue(PatternGroup.ID);
        Optional<IService> optionalService = config.getServices()
                .stream()
                .filter(s -> s.getID().equals(id)).findFirst();
        if(optionalService.isPresent()) {
            IService service = optionalService.get();
            if (stateStack.hasService(service)) {
                ServiceState serviceState = stateStack.getLastState(service);
                IProtocolPacketSender packetSender = (IProtocolPacketSender) sender;
                IProtocolPacket packetRespond = new ProtocolPacketBuilder().withPatternType(ProtocolPattern.STATE_SERVICE_RESP)
                        .withGroup(PatternGroup.ID, id)
                        .withGroup(PatternGroup.URL, service.getURL())
                        .withGroup(PatternGroup.STATE, serviceState.name())
                        .build();
                packetSender.send(packetRespond);
            }
        }
    }

    /**
     * Méthode qui renvoie l'ensemble des services au client
     *
     * @param sender correspond au client qui demande la liste des services
     * @param packet n'est pas utilisé dans cette méthode
     */
    @Protocol(pattern = ProtocolPattern.LIST_SERVICE_REQ)
    private void onListServiceRequest(final Object sender, final IProtocolPacket packet) {
        List<IService> services = config.getServices();
        StringBuilder srvList = new StringBuilder(services.get(0).getID());
        for (int i = 1; i < services.size(); i++) {
            srvList.append(" ").append(services.get(i).getID());
        }
        IProtocolPacketSender packetSender = (IProtocolPacketSender) sender;
        IProtocolPacket packetRespond = new ProtocolPacketBuilder().withPatternType(ProtocolPattern.LIST_SERVICE_RESP)
                .withGroup(PatternGroup.SRVLIST, srvList.toString())
                .build();
        packetSender.send(packetRespond);
    }

    /**
     * Méthode qui permet, tant que le SouthPole est en cours d'exécution, d'écouter les
     * connexions des clients.
     * Pour ce faire, on récupère un {@code Socket} depuis le {@code ServerSocket} et ce grâce
     * à la méthode {@link ServerSocket::accept}.
     * Une fois cela fait, on vérifie si la configuration est sécurisée :
     * Si c'est le cas alors on cast le {@code Socket} en {@code SSLSocket} et on exécuter la méthode
     * {@link SSLSocket::startHandshake}
     * Ensuite, on crée un nouveau client à partir du {@code Socket} et de la classe qui fera office de {@code AbstractRouter}
     * On exécute la méthode {@link ExecutorService::submit} en fournissant le client et on le push dans la liste
     */
    @Override
    public void listenForConnections() {
        while (!isShuttingDown()) {
            try {
                Socket socket = serverSocket.accept();
                log.info("Client connecté");
                // Handshake nécessaire si TLS (NB : Handshake TCP géré par Java)
                if(config.isTls()) {
                    ((SSLSocket) socket).startHandshake();
                }
                DaemonClient client = new DaemonClient(socket, this);
                Future<?> future = executorService.submit(client);
                clients.put(client, future);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Méthode qui permet de récuper un false pour dire que l'exécution du SouthPole
     * n'a pas pris fin
     * @return false
     */
    @Override
    public boolean isShuttingDown() {
        return false;
    }
}
