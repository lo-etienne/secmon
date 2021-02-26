package be.flmr.secmon.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import be.flmr.secmon.core.pattern.*;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Protocol;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "monitor", mixinStandardHelpOptions = true, version = "monitor 1.0", description = "Console for interation with daemon")
public class Main extends AbstractRouter implements Callable<Integer>{
    // TODO : connection daemon
    // TODO : configurer les ligne de commmande
    // TODO : securiser les envoie

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

    @Override
    public Integer call() throws Exception {
        Socket socket = new Socket(host,Integer.parseInt(port));

        var writer = new PrintWriter(socket.getOutputStream());
        var buffered = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        writer.print(setPacket().buildMessage());
        writer.flush();

        buffered.lines().map(l -> ProtocolPacket.from(l).getMessage()).forEach(System.out::println);
        return 0;
    }

    public static void main(String[] args){
        String[] args2 = {"localhost","-a", "LaP0mm3!P0mm3://Sart0:mdp1234@abc.def.123:55555/LesP0mm3s.com!30!250!600"};
        int exitCode = new CommandLine(new Main()).execute(args2);
        System.exit(exitCode);
    }

    /**
     * Methode qui creer un packet avec les valeurs adequates et lance une expection si le packet n'est pas creer
     * @return packet
     */
    private IProtocolPacket setPacket(){
        IProtocolPacket packet = null;

        if(!(add_service_req.isEmpty())){
            packet = new ProtocolPacketBuilder()
                    .withPatternType(ProtocolPattern.ADD_SERVICE_REQ)
                    .withGroup(PatternGroup.AUGMENTEDURL,add_service_req)
                    .build();
        }
        if(list_service_req.isEmpty()){
            packet = new ProtocolPacketBuilder()
                    .withPatternType(ProtocolPattern.LIST_SERVICE_REQ)
                    .build();
        }
        if(!(state_service_req.isEmpty())){
            packet = new ProtocolPacketBuilder()
                    .withPatternType(ProtocolPattern.STATE_SERVICE_REQ)
                    .withGroup(PatternGroup.ID,state_service_req)
                    .build();
        }

        if(packet == null)throw new IllegalArgumentException();
        return packet;
    }

    @Protocol(pattern = ProtocolPattern.ADD_SERVICE_RESP_ERR)
    private void respondServiceError(Object obj, IProtocolPacket packet){
        String str = "-ERR";
        if(!(packet.getValue(PatternGroup.MESSAGE).isEmpty())){
            str += packet.getValue(PatternGroup.MESSAGE);
        }
        System.out.println(str);
    }
    @Protocol(pattern = ProtocolPattern.ADD_SERVICE_RESP_OK)
    private void respondServiceOk(Object obj, IProtocolPacket packet){
        String str = "+OK";
        if(!(packet.getValue(PatternGroup.MESSAGE).isEmpty())){
            str += packet.getValue(PatternGroup.MESSAGE);
        }
        System.out.println(str);
    }

    @Protocol(pattern = ProtocolPattern.LIST_SERVICE_RESP)
    private void respondList(Object obj, IProtocolPacket packet){
        var list = PatternUtils.findGroups(packet.getMessage(),PatternGroup.ID);

        String str = list.stream().reduce("SRV", (a, b) -> a + " " + b);
        System.out.println(str);
    }
    @Protocol(pattern = ProtocolPattern.STATE_SERVICE_RESP)
    private void respondState(Object obj, IProtocolPacket packet){
        String str = "STATE";
        str += packet.getValue(PatternGroup.ID);
        str += packet.getValue(PatternGroup.URL);
        str += packet.getValue(PatternGroup.STATE);
        System.out.println(str);
    }

}
