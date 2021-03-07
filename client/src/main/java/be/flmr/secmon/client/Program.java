package be.flmr.secmon.client;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.PatternGroup;
import be.flmr.secmon.core.pattern.ProtocolPacket;
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

    @Parameters(index= "1", description = "add-service | list-service | state-service")
    private String typeService = "";

    @Parameters(index= "2", defaultValue = "")
    private String parameterService = "";

    @Option(names = {"-p", "--port"}, description = "Port")
    private String port = "42069";

    private PrintStream stream;

    public Program(){
        this.stream = System.out;
    }

    @Override
    public Integer call() throws Exception {

        Client client = new Client(System.out,host,port);
        if(verify()){
            switch(typeService) {
                case "add-service":
                    client.addSrvReq(parameterService);
                    break;
                case "list-service":
                    client.listSrvReq();
                    break;
                case "state-service":
                    client.stateSrvReq(parameterService);
                    break;
            }
        }
        return 0;
    }

    public static void main(String[] args){
        int exitCode = new CommandLine(new Program()).execute(args);
        System.exit(exitCode);
    }

    private boolean verify(){
        switch(typeService) {
            case "add-service":
                return verifyContent(PatternGroup.AUGMENTEDURL);
            case "list-service":
                return true;
            case "state-service":
                return verifyContent(PatternGroup.ID);
            default:
                stream.print("Mauvaise commande");
                return false;
        }
    }

    private boolean verifyContent(PatternGroup group) {
        return parameterService.matches(group.getPattern());
    }
}