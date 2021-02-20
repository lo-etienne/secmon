package be.flmr.secmon.core.net;

import be.flmr.secmon.core.patterns.PatternGroup;
import be.flmr.secmon.core.patterns.PatternUtils;

import java.util.HashMap;
import java.util.Map;

public class ProtocolPacket {
    private Map<PatternGroup, String> values;
    private String regex;

    public ProtocolPacket(String input, String regex, PatternGroup... groups) {
        values = new HashMap<>();
        this.regex = regex;

        for (PatternGroup group : groups) {
            String extractedValue = PatternUtils.extractGroup(input, regex, group.name());
            values.put(group, extractedValue);
        }
    }

    public String getValue(PatternGroup group) {
        return values.get(group);
    }

    public String buildMessage(String substituted) {

        return null;
    }

    protected Map<PatternGroup, String> getValues() {
        return values;
    }
}