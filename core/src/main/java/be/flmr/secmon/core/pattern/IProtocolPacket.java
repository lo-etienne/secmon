package be.flmr.secmon.core.pattern;

import java.net.InetAddress;

public interface IProtocolPacket {
    String getValue(IEnumPattern pattern);
    void setValue(IEnumPattern pattern, String value);
    String buildMessage();

    IEnumPattern getType();

    InetAddress getSourceAddress();

    String getMessage();
}
