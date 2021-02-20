package be.flmr.secmon.core;

import java.lang.reflect.Modifier;
import java.net.PortUnreachableException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ProtocolPatterns implements PatternExtractor {
    LETTER("[A-Za-z]"),
    DIGIT("[0-9]"),
    LETTER_DIGIT(LETTER.p + "|" + DIGIT.p),
    CRLF("\\\\r\\\\n"),
    PORT("([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])"),
    CHARACTER("\\p{Print}"),                    // "[\\x20-\\xFF]"  //Tous les character imprimable + espace
    CHARACTER_PASS("\\p{Graph}"),               // "[\\x21-\\xFF]"  //Tous les character imprimable
    SP("\\p{Space}"),                           // "[\\x20]"        //character espace

    ID("(?<ID>(" + LETTER_DIGIT.p + "){5,10})"),
    PROTOCOL("(?<PROTOCOL>(" + LETTER_DIGIT.p + "){3,15})"),
    USERNAME("(?<USERNAME>(" + LETTER_DIGIT.p + "){3,50})"),
    PASSWORD("(?<PASSWORD>" + CHARACTER_PASS.p + "{3,50})"),
    HOST("(?<HOST>(" + LETTER_DIGIT.p + "|[._-]" + "){3,50})"),
    PATH("(?<PATH>/" + "(" + LETTER_DIGIT.p + "|[\\\\/._-]" + "){0,100})"),

    URL(PROTOCOL.p + "://" + "(" + USERNAME.p + "(:" + PASSWORD.p + ")?" + "@)?" + HOST.p + "(:" + PORT.p + ")?" + PATH.p),
    MIN("(?<MIN>(" + DIGIT.p + "){1,8})"),//seuil inférieur (pour l’alarme)
    MAX("(?<MAX>(" + DIGIT.p + "){1,8})"),//seuil supérieur (pour l’alarme)
    FREQUENCY("(?<FREQUENCY>(" + DIGIT.p + "){1,8})"),
    AUGMENTED_URL(ID.p + "!" + URL.p + "!" + MIN.p + "!" + MAX.p + "!" + FREQUENCY.p),
    STATE("(?<STATE>(OK|ALARM|DOWN))"),
    MESSAGE("(?<MESSAGE>(" + CHARACTER.p + "){1,200})");
    
    public String p;
    
    private ProtocolPatterns(String pattern) {
        this.p = pattern;
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

    @Override
    public String getPattern() {
        return p;
    }
}
