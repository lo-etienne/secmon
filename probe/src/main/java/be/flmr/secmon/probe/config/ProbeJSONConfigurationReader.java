package be.flmr.secmon.probe.config;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Reader;

/**
 * Permet de lire, depuis un fichier JSON, la configuration d'une probe.
 * <ul>
 *     <li>Nom, Version, Protocole</li>
 *     <li>Adresse et Port Multicast</li>
 *     <li>Clé de chiffrement AES</li>
 *     <li>Fréquence d'envoi d'information</li>
 * </ul>
 */
public class ProbeJSONConfigurationReader implements ProbeConfigurationReader {
    private ProbeJSONConfig config;

    public ProbeJSONConfigurationReader(Reader reader) {
        Gson gson = new Gson();
        config = gson.fromJson(reader, ProbeJSONConfig.class);
    }

    @Override
    public String getName() {
        return config.name;
    }

    @Override
    public String getVersion() {
        return config.version;
    }

    @Override
    public String getProtocol() {
        return config.protocol;
    }

    @Override
    public String getMulticastAddress() {
        return config.multicastAddress;
    }

    @Override
    public String getMulticastPort() {
        return config.multicastPort;
    }

    @Override
    public String getAesKey() {
        return config.aesKey;
    }

    @Override
    public int getAliveInterval() {
        return config.aliveInterval;
    }

    private static class ProbeJSONConfig {
        @SerializedName("name")
        private String name;

        @SerializedName("version")
        private String version;

        @SerializedName("protocol")
        private String protocol;

        @SerializedName("multicast_address")
        private String multicastAddress;

        @SerializedName("multicast_port")
        private String multicastPort;

        @SerializedName("aes_key")
        private String aesKey;

        @SerializedName("alive_interval")
        private int aliveInterval;
    }
}
