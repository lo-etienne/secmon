package be.flmr.secmon.probe.config;

public interface ProbeConfigurationReader {
    String getName();
    String getVersion();
    String getMulticastAddress();
    String getMulticastPort();
    String getAesKey();
    int getAliveInterval();
}