package be.flmr.secmon.core;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public enum MulticastProtocolPatterns implements PatternExtractor {

    ANNOUCE("IAMHERE" + ProtocolPatterns.SP.p + ProtocolPatterns.PROTOCOL.p + ProtocolPatterns.SP.p + ProtocolPatterns.PORT.p + ProtocolPatterns.CRLF.p),
    NOTIFICATION("NOTIFY" + ProtocolPatterns.SP.p + ProtocolPatterns.PROTOCOL.p + ProtocolPatterns.SP.p + ProtocolPatterns.PORT.p + ProtocolPatterns.CRLF.p);

    private String p;

    private MulticastProtocolPatterns(String pattern){
        this.p = pattern;
    }

    public static void main(String[] args) {
        Arrays.stream(MulticastProtocolPatterns.class.getDeclaredFields())
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
