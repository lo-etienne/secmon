package be.flmr.secmon.daemon.config;

import be.flmr.secmon.core.net.IService;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DaemonJSONConfig {
    @SerializedName("name")
    private String name;

    @SerializedName("version")
    private String version;

    @SerializedName("multicast_address")
    private String multicastAddress;

    @SerializedName("multicast_port")
    private String multicastPort;

    @SerializedName("client_port")
    private String clientPort;

    @SerializedName("tls")
    private boolean tls;

    @SerializedName("aes_key")
    private String aesKey;

    @SerializedName("certificate_path")
    private String certificatePath;

    @SerializedName("certificate_password")
    private String certificatePassword;

    @SerializedName("probes")
    private List<IService> services;

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
                ", probes=" + services +
                '}';
    }

    public List<IService> getServices() {
        return services;
    }

    public String getAesKey() {
        return aesKey;
    }

    public String getCertificatePath() {
        return certificatePath;
    }

    public String getCertificatePassword() {
        return certificatePassword;
    }

    public String getClientPort() {
        return clientPort;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public String getMulticastPort() {
        return multicastPort;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public void addService(final IService service) {
        services.add(service);
    }

    public boolean hasService(final IService service) {
        return services.contains(service);
    }

    public void removeService(final IService service) {
         services.remove(service);
    }

    public boolean isTls() {
        return tls;
    }
}