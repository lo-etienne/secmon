package be.flmr.secmon.core;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public enum ClientDaemonProtocolPatterns implements PatternExtractor {
    ADD_SERVICE_REQUEST("ADDSRV" + ProtocolPatterns.SP.p + ProtocolPatterns.AUGMENTED_URL.p + ProtocolPatterns.CRLF.p),
    ADD_SERVICE_RESPONSE("(\\+OK|-ERR)" + "(" + ProtocolPatterns.SP.p + ProtocolPatterns.MESSAGE.p + ")?" + ProtocolPatterns.CRLF.p),

    LIST_SERVICE_REQUEST("LISTSRV" + ProtocolPatterns.CRLF.p),
    LIST_SERVICE_RESPONSE("SRV(" + ProtocolPatterns.SP.p + ProtocolPatterns.ID.p + "){0,100}" + ProtocolPatterns.CRLF.p),

    STATE_SERVICE_REQUEST("STATESRV" + ProtocolPatterns.SP.p + ProtocolPatterns.ID.p + ProtocolPatterns.CRLF.p),
    STATE_SERVICE_RESPONSE("STATE" + ProtocolPatterns.SP.p + ProtocolPatterns.ID.p + ProtocolPatterns.SP.p + ProtocolPatterns.URL.p + ProtocolPatterns.SP.p + ProtocolPatterns.STATE.p + ProtocolPatterns.CRLF.p);

    private String p;

    private ClientDaemonProtocolPatterns(String pattern){
        this.p = pattern;
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

    @Override
    public String getPattern() {
        return p;
    }
}
