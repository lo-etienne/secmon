package be.flmr.secmon.core.net;

import be.flmr.secmon.core.patterns.PatternGroup;
import be.flmr.secmon.core.patterns.PatternUtils;
import be.flmr.secmon.core.patterns.ProtocolPatternsGestionner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProtocolPacket {
    private Map<PatternGroup, String> values;
    private ProtocolPatternsGestionner protocol;

    public ProtocolPacket(String input) {
        values = new HashMap<>();
        this.protocol = ProtocolPatternsGestionner.getProtocol(input);

        for (PatternGroup group : protocol.getGroupProtocols()) {
            String extractedValue = PatternUtils.extractGroup(input, protocol.getPattern(), group.name());
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