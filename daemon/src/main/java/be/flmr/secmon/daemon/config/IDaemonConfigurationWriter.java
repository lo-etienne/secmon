package be.flmr.secmon.daemon.config;

public interface IDaemonConfigurationWriter {
    // TODO: Changer String en IService
    void addService(final String service);
    void addServices(final String... service);
}
