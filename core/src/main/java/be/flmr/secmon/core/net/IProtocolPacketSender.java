package be.flmr.secmon.core.net;

import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.ProtocolPacket;

public interface IProtocolPacketSender {
    void send(final IProtocolPacket packet);
}