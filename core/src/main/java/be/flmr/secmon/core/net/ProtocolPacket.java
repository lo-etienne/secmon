package be.flmr.secmon.core.net;

import be.flmr.secmon.core.patterns.PatternGroup;
import be.flmr.secmon.core.patterns.PatternUtils;
import be.flmr.secmon.core.patterns.ProtocolPatternsGestionner;

import java.util.*;

public class ProtocolPacket {
    private Map<PatternGroup, String> values;
    private ProtocolPatternsGestionner protocol;

    public ProtocolPacket(){}

    public String getValue(PatternGroup group) {
        return values.get(group);
    }

    public String buildMessage() {
        List<PatternGroup> order = protocol.getGroupProtocols();
        List<String> orderedValues = new ArrayList<>();

        order.forEach(group -> {
            values.forEach((key, value) -> {
                if (group == key) orderedValues.add(value);
            });
        });

        return protocol.buildMessage(orderedValues);
    }

    protected Map<PatternGroup, String> getValues() {
        return values;
    }

    public static void main(String[] args) {

        Arrays.stream(ProtocolPatternsGestionner.values())
                .forEach(ProtocolPacket::displayGroup);
    }

    private static void displayGroup(ProtocolPatternsGestionner group) {
        ProtocolPacketBuilder builder = new ProtocolPacketBuilder();

        var packet = builder.withType(group)
                .with(PatternGroup.AUGMENTEDURL, PatternGroup.AUGMENTEDURL.name())
                .with(PatternGroup.CONFIG, PatternGroup.CONFIG.name())
                .with(PatternGroup.FREQUENCY, PatternGroup.FREQUENCY.name())
                .with(PatternGroup.HOST, PatternGroup.HOST.name())
                .with(PatternGroup.ID, PatternGroup.ID.name())
                .with(PatternGroup.MAX, PatternGroup.MAX.name())
                .with(PatternGroup.MESSAGE, PatternGroup.MESSAGE.name())
                .with(PatternGroup.MIN, PatternGroup.MIN.name())
                .with(PatternGroup.OPTIONALMESSAGE, PatternGroup.OPTIONALMESSAGE.name())
                .with(PatternGroup.PROTOCOL, PatternGroup.PROTOCOL.name())
                .with(PatternGroup.PASSWORD, PatternGroup.PASSWORD.name())
                .with(PatternGroup.PATH, PatternGroup.PATH.name())
                .with(PatternGroup.PORT, PatternGroup.PORT.name())
                .with(PatternGroup.SRVLIST, PatternGroup.SRVLIST.name())
                .with(PatternGroup.STATE, PatternGroup.STATE.name())
                .with(PatternGroup.URL, PatternGroup.URL.name())
                .with(PatternGroup.USERNAME, PatternGroup.USERNAME.name())
                .build();

        System.out.println(packet.buildMessage());
    }
}