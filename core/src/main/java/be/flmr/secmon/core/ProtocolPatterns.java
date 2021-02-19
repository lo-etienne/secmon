package be.flmr.secmon.core;

import java.lang.reflect.Modifier;
import java.net.PortUnreachableException;
import java.util.Arrays;

public final class ProtocolPatterns {
    public static final String LETTER = "[A-Za-z]";
    public static final String DIGIT = "[0-9]";
    public static final String LETTER_DIGIT = LETTER + "|" + DIGIT;
    public static final String CRLF = "\\\\r\\\\n";
    public static final String PORT = "([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])";
    public static final String CHARACTER = "\\p{Print}";                    // "[\\x20-\\xFF]"  //Tous les character imprimable + espace
    public static final String CHARACTER_PASS = "\\p{Graph}";               // "[\\x21-\\xFF]"  //Tous les character imprimable
    public static final String SP = "\\p{Space}";                           // "[\\x20]"        //character espace

    public static final String ID = "(?<ID>(" + LETTER_DIGIT + "){5,10})";
    public static final String PROTOCOL = "(?<PROTOCOL>(" + LETTER_DIGIT + "){3,15})";
    public static final String USERNAME = "(?<USERNAME>(" + LETTER_DIGIT + "){3,50})";
    public static final String PASSWORD = "(?<PASSWORD>" + CHARACTER_PASS + "{3,50})";
    public static final String HOST = "(?<HOST>(" + LETTER_DIGIT + "|[._-]" + "){3,50})";
    public static final String PATH = "(?<PATH>/" + "(" + LETTER_DIGIT + "|[\\\\/._-]" + "){0,100})";

    public static final String URL = PROTOCOL + "://" + "(" + USERNAME + "(:" + PASSWORD + ")?" + "@)?" + HOST + "(:" + PORT + ")?" + PATH;
    public static final String MIN = "(?<MIN>(" + DIGIT + "){1,8})";//seuil inférieur (pour l’alarme)
    public static final String MAX = "(?<MAX>(" + DIGIT + "){1,8})";//seuil supérieur (pour l’alarme)
    public static final String FREQUENCY = "(?<FREQUENCY>(" + DIGIT + "){1,8})";
    public static final String AUGMENTED_URL = ID + "!" + URL + "!" + MIN + "!" + MAX + "!" + FREQUENCY;
    public static final String STATE = "(?<STATE>(OK|ALARM|DOWN))";
    public static final String MESSAGE = "(?<MESSAGE>(" + CHARACTER + "){1,200})";

    private ProtocolPatterns(){

    }

    public static void main(String[] args) {
        Arrays.stream(ProtocolPatterns.class.getDeclaredFields())
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
