package be.flmr.secmon.daemon.config;

import be.flmr.secmon.core.net.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Writer;

public class DaemonJSONConfigurationWriter implements IDaemonConfigurationWriter {

    private static final Logger LOG = LoggerFactory.getLogger(DaemonJSONConfigurationWriter.class);
    private Writer writer;

    private Gson gson;

    public DaemonJSONConfigurationWriter(final Writer writer) {
        this.writer = writer;
        this.gson = new GsonBuilder().registerTypeAdapter(Service.class, new ServiceJSONParser()).create();
    }

    @Override
    public void write(final DaemonJSONConfig config) {
        try {
            gson.toJson(config, writer);
        } catch (JsonIOException e) {
            LOG.warn("Il y a eu une erreur au niveau de la persistance de la configuration du daemon", e);
        }

    }

    @Override
    public void close() throws Exception {
        this.writer.close();
    }
}
