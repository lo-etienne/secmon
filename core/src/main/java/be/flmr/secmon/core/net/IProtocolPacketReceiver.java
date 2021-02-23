package be.flmr.secmon.core.net;

import be.flmr.secmon.core.pattern.ProtocolPacket;

public interface IProtocolPacketReceiver {
    ProtocolPacket receive();
}
