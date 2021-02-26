package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.IServer;
import be.flmr.secmon.daemon.config.IDaemonConfigurationReader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DaemonServer implements Runnable, IServer {

    private Set<DaemonClient> clientList = new HashSet<>();
    private ServerSocket serverSocket;
    private ExecutorService executor;

    private INorthPole northPole;
    private ISouthPole southPole;

    public DaemonServer(final IDaemonConfigurationReader daemonConfigurationReader, final INorthPole northPole, final ISouthPole southPole) {
        serverSocket = SocketFactory.unsecuredSocket(Integer.parseInt(daemonConfigurationReader.getClientPort()));
        executor = Executors.newSingleThreadExecutor();
        this.northPole = northPole;
        this.southPole = southPole;
    }

    @Override
    public void run() {
        executor.execute(this::listenForConnections);

    }

    @Override
    public void listenForConnections() {
        while(!isShuttingDown()) {
            try {
                Socket daemonClient = serverSocket.accept();
                southPole.addClient(daemonClient);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

}
