package be.flmr.secmon.probehttps.service;

import be.flmr.secmon.core.net.IService;
import be.flmr.secmon.probe.service.ServiceProber;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Classe permettant l'interrogation d'un service HTTPS
 */
public class HttpsServiceProber implements ServiceProber {
    private final static Logger log = LoggerFactory.getLogger(HttpsServiceProber.class);

    /**
     * Interroge un service HTTPS et renvoie sa valeur ou lance une {@code IOException} si une erreur survient.
     * @param service Service à interroger
     * @return int - Valeur obtenue lors de l'interrogation du service
     * @throws IOException lancée si une erreur I/O survient lors de la connexion
     */
    @Override
    public int get(IService service) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(service.getURL()).openConnection();

        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(new InputStreamReader(connection.getInputStream()), JsonObject.class);

        log.debug("{}", gson.toJson(obj));

        var temp = obj.entrySet().stream().findFirst().orElseThrow();
        return (int) temp.getValue().getAsDouble();
    }
}
