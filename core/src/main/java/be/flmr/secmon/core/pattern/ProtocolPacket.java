package be.flmr.secmon.core.pattern;

import java.util.*;

public class ProtocolPacket {
    private Map<IEnumPattern, String> values;
    private ProtocolPattern protocol;

    public ProtocolPacket() {}

    public static ProtocolPacket from(String message) {
        ProtocolPacket packet = new ProtocolPacket();
        packet.values = new HashMap<>();
        packet.protocol = ProtocolPattern.getProtocol(message);

        for (PatternGroup group : packet.protocol.getGroupProtocols()) {
            String extractedValue = PatternUtils.extractGroup(message, packet.protocol.getPattern(), group.name());
            packet.values.put(group, extractedValue);
        }
        return packet;
    }

    public String getValue(IEnumPattern group) {
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

    protected Map<IEnumPattern, String> getValues() {
        return values;
    }

    public static void main(String[] args) {
        Arrays.stream(ProtocolPattern.values())
                .forEach(ProtocolPacket::displayGroup);
    }

    private static void displayGroup(ProtocolPattern group) {
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