package be.flmr.secmon.daemon;

import be.flmr.secmon.core.multicast.ConnectionBroadcaster;
import be.flmr.secmon.daemon.net.NorthPole;
import be.flmr.secmon.daemon.net.ServiceStateStack;
import be.flmr.secmon.daemon.net.SocketFactory;
import be.flmr.secmon.daemon.net.SouthPole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
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
        ServiceStateStack serviceStateStack = new ServiceStateStack();
        SouthPole southPole = new SouthPole(file, serviceStateStack, SocketFactory::createSocketByConfig);
        // NorthPole northPole = new NorthPole(new ConnectionBroadcaster());
        ExecutorService executor = Executors.newSingleThreadExecutor();

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
