package be.flmr.secmon.core;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public class MulticastProtocolPatterns {

    public static final String ANNOUCE = "IAMHERE" + ProtocolPatterns.SP + ProtocolPatterns.PROTOCOL
            + ProtocolPatterns.SP + ProtocolPatterns.PORT + ProtocolPatterns.CRLF;
    public static final String NOTIFICATION = "NOTIFY" + ProtocolPatterns.SP + ProtocolPatterns.PROTOCOL
            + ProtocolPatterns.SP + ProtocolPatterns.PORT + ProtocolPatterns.CRLF;

    private MulticastProtocolPatterns(){

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

}
