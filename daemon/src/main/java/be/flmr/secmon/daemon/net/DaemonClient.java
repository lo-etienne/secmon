package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.IClient;
import be.flmr.secmon.core.net.IProtocolPacketReceiver;
import be.flmr.secmon.core.net.IProtocolPacketSender;
import be.flmr.secmon.core.pattern.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;


public class DaemonClient implements IClient, IProtocolPacketSender, IProtocolPacketReceiver {

    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;



    public DaemonClient(final Socket socket)  {
        try {
            this.socket = socket;
            printWriter = new PrintWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void send(IProtocolPacket packet) {
        printWriter.print(packet.buildMessage());
    }

    @Override
    public IProtocolPacket receive() {
        try {
            final String message = bufferedReader.readLine();
            IProtocolPacket protocolPacket = ProtocolPacket.from(message);
            return protocolPacket;
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {

    }
}
