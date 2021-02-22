package be.flmr.secmon.core.net;

import be.flmr.secmon.core.patterns.PatternGroup;
import be.flmr.secmon.core.patterns.ProtocolPatternsGestionner;

public interface IProtocolPacketBuilder {

    ProtocolPacket build();
    IProtocolPacketBuilder with(PatternGroup group, String value);
    IProtocolPacketBuilder withType(ProtocolPatternsGestionner type);
}
