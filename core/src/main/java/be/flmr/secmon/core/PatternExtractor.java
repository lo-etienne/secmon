package be.flmr.secmon.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface PatternExtractor {
    String getPattern();

    default String extract(String sequence, String group) {
        try {
            Pattern pattern = Pattern.compile(getPattern());
            Matcher matcher = pattern.matcher(sequence);
            return matcher.group(group);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
