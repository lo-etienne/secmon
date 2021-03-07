package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.*;
import be.flmr.secmon.core.pattern.*;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Protocol;
import be.flmr.secmon.daemon.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;

public class SouthPole extends AbstractRouter implements ISouthPole, AutoCloseable, IServer {

    private static final Logger log = LoggerFactory.getLogger(SouthPole.class);

    private DaemonJSONConfig config;
    private IDaemonConfigurationWriter daemonConfigurationWriter;
    private ServiceStateStack stateStack;
    private ServerSocket serverSocket;
    private ExecutorService executorService;


    private Map<DaemonClient, Future<?>> clients = new HashMap<>();

    public SouthPole(final File config,
                     final ServiceStateStack stateStack,
                     final Function<DaemonJSONConfig, ? extends ServerSocket> serverSocketSupplier) {
        super();
        initDaemonConfiguration(config);
        this.stateStack = stateStack;
        for (var service : this.config.getServices()) {
            stateStack.registerService(service);
        }
        this.serverSocket = serverSocketSupplier.apply(this.config);
        executorService = Executors.newFixedThreadPool(10);
    }

    private void initDaemonConfiguration(File config) {
        try (var daemonJSONConfigurationReader = new DaemonJSONConfigurationReader(new FileReader(config)) ){
            this.config = daemonJSONConfigurationReader.read();
            this.daemonConfigurationWriter = new DaemonJSONConfigurationWriter(new FileWriter(config));
        } catch (Exception e) {
            throw new IllegalArgumentException("Le fichier spécifié est introuvable", e);
        }
    }

    @Override
    public void run() {
        executorService.execute(this::listenForConnections);
    }

    @Override
    public void close() {
        try {
            log.info("Sauvegarde des données");
            daemonConfigurationWriter.write(this.config);
            daemonConfigurationWriter.close();
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
     * Méthode qui permet d'ajouter un service à la configuration du daemon
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
            daemonConfigurationWriter.write(config);
            IProtocolPacket packetRespond = createAddRespond(ProtocolPattern.ADD_SERVICE_RESP_OK, "Ajout effectué");
            packetSender.send(packetRespond);

        }
    }

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

    @Override
    public boolean isShuttingDown() {
        return false;
    }
}
