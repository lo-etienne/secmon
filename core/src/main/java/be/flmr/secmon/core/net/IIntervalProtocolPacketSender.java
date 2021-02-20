package be.flmr.secmon.core.net;

import java.util.concurrent.TimeUnit;

public interface IIntervalProtocolPacketSender {
    void sendWithInterval(final ProtocolPacket packet, final long timeOut, final TimeUnit unit);
}
