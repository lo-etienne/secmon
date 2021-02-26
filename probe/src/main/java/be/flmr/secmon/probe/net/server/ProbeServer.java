package be.flmr.secmon.probe.net.server;

import be.flmr.secmon.core.net.IIntervalProtocolPacketSender;
import be.flmr.secmon.core.net.IServer;
import be.flmr.secmon.core.pattern.*;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Protocol;
import be.flmr.secmon.probe.config.ProbeJSONConfigurationReader;
import be.flmr.secmon.probe.net.IService;
import be.flmr.secmon.probe.net.Service;
import be.flmr.secmon.probe.net.client.ProbeClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProbeServer extends AbstractRouter implements IServer, Runnable {
    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private IProtocolPacket announceMsg;

    private int aliveInterval;

    private ServerSocket socket;
    private Set<ProbeClient> clients;
    private List<IService> services;

    private IIntervalProtocolPacketSender multicastSender;

    public ProbeServer(ProbeJSONConfigurationReader config) {
        try {
            socket = new ServerSocket(Integer.parseInt(config.getMulticastPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        services = new ArrayList<>();
        aliveInterval = config.getAliveInterval();

        announceMsg = new ProtocolPacketBuilder()
                .withPatternType(ProtocolPattern.ANNOUNCE)
                .withGroup(PatternGroup.PROTOCOL, config.getProtocol())
                .withGroup(PatternGroup.PORT, config.getMulticastPort())
                .build();

        //TODO : Créer une implémentation d'IIntervalProtocolPacketSender
    }

    @Override
    public void listenForConnections() {
        try {
            Socket client = socket.accept();
            clients.add(new ProbeClient(client, this, this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

    @Override
    public void run() {
        multicastSender.sendWithInterval(announceMsg, aliveInterval, TimeUnit.SECONDS);

        executor.execute(
            () -> {
                for (var client : clients) {
                    IProtocolPacket packet = client.receive();
                    if(packet != null) {
                        execute(client, packet);
                    }
                }
            }
        );
    }

    @Protocol(pattern = ProtocolPattern.CONFIG)
    private void updateServices(Object sender, IProtocolPacket packet) {
        var services = Service.from(packet);

        services.forEach((service) -> {
            if(!this.services.contains(service)) {
                this.services.add(service);
            } else {
                this.services.set(this.services.indexOf(service), service);
            }
        });
    }

    @Protocol(pattern = ProtocolPattern.STATE_REQ)
    private void getServiceState(Object sender, IProtocolPacket packet) {

    }
}
