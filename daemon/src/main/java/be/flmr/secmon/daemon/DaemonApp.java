package be.flmr.secmon.daemon;

import be.flmr.secmon.daemon.config.DaemonJSONConfig;
import be.flmr.secmon.daemon.config.DaemonJSONConfigurationReader;
import be.flmr.secmon.daemon.net.ProbeCommunicator;
import be.flmr.secmon.daemon.net.ServiceStateStack;
import be.flmr.secmon.daemon.net.SocketFactory;
import be.flmr.secmon.daemon.net.ClientCommunicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Il s'agit de la classe qui permettra d'exécuter le Daemon
 */
public class DaemonApp {

    private static final Logger LOG = LoggerFactory.getLogger(DaemonApp.class);

    public static void main(String[] args) {
        // LIRE LE CONFIG POUR LE NORTHPOLE
        final File file = new File(Objects.requireNonNull(DaemonApp.class.getClassLoader().getResource("monitor.json")).getFile());
        DaemonJSONConfig daemonJSONConfig;

        try (DaemonJSONConfigurationReader daemonJSONConfigurationReader = new DaemonJSONConfigurationReader(new FileReader(file))) {
            daemonJSONConfig = daemonJSONConfigurationReader.read();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ServiceStateStack serviceStateStack = new ServiceStateStack();
        ClientCommunicator clientCommunicator = new ClientCommunicator(daemonJSONConfig, serviceStateStack, SocketFactory::createSocketByConfig);
        ProbeCommunicator probeCommunicator = new ProbeCommunicator(daemonJSONConfig, serviceStateStack);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.execute(probeCommunicator);
        executor.execute(clientCommunicator);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Début de la fermeture du serveur ...");
            clientCommunicator.close();
        }));
        Scanner scanner = new Scanner(System.in);
        String line;
        // Boucle while + Scanner.nextLine().equals() ... comme condition does not loop
        while (true) {
            line = scanner.nextLine();
            if (line.equals("quit")) {
                executor.shutdown();
                System.exit(0);
            }
        }
    }

}