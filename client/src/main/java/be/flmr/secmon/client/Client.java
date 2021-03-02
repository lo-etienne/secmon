package be.flmr.secmon.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import be.flmr.secmon.core.pattern.*;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "monitor", mixinStandardHelpOptions = true, version = "monitor 1.0", description = "Console for interation with daemon")
public class Client implements Callable<Integer>{
    // TODO : securiser les envoie

    private ProtocolClient protocol;

    @Parameters(index = "0", description = "host")
    private String host = "localhost";

    @Option(names = {"-a", "--add_service_req"}, description = "augmented_url")
    private String add_service_req = "";

    @Option(names = {"-l", "--list_service_req"}, description = "nothing needed")
    private String list_service_req = "void";

    @Option(names = {"-s", "--state_service_req"}, description = "ID")
    private String state_service_req = "";

    @Option(names = {"-p", "--port"}, description = "Port")
    private String port = "42069";

    public Client(){
        protocol = new ProtocolClient(System.out);
    }

    @Override
    public Integer call() throws Exception {
        Socket socket = new Socket(host,Integer.parseInt(port));

        var writer = new PrintWriter(socket.getOutputStream());
        var buffered = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        writer.print(SetPacketClient.setPacket(getMap()).buildMessage());
        writer.flush();

        String str = buffered.readLine();
        IProtocolPacket packet = ProtocolPacket.from(str + "\r\n");
        protocol.execute(socket,packet);
        return 0;
    }

    /**
     * Methode qui creer une map<String, String> pour avoir les valeur entree par l'utilisateur
     * @return Map<String, String>
     */
    public Map<String,String> getMap(){
        Map<String, String> map = new HashMap<>();

        map.put("add_service_req",add_service_req);
        map.put("list_service_req",list_service_req);
        map.put("state_service_req",state_service_req);

        return map;
    }

    public static void main(String[] args){
        String[] args2 = {"localhost","-a", "LaP0mm3!P0mm3://Sart0:mdp1234@abc.def.123:55555/LesP0mm3s.com!30!250!600"};
        int exitCode = new CommandLine(new Client()).execute(args2);
        System.exit(exitCode);
    }
}
