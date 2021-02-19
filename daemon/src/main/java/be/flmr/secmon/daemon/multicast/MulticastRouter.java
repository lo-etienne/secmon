package be.flmr.secmon.daemon.multicast;

import be.flmr.secmon.core.MulticastProtocolPatterns;
import be.flmr.secmon.core.multicast.IBroadcasterReceiver;
import be.flmr.secmon.core.router.Group;
import be.flmr.secmon.core.router.Protocol;

public class MulticastRouter {

    @Protocol(pattern = MulticastProtocolPatterns.ANNOUCE)
    public void annouce(@Group(groupName = "PROTOCOL") String protocol, @Group(groupName = "PORT") String port){

    }

    @Protocol(pattern = MulticastProtocolPatterns.NOTIFICATION)
    public void notification(@Group(groupName = "PROTOCOL") String protocol, @Group(groupName = "PORT") String port){

    }
}
