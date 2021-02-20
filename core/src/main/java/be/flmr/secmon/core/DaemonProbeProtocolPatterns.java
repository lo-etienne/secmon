package be.flmr.secmon.core;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public enum DaemonProbeProtocolPatterns implements PatternExtractor {

    CONFING("CURCONFIG(" + ProtocolPatterns.SP.p + ProtocolPatterns.AUGMENTED_URL.p + "){0,100}" + ProtocolPatterns.CRLF.p),
    STATE_REQ("STATEREQ" + ProtocolPatterns.SP.p + ProtocolPatterns.ID.p + ProtocolPatterns.CRLF.p),
    STATE_RESP("STATERESP" + ProtocolPatterns.SP.p + ProtocolPatterns.ID.p + ProtocolPatterns.SP.p + ProtocolPatterns.STATE.p + ProtocolPatterns.CRLF.p),
    ;

    private String p;

    private DaemonProbeProtocolPatterns(String pattern){
        this.p = pattern;
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

    @Override
    public String getPattern() {
        return p;
    }
}
