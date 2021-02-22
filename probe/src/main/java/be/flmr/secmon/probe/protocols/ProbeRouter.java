package be.flmr.secmon.probe.protocols;

import be.flmr.secmon.core.patterns.ProtocolPatternsGestionner;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Group;
import be.flmr.secmon.core.router.Protocol;
import be.flmr.secmon.core.router.Router;

@Router
public class ProbeRouter extends AbstractRouter {

    //@Protocol(pattern = ProtocolPatternsGestionner.CONFIG)
    public void config(){

    }

   //@Protocol(pattern = ProtocolPatternsGestionner.STATE_REQ)
   public void stateReq(@Group(groupName = "ID") String id){

    }

    //@Protocol(pattern = ProtocolPatternsGestionner.STATE_RESP)
    public void stateResp(@Group(groupName = "ID") String id, @Group(groupName = "STATE") String state){

    }
}
