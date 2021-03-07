package be.flmr.secmon.probehttps;

import be.flmr.secmon.core.multicast.ConnectionBroadcaster;
import be.flmr.secmon.probe.config.ProbeJSONConfigurationReader;
import be.flmr.secmon.probe.net.server.ProbeServer;
import be.flmr.secmon.probehttps.service.HttpsServiceProber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Program {
    private final static Logger log = LoggerFactory.getLogger(Program.class);
    private final static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try(var resource = Program.class.getClassLoader().getResourceAsStream((args.length > 0) ? args[0] : "probe.conf.json")) {
            if(resource == null) {
                throw new NullPointerException("Ressource Json non trouvée");
            }

            ProbeJSONConfigurationReader reader = new ProbeJSONConfigurationReader(new InputStreamReader(resource));
            ConnectionBroadcaster multicastSender = new ConnectionBroadcaster(reader.getMulticastAddress(), Integer.parseInt(reader.getMulticastPort()));

            ProbeServer server = new ProbeServer(reader, multicastSender, new HttpsServiceProber());

            var executor = Executors.newSingleThreadExecutor();
            executor.execute(server);

            while(!scanner.nextLine().equals("quit"));

            server.close();
            executor.shutdown();
        } catch(NullPointerException e) {
            log.error(e.getMessage(), e);
            System.exit(-1);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(-2);
        } catch (Exception e) {
            e.printStackTrace();
            //On saurait rien faire d'autre ¯\_(ツ)_/¯
        }
    }
}
