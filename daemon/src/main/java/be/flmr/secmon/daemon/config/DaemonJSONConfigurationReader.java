package be.flmr.secmon.daemon.config;

import be.flmr.secmon.core.net.IService;
import be.flmr.secmon.core.net.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

public class DaemonJSONConfigurationReader implements IDaemonConfigurationReader {
    private Reader reader;
    private Gson gson;

    public DaemonJSONConfigurationReader(final Reader reader) {
        this.reader = reader;
        this.gson = new GsonBuilder().registerTypeHierarchyAdapter(IService.class, new ServiceJSONParser()).create();
    }

    @Override
    public DaemonJSONConfig read() {
        return gson.fromJson(reader, DaemonJSONConfig.class);
    }

    @Override
    public void close() throws Exception {
        this.reader.close();
    }
}
