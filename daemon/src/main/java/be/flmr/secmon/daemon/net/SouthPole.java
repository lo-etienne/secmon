package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.IClient;

import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class SouthPole implements ISouthPole {

    private Set<IClient> clients = new HashSet<>();

    @Override
    public void addClient(final Socket daemonClient) {
        clients.add(new DaemonClient(daemonClient));
    }

    @Override
    public Set<IClient> getClients() {
        return this.clients;
    }

    @Override
    public void run() {

    }
}
