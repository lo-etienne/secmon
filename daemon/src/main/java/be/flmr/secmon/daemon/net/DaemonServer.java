package be.flmr.secmon.daemon.net;

import be.flmr.secmon.daemon.config.IDaemonConfigurationReader;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DaemonServer implements Runnable {

    private Set<DaemonClient> clientList = new HashSet<>();
    private ExecutorService executor;

    private INorthPole northPole;
    private ISouthPole southPole;

    public DaemonServer(final IDaemonConfigurationReader daemonConfigurationReader, final INorthPole northPole, final ISouthPole southPole) {
        executor = Executors.newSingleThreadExecutor();
        this.northPole = northPole;
        this.southPole = southPole;
    }

    @Override
    public void run() {
        executor.execute(northPole);
        executor.execute(southPole);

    }

}
