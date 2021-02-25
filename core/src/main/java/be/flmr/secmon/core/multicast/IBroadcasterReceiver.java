package be.flmr.secmon.core.multicast;

import java.util.concurrent.Future;

public interface IBroadcasterReceiver {

    Future<String> receive();


}
