package be.flmr.secmon.daemon.config;

import java.util.List;

public interface IDaemonConfigurationReader {

    List<String> getServices();
    String getName();
    String getVersion();
    String getMulticastAddress();
    String getMulticastPort();
    String getClientPort();
    String getAesKey();
    boolean isTls();


}
