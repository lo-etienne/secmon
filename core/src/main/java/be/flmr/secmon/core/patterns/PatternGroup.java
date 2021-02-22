package be.flmr.secmon.core.patterns;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public enum PatternGroup {
    ID("(" + PatternGroup.LETTER_DIGIT + "){5,10}"),
    PROTOCOL("(" + PatternGroup.LETTER_DIGIT + "){3,15}"),
    USERNAME("(" + PatternGroup.LETTER_DIGIT + "){3,50}"),
    PASSWORD("(" + PatternGroup.CHARACTER_PASS + "){3,50}"),
    PORT("[1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]"),
    HOST("(" + PatternGroup.LETTER_DIGIT + "|[._-]" + "){3,50}"),
    PATH("/" + "(" + PatternGroup.LETTER_DIGIT + "|[\\\\/._-]" + "){0,100}"),
    MIN("(" + PatternGroup.DIGIT + "){1,8}"),
    MAX("(" + PatternGroup.DIGIT + "){1,8}"),
    FREQUENCY("(" + PatternGroup.DIGIT + "){1,8}"),
    STATE("(OK|ALARM|DOWN)"),
    MESSAGE("(" + PatternGroup.CHARACTER + "){1,200}"),
    URL(PROTOCOL.getPattern() + "://" + "(" + USERNAME.getPattern() + "(:" + PASSWORD.getPattern()
            + ")?" + "@)?" + HOST.getPattern() + "(:" + PORT.getPattern() + ")?" + PATH.getPattern()),
    AUGMENTEDURL(ID.getPattern() + "!" + URL.getPattern() + "!" + MIN.getPattern() + "!" + MAX.getPattern() + "!" + FREQUENCY.getPattern()),
    CONFIG("(" + PatternGroup.SP + PatternGroup.AUGMENTEDURL.getPattern() + "){0,100}"),
    SRVLIST("(" + PatternGroup.SP + PatternGroup.ID.getPattern() + "){0,100}"),
    OPTIONALMESSAGE("(" + PatternGroup.SP + "(" + PatternGroup.CHARACTER + "){1,200})?");

    public static final String LETTER = "[A-Za-z]";
    public static final String DIGIT = "[0-9]";
    public static final String LETTER_DIGIT = LETTER + "|" + DIGIT;
    public static final String CRLF = "\\\\r\\\\n";
    public static final String CHARACTER = "\\p{Print}";                    // "[\\x20-\\xFF]"  //Tous les character imprimable + espace
    public static final String CHARACTER_PASS = "\\p{Graph}";               // "[\\x21-\\xFF]"  //Tous les character imprimable
    public static final String SP = "\\p{Space}";

    private String pattern;

    PatternGroup(String pattern){
        this.pattern = pattern;
    }

    public String getPattern() {
        return String.format("(?<%s>%s)", name(), pattern);
    }

    public static void main(String[] args) {
        Arrays.stream(PatternGroup.values()).map(PatternGroup::getPattern).forEach(System.out::println);
    }
}
