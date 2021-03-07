package be.flmr.secmon.daemon;

import be.flmr.secmon.core.multicast.ConnectionBroadcaster;
import be.flmr.secmon.daemon.config.DaemonJSONConfig;
import be.flmr.secmon.daemon.config.DaemonJSONConfigurationReader;
import be.flmr.secmon.daemon.net.NorthPole;
import be.flmr.secmon.daemon.net.ServiceStateStack;
import be.flmr.secmon.daemon.net.SocketFactory;
import be.flmr.secmon.daemon.net.SouthPole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        SouthPole southPole = new SouthPole(daemonJSONConfig, serviceStateStack, SocketFactory::createSocketByConfig);
        NorthPole northPole = new NorthPole(daemonJSONConfig, serviceStateStack);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.execute(northPole);
        executor.execute(southPole);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Début de la fermeture du serveur ...");
            southPole.close();
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

class NorthTest {
    public static void main(String[] args) {
        final File file = new File(Objects.requireNonNull(DaemonApp.class.getClassLoader().getResource("monitor.json")).getFile());
        DaemonJSONConfig daemonJSONConfig;

        try (DaemonJSONConfigurationReader daemonJSONConfigurationReader = new DaemonJSONConfigurationReader(new FileReader(file))) {
            daemonJSONConfig = daemonJSONConfigurationReader.read();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ServiceStateStack serviceStateStack = new ServiceStateStack();
        NorthPole northPole = new NorthPole(daemonJSONConfig, serviceStateStack);

        northPole.run();
    }
}