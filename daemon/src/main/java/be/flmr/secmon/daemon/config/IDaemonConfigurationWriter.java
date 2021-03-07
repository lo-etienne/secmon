package be.flmr.secmon.daemon.config;

public interface IDaemonConfigurationWriter extends AutoCloseable {

    void write(DaemonJSONConfig config);
}
