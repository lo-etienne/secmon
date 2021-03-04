package be.flmr.secmon.daemon.config;

import be.flmr.secmon.core.net.IService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class DaemonJSONConfigurationWriter implements IDaemonConfigurationWriter {
    private Reader reader;
    private Writer writer;

    private Gson gson;

    public static DaemonJSONConfigurationWriter fromFile(File file) {
        try {
            return new DaemonJSONConfigurationWriter(new FileReader(file), new FileWriter(file));
        } catch (IOException e) {
            throw new IllegalArgumentException("Le fichier n'as pas été trouvé ou il y a eu un problème avec ce dernier.", e);
        }
    }

    public DaemonJSONConfigurationWriter(Reader reader, Writer writer) {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        this.writer = writer;
        this.reader = reader;
    }

    @Override
    public void addService(IService service) {
        var config = gson.fromJson(reader, DaemonJSONConfig.class);
        appendService(service, config);
        write(config);
    }

    @Override
    public void addServices(IService... service) {
        var config = gson.fromJson(reader, DaemonJSONConfig.class);
        for (IService s : service) appendService(s, config);
        write(config);
    }

    private void appendService(IService service, DaemonJSONConfig config) {
        config.probes.add(service.getAugmentedURL());
    }

    private void write(DaemonJSONConfig config) {
        gson.toJson(config, writer);
    }
}
