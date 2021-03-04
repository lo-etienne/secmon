package be.flmr.secmon.daemon.config;

import be.flmr.secmon.core.net.IService;

import java.util.List;

public interface IDaemonConfigurationReader {

    List<IService> getServices();
    String getName();
    String getVersion();
    String getMulticastAddress();
    String getMulticastPort();
    String getClientPort();
    String getAesKey();
    boolean isTls();


}
