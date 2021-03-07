package be.flmr.secmon.daemon.config;

import be.flmr.secmon.core.net.IService;

import java.util.List;

public interface IDaemonConfigurationReader extends AutoCloseable {

    DaemonJSONConfig read();


}
