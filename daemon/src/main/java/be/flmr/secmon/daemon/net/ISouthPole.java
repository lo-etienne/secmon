package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.IClient;

import java.net.Socket;
import java.util.Set;

public interface ISouthPole extends Runnable {

    void addClient(final Socket daemonClient);
    Set<IClient> getClients();

}
