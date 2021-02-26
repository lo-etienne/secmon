package be.flmr.secmon.core.pattern;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PatternUtils {
    public static String extractGroup(String sequence, String regex, String group) {
        var matcher = getMatcher(sequence, regex);
        if (!matcher.matches()) throw new IllegalArgumentException("La séquence ne match pas avec le pattern spécifié");
        return matcher.group(group);
    }

    public static List<String> findGroups(String sequence, PatternGroup group) {
        List<String> list = new ArrayList<>();
        var matcher = getMatcher(sequence, group.getPattern());
        while (matcher.find()) {
            list.add(matcher.group(group.name()));
        }
        return ImmutableList.copyOf(list);
    }

    public static String extractGroup(String sequence, IEnumPattern pattern, String group) {
        return extractGroup(sequence, pattern.getPattern(), group);
    }

    private static Matcher getMatcher(String sequence, String regex) { return Pattern.compile(regex).matcher(sequence); }
}
