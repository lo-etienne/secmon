package be.flmr.secmon.core.pattern;

public interface IProtocolPacketBuilder {
    IProtocolPacket build();
    IProtocolPacketBuilder withGroup(IEnumPattern group, String value);
    IProtocolPacketBuilder withPatternType(IEnumPattern type);
}
