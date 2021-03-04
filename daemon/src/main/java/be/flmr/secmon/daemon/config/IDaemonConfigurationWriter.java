package be.flmr.secmon.daemon.config;

import be.flmr.secmon.core.net.IService;

public interface IDaemonConfigurationWriter {
    void addService(final IService service);
    void addServices(final IService... service);
}
