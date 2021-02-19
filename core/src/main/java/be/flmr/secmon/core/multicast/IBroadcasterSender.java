package be.flmr.secmon.core.multicast;

import java.util.concurrent.TimeUnit;

public interface IBroadcasterSender {

    void send(final String message);
    void sendWithInterval(final String message, final int timeOut, final TimeUnit unit);


}
