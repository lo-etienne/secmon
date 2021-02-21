package be.flmr.secmon.core.net;

public interface IServer extends Runnable {
    void listenForConnections();

    @Override
    default void run() {
        listenForConnections();
    }
}
