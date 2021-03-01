package be.flmr.secmon.daemon.config;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class DaemonJSONConfig {
    @SerializedName("name")
    String name;

    @SerializedName("version")
    String version;

    @SerializedName("multicast_address")
    String multicastAddress;

    @SerializedName("multicast_port")
    String multicastPort;

    @SerializedName("client_port")
    String clientPort;

    @SerializedName("tls")
    boolean tls;

    @SerializedName("aes_key")
    String aesKey;

    @SerializedName("probes")
    List<String> probes;

    @Override
    public String toString() {
        return "DaemonJSONConfig{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", multicastAddress='" + multicastAddress + '\'' +
                ", multicastPort='" + multicastPort + '\'' +
                ", clientPort='" + clientPort + '\'' +
                ", tls=" + tls +
                ", aesKey='" + aesKey + '\'' +
                ", probes=" + probes +
                '}';
    }
}