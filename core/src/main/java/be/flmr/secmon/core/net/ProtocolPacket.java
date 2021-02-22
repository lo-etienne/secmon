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
        ProtocolPacketBuilder builder = new ProtocolPacketBuilder();
        var packet = builder.withType(ProtocolPatternsGestionner.STATE_RESP)
//                .with(PatternGroup.AUGMENTEDURL)
//                .with(PatternGroup.CONFIG)
//                .with(PatternGroup.FREQUENCY)
//                .with(PatternGroup.HOST)
//                .with(PatternGroup.ID)
//                .with(PatternGroup.MAX)
//                .with(PatternGroup.MESSAGE)
//                .with(PatternGroup.MIN)
//                .with(PatternGroup.OPTIONALMESSAGE)
//                .with(PatternGroup.PROTOCOL)
//                .with(PatternGroup.PASSWORD)
//                .with(PatternGroup.PATH)
//                .with(PatternGroup.PORT)
//                .with(PatternGroup.SRVLIST)
//                .with(PatternGroup.STATE)
//                .with(PatternGroup.URL)
//                .with(PatternGroup.USERNAME)
                .build();

        System.out.println(packet.buildMessage());
    }
}