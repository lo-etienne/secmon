package be.flmr.secmon.probe.protocols;

import be.flmr.secmon.core.patterns.DaemonProbeProtocolPatterns;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Group;
import be.flmr.secmon.core.router.Protocol;
import be.flmr.secmon.core.router.Router;

@Router
public class ProbeRouter extends AbstractRouter {

    @Protocol(pattern = DaemonProbeProtocolPatterns.CONFING)
    public void config(){

    }

   @Protocol(pattern = DaemonProbeProtocolPatterns.STATE_REQ)
   public void stateReq(@Group(groupName = "ID") String id){

    }

    @Protocol(pattern = DaemonProbeProtocolPatterns.STATE_RESP)
    public void stateResp(@Group(groupName = "ID") String id, @Group(groupName = "STATE") String state){

    }
}
