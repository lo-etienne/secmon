package be.flmr.secmon.core.pattern;

public interface IProtocolPacket {
    String getValue(IEnumPattern pattern);
    String buildMessage();
}
