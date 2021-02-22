package be.flmr.secmon.core.net;

import be.flmr.secmon.core.patterns.PatternGroup;
import be.flmr.secmon.core.patterns.ProtocolPatternsGestionner;

import java.util.HashMap;

public class ProtocolPacketBuilder implements IProtocolPacketBuilder{

    private ProtocolPatternsGestionner typeProtocol;
    private HashMap<PatternGroup, String> groupWithVal = new HashMap<PatternGroup, String>();

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

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return packet;
    }

    @Override
    public IProtocolPacketBuilder with(PatternGroup group, String value) {
        groupWithVal.put(group, value);
        return this;
    }

    @Override
    public IProtocolPacketBuilder withType(ProtocolPatternsGestionner type) {
        this.typeProtocol = type;
        return this;
    }
}
