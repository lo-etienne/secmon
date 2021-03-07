package be.flmr.secmon.daemon.config;

public interface IDaemonConfigurationReader extends AutoCloseable {

    DaemonJSONConfig read();


}
