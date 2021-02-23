package be.flmr.secmon.core.pattern;

public interface IProtocolPacketBuilder {
    IProtocolPacket build();
    IProtocolPacketBuilder with(IEnumPattern group, String value);
    IProtocolPacketBuilder withType(IEnumPattern type);
}
