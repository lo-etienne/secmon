package be.flmr.secmon.core.net;

import be.flmr.secmon.core.pattern.IProtocolPacket;

public interface IProtocolPacketReceiver extends AutoCloseable {
    IProtocolPacket receive();
}
