package be.flmr.secmon.probe.net.client;

import be.flmr.secmon.core.net.IClient;
import be.flmr.secmon.core.net.IProtocolPacketReceiver;
import be.flmr.secmon.core.net.IProtocolPacketSender;
import be.flmr.secmon.core.net.IServer;
import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.router.AbstractRouter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ProbeClient implements IClient, IProtocolPacketSender, IProtocolPacketReceiver {
    private final AbstractRouter router;
    private Socket socket;
    private IServer server;

    private PrintWriter out;
    private BufferedReader in;

    public ProbeClient(Socket socket, IServer server, AbstractRouter router) {
        this.server = server;
        this.socket = socket;
        this.router = router;

        //out = new PrintWriter(socket.getOutputStream());
        //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public IProtocolPacket receive() {
        return null;
    }

    @Override
    public void send(IProtocolPacket packet) {

    }

    @Override
    public void run() {
        while (!server.isShuttingDown()) {
            var packet = receive();
            router.execute(this, packet);
        }
    }
}
