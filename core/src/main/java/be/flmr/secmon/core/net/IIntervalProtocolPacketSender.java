package be.flmr.secmon.core.net;

import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.ProtocolPacket;

import java.util.concurrent.TimeUnit;

public interface IIntervalProtocolPacketSender {
    void sendWithInterval(final IProtocolPacket packet, final long timeOut, final TimeUnit unit);
}
