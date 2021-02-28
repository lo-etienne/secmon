package be.flmr.secmon.daemon.config;

public interface IDaemonConfigurationWriter {
    // TODO: Changer String en IService
    IDaemonConfigurationWriter addService(final String service);
    IDaemonConfigurationWriter addServices(final String... service);
    IDaemonConfigurationWriter beginTransaction();
    void endTransaction();
}
