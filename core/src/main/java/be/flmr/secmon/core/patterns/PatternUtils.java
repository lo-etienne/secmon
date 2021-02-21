package be.flmr.secmon.core.patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PatternUtils {
    public static String extractGroup(String sequence, String regex, String group) {
        var matcher = getMatcher(sequence, regex);
        if (!matcher.matches()) throw new IllegalArgumentException();
        return matcher.group(group);
    }

    private static Matcher getMatcher(String sequence, String regex) { return Pattern.compile(regex).matcher(sequence); }
}
