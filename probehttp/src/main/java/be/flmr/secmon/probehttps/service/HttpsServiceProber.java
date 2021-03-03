package be.flmr.secmon.probehttps.service;

import be.flmr.secmon.core.net.IService;
import be.flmr.secmon.core.net.Service;
import be.flmr.secmon.core.pattern.ProtocolPacket;
import be.flmr.secmon.probe.net.server.ProbeServiceCommunicator;
import be.flmr.secmon.probe.service.ServiceProber;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class HttpsServiceProber implements ServiceProber {
    private final static Logger log = LoggerFactory.getLogger(HttpsServiceProber.class);

    @Override
    public int get(IService service) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(service.getURL()).openConnection();

        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(new InputStreamReader(connection.getInputStream()), JsonObject.class);

        log.debug("{}", gson.toJson(obj));

        var temp = obj.entrySet().stream().findFirst().orElseThrow();
        return (int) temp.getValue().getAsDouble();
    }

    public static void main(String[] args) throws Exception {
        var service = Service.from(ProtocolPacket.from("CURCONFIG http1!http://192.168.128.38/api/get-temp/!5!35!6 http2!http://192.168.128.38/api/get-humidity/!0!80!8 http1!http://192.168.128.38/api/get-temp/!26!40!2\r\n"));
        HttpsServiceProber prober = new HttpsServiceProber();

        ProbeServiceCommunicator communicator = new ProbeServiceCommunicator(prober);

        communicator.addService(service.get(0));
        communicator.addService(service.get(1));

        TimeUnit.SECONDS.sleep(5);
        String status = communicator.getServiceState(service.get(0).getID());
        System.out.println(status);
        status = communicator.getServiceState(service.get(1).getID());
        System.out.println(status);

        TimeUnit.SECONDS.sleep(5);
        communicator.updateService(service.get(2));

        while(true) {
            TimeUnit.SECONDS.sleep(5);
            status = communicator.getServiceState(service.get(0).getID());
            System.out.println(status);
            status = communicator.getServiceState(service.get(1).getID());
            System.out.println(status);
        }

    }
}
