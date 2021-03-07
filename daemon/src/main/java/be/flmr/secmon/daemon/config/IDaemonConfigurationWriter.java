package be.flmr.secmon.daemon.config;

import be.flmr.secmon.core.net.IService;

public interface IDaemonConfigurationWriter extends AutoCloseable {

    void write(DaemonJSONConfig config);
}
