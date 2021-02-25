package be.flmr.secmon.core.net;

public interface IServer {
    void listenForConnections();
    boolean isShuttingDown();
}
