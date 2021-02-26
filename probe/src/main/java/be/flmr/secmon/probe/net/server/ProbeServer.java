package be.flmr.secmon.probe.net.server;

import be.flmr.secmon.core.multicast.ConnectionBroadcaster;
import be.flmr.secmon.core.net.IClient;
import be.flmr.secmon.core.net.IServer;
import be.flmr.secmon.core.pattern.ProtocolPattern;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Protocol;
import be.flmr.secmon.probe.config.ProbeJSONConfigurationReader;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ProbeServer extends AbstractRouter implements IServer, Runnable {
    private int aliveInterval;

    private ServerSocket socket;
    private Set<IClient> clients;
    private ConnectionBroadcaster broadcaster; /* TODO: remplacer ConnectionBroadcaster par une interface
                                                        & remplacer les interfaces par les interfaces de core.net */

    public ProbeServer(ProbeJSONConfigurationReader config) {

    }

    @Override
    public void listenForConnections() {
        //Socket client = socket.accept();
        //clients.add();
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

    @Override
    public void run() {
        broadcaster.sendWithInterval("", aliveInterval, TimeUnit.SECONDS);


    }

    //@Protocol(pattern = ProtocolPattern.CONFIG)
}
