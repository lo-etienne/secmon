package be.flmr.secmon.client;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import be.flmr.secmon.core.pattern.PatternGroup;
import be.flmr.secmon.core.pattern.ProtocolPattern;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "monitor", mixinStandardHelpOptions = true, version = "monitor 1.0", description = "Console for interation with daemon")
public class Program implements Callable<Integer>{

    private ProtocolClient protocol;

    @Parameters(index = "0", description = "host")
    private String host = "localhost";

    @Parameters(index= "1" ,description = "add-service | list-service | state-service")
    private String typeService = "";

    @Parameters(index= "2")
    private String parameterService = "";

    @Option(names = {"-p", "--port"}, description = "Port")
    private String port = "42069";

    private PrintStream stream;
    private String add_service_req;
    private String list_service_req;
    private String state_service_req;

    public Program(){
        this.stream = System.out;
    }

    @Override
    public Integer call() throws Exception {

        Client client = new Client(System.out,host,port);
        verifie();
        if(!(add_service_req.isEmpty())){
            if(add_service_req.matches(PatternGroup.AUGMENTEDURL.getPattern()))
                client.addSrvReq(add_service_req);
        }
        if((list_service_req.isEmpty())){
            if(list_service_req.equals(""))
                client.listSrvReq();
        }
        if(!(state_service_req.isEmpty())){
            if(state_service_req.matches(PatternGroup.ID.getPattern()))
                client.stateSrvReq(state_service_req);
        }
        client.receive();
        return 0;
    }

    public static void main(String[] args){
        //String[] args2 = {"localhost","-a", "LaP0mm3!P0mm3://Sart0:mdp1234@abc.def.123:55555/LesP0mm3s.com!30!250!600"};
        int exitCode = new CommandLine(new Program()).execute(args);
        System.exit(exitCode);
    }

    private boolean verifie(){
        switch(typeService) {
            case "add-service":
                return verifieContent(ProtocolPattern.ADD_SERVICE_REQ);
            case "list-service":
                return verifieContent(ProtocolPattern.LIST_SERVICE_REQ);
            case "state-service":
                return verifieContent(ProtocolPattern.STATE_SERVICE_REQ);
            default:
                stream.print("Mauvaise commande");
                return false;
        }
    }

    private boolean verifieContent(ProtocolPattern protocol) {
        return parameterService.matches(protocol.getPattern());
    }
}