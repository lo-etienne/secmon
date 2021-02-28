package be.flmr.secmon.daemon.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.jar.JarEntry;

import static be.flmr.secmon.daemon.config.ReadWriteLockContainer.lock;

public class DaemonJSONConfigurationWriter implements IDaemonConfigurationWriter {
    private Lock read = lock.readLock();
    private Lock write = lock.writeLock();

    private Reader reader;
    private Writer writer;

    private Gson gson;
    private DaemonJSONConfig config;

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
    public IDaemonConfigurationWriter addService(String service) {
        if (!lock.isWriteLocked())
            throw new IllegalStateException("Il faut commencer une transaction avant de pouvoir ajouter un service !");
        config = gson.fromJson(reader, DaemonJSONConfig.class);
        config.probes.add(service);
        return this;
    }

    @Override
    public IDaemonConfigurationWriter addServices(String... service) {
        for (String s : service) addService(s);
        return this;
    }

    @Override
    public IDaemonConfigurationWriter beginTransaction() {
        read.lock();
        write.lock();
        return this;
    }

    @Override
    public void endTransaction() {
        if (!lock.isWriteLocked())
            throw new IllegalStateException("Une transaction n'as pas été commencée");

        gson.toJson(config, writer);

        write.unlock();
        read.unlock();
    }
}
