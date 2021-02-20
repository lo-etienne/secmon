package be.flmr.secmon.core.net;

public interface IProtocolPacketSender {
    void send(final ProtocolPacket packet);
}
