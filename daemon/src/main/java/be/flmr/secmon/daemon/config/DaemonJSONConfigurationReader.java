package be.flmr.secmon.daemon.config;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class DaemonJSONConfigurationReader implements IDaemonConfigurationReader {
    private DaemonJSONConfig config;
    private Lock lock = ReadWriteLockContainer.lock.readLock();

    public DaemonJSONConfigurationReader(Reader reader) {
        lock.lock();
        try {
            Gson gson = new Gson();
            config = gson.fromJson(reader, DaemonJSONConfig.class);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<String> getServices() {
        return config.probes;
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
    public String getMulticastAddress() {
        return config.multicastAddress;
    }

    @Override
    public String getMulticastPort() {
        return config.multicastPort;
    }

    @Override
    public String getClientPort() {
        return config.clientPort;
    }

    @Override
    public String getAesKey() {
        return config.aesKey;
    }

    @Override
    public boolean isTls() {
        return config.tls;
    }
}
