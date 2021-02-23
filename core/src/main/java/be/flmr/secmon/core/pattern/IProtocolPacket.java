package be.flmr.secmon.core.pattern;

public interface IProtocolPacket {
    String getValue(IEnumPattern pattern);
    void setValue(IEnumPattern pattern, String value);
    String buildMessage();
}
