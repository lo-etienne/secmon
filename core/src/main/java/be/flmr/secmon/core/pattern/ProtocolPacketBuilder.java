package be.flmr.secmon.core.pattern;

import java.util.HashMap;

public class ProtocolPacketBuilder implements IProtocolPacketBuilder {

    private IEnumPattern typeProtocol;
    private HashMap<IEnumPattern, String> groupWithVal = new HashMap<>();

    @Override
    public ProtocolPacket build() {
        ProtocolPacket packet = new ProtocolPacket();
        var classPacket = packet.getClass();

        try {
            var fieldProtocol = classPacket.getDeclaredField("protocol");
            fieldProtocol.setAccessible(true);
            fieldProtocol.set(packet, typeProtocol);
            fieldProtocol.setAccessible(false);

            var fieldMap = classPacket.getDeclaredField("values");
            fieldMap.setAccessible(true);
            fieldMap.set(packet,groupWithVal);
            fieldMap.setAccessible(false);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return packet;
    }

    @Override
    public IProtocolPacketBuilder with(IEnumPattern group, String value) {
        groupWithVal.put(group, value);
        return this;
    }

    @Override
    public IProtocolPacketBuilder withType(IEnumPattern type) {
        this.typeProtocol = type;
        return this;
    }
}
