package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.IProtocolPacketReceiver;
import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.ProtocolPattern;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Protocol;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class NorthPole extends AbstractRouter implements INorthPole {

    private Socket socket;
    private IProtocolPacketReceiver mutlicast;

    private PrintWriter out;

    public NorthPole(Socket socket, IProtocolPacketReceiver mutlicast) {
        this.socket = socket;
        this.mutlicast = mutlicast;
        try {
            this.out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            // TODO: écrire une exception
        }
    }


    boolean isConnected() {
        return true;
    }

    @Override
    public void run() {

    }

    @Protocol(pattern = ProtocolPattern.ANNOUNCE)
    private void onAnnounce(Object sender, IProtocolPacket packet) {

    }

    @Protocol(pattern = ProtocolPattern.NOTIFICATION)
    private void onNotify(Object sender, IProtocolPacket packet) {

    }
}
