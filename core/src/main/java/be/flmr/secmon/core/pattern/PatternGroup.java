package be.flmr.secmon.core.pattern;

import be.flmr.secmon.core.net.ServiceState;

import java.util.Arrays;

/**
 * Énumération comprenant les différents "groupes" de patterns défninis dans les "définitions standards". Ces "groupes"
 * représentent, pour la majorité, des patterns "groupés" (i.e. (?&lt;nom de groupe&gt;*pattern*)).
 */
public enum PatternGroup implements IEnumPattern {
    SP("\\p{Space}", true),
    CRLF("\\r\\n", true),

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
    STATE(Arrays.stream(ServiceState.values()).map(ServiceState::name).reduce("", (a,b) -> a + (a.isEmpty() ? "" : "|") + b)),
    MESSAGE("(" + PatternGroup.CHARACTER + "){1,200}"),
    URL(PROTOCOL.getPattern() + "://" + "(" + USERNAME.getPattern() + "(:" + PASSWORD.getPattern()
            + ")?" + "@)?" + HOST.getPattern() + "(:" + PORT.getPattern() + ")?" + PATH.getPattern()),
    AUGMENTEDURL(ID.getPattern() + "!" + URL.getPattern() + "!" + MIN.getPattern() + "!" + MAX.getPattern() + "!" + FREQUENCY.getPattern()),
    CONFIG("(" + SP.getPattern() + PatternGroup.AUGMENTEDURL.getPattern() + "){0,100}"),
    SRVLIST("(" + SP.getPattern() + PatternGroup.ID.getPattern() + "){0,100}"),
    OPTIONALMESSAGE("(" + SP.getPattern() + "(" + PatternGroup.CHARACTER + "){1,200})?");

    public static final String LETTER = "[A-Za-z]";
    public static final String DIGIT = "[0-9]";
    public static final String LETTER_DIGIT = LETTER + "|" + DIGIT;
    public static final String CHARACTER = "\\p{Print}";                    // "[\\x20-\\xFF]"  //Tous les character imprimable + espace
    public static final String CHARACTER_PASS = "\\p{Graph}";               // "[\\x21-\\xFF]"  //Tous les character imprimable

    /**
     * Le pattern interne au groupe
     */
    private String pattern;

    /**
     * Si le groupe est "spécial" ou non. Cela permet d'ajouter SP et CRLF dans les "groupes" sans qu'ils aient des
     * noms de groupe regex, ce qui lèvra une erreur lors de {@code Pattern::compile}
     */
    private boolean special;

    PatternGroup(String pattern, boolean special) {
        this.pattern = pattern;
        this.special = special;
    }

    PatternGroup(String pattern) {
        this(pattern, false);
    }

    /**
     * Construit le pattern à partir du pattern interne à l'instance de l'énum. Le nom du groupe est le nom de la
     * constante énumérée
     * @return le pattern de l'instance de l'énum
     */
    @Override
    public String getPattern() {
        return special ? pattern : String.format("(?<%s>%s)", name(), pattern);
    }

    public static void main(String[] args) {
        Arrays.stream(PatternGroup.values())
                .forEach(p -> System.out.printf("%s: %s\n", p.name(), p.getPattern().replace("\\", "\\\\")));
    }
}
