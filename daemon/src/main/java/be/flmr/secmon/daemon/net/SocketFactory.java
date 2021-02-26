package be.flmr.secmon.daemon.net;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class SocketFactory {

    public static ServerSocket securedSocket(final File certificat, final int port) {
        //TODO
        return null;
    }

    public static ServerSocket unsecuredSocket(final int port) {
        try {
            return new ServerSocket(port);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

}
