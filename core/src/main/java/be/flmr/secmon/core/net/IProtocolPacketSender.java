package be.flmr.secmon.core.net;

import be.flmr.secmon.core.pattern.IProtocolPacket;

public interface IProtocolPacketSender {
    void send(final IProtocolPacket packet);
}
