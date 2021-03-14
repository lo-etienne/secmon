package be.flmr.secmon.client;

import be.flmr.secmon.core.pattern.PatternGroup;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.PrintStream;
import java.util.concurrent.Callable;

/**
 * Class qui recupere et verifie les entree du user puis fais appel a la class client pour exploiter les entree
 */
@Command(name = "monitor", mixinStandardHelpOptions = true, version = "monitor 1.0", description = "Console for interation with daemon")
public class Program implements Callable<Integer> {

    @Parameters(index = "0", description = "host")
    private String host = "localhost";

    @Parameters(index = "1", description = "add-service | list-service | state-service")
    private String typeService = "";

    @Parameters(index = "2", defaultValue = "")
    private String parameterService = "";

    @Option(names = {"-p", "--port"}, description = "Port")
    private String port = "42069";

    @Option(names = {"--no-tls"}, description = "Désactive la connexion au serveur en TLS")
    private boolean tls;

    private PrintStream stream;

    public Program() {
        this.stream = System.out;
    }

    @Override
    public Integer call() {
        try {
            Client client = new Client(System.out, host, port, !tls);
            if (verify()) {
                switch (typeService) {
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
        } catch (Exception e) {
            stream.printf("Une erreur s'est produite: %s\n", e.getMessage());
        }
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Program()).execute(args);
        System.exit(exitCode);
    }

    /**
     * Methode qui verifie si le type de service est un type de service correct et valide les paramettre de ces dernier
     *
     * @return true si l'utilisateur a encoder un bon service et false si il na pas encoder un bon service
     */
    private boolean verify() {
        switch (typeService) {
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

    /**
     * Methode qui verifie si ce que l'utilisateur entre repond au pattern
     *
     * @param group et le pattern que l'entree doit correspondre
     * @return true si le pattern valide le parameterService et false si ne le valide pas
     */
    private boolean verifyContent(PatternGroup group) {
        return parameterService.matches(group.getPattern());
    }
}