package be.flmr.secmon.core;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public class DaemonProbeProtocolPatterns {

    public static final String CONFING = "CURCONFIG(" + ProtocolPatterns.SP + ProtocolPatterns.AUGMENTED_URL + "){0,100}" + ProtocolPatterns.CRLF;
    public static final String STATE_REQ = "STATEREQ" + ProtocolPatterns.SP + ProtocolPatterns.ID + ProtocolPatterns.CRLF;
    public static final String STATE_RESP = "STATERESP" + ProtocolPatterns.SP + ProtocolPatterns.ID + ProtocolPatterns.SP + ProtocolPatterns.STATE + ProtocolPatterns.CRLF;

    private DaemonProbeProtocolPatterns(){

    }

    public static void main(String[] args) {
        Arrays.stream(DaemonProbeProtocolPatterns.class.getDeclaredFields())
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
