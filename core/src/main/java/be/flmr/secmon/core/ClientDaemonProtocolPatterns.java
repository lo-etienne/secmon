package be.flmr.secmon.core;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ClientDaemonProtocolPatterns {
    public static final String ADD_SERVICE_REQUEST
            = "ADDSRV" + ProtocolPatterns.SP + ProtocolPatterns.AUGMENTED_URL + ProtocolPatterns.CRLF;
    public static final String ADD_SERVICE_RESPONSE
            = "(\\+OK|-ERR)" + "(" + ProtocolPatterns.SP
            + ProtocolPatterns.MESSAGE + ")?"
            + ProtocolPatterns.CRLF;

    public static final String LIST_SERVICE_REQUEST
            = "LISTSRV" + ProtocolPatterns.CRLF;
    public static final String LIST_SERVICE_RESPONSE
            = "SRV(" + ProtocolPatterns.SP + ProtocolPatterns.ID + "){0,100}" + ProtocolPatterns.CRLF;

    public static final String STATE_SERVICE_REQUEST
            = "STATESRV" + ProtocolPatterns.SP + ProtocolPatterns.ID + ProtocolPatterns.CRLF;
    public static final String STATE_SERVICE_RESPONSE
            = "STATE"
            + ProtocolPatterns.SP + ProtocolPatterns.ID + ProtocolPatterns.SP
            + ProtocolPatterns.URL + ProtocolPatterns.SP + ProtocolPatterns.STATE
            + ProtocolPatterns.CRLF;

    private ClientDaemonProtocolPatterns() {

    }

    public static void main(String[] args) {
        Arrays.stream(ClientDaemonProtocolPatterns.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .forEach(f -> {
                    try {
                        System.out.println(f.getName() + " ==> " + f.get(null));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }
}
