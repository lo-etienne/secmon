package be.flmr.secmon.client.router;

import be.flmr.secmon.core.ClientDaemonProtocolPatterns;
import be.flmr.secmon.core.MulticastProtocolPatterns;
import be.flmr.secmon.core.router.Group;
import be.flmr.secmon.core.router.Protocol;

public class ClientRouter {

    @Protocol(pattern = ClientDaemonProtocolPatterns.ADD_SERVICE_REQUEST)
    public void addSrvRequest(@Group(groupName = "ID") String id,@Group(groupName = "PROTOCOL") String protocolUrl,
                              @Group(groupName = "HOST") String hostUrl,@Group(groupName = "PORT") String portUrl,
                              @Group(groupName = "PATH") String pathUrl,@Group(groupName = "MIN") String min,
                              @Group(groupName = "MAX") String max,@Group(groupName = "FREQUENCY") String frequency){

    }

    @Protocol(pattern = ClientDaemonProtocolPatterns.ADD_SERVICE_RESPONSE)
    public void addSrvResponse(@Group(groupName = "MESSAGE") String message){

    }

    @Protocol(pattern = ClientDaemonProtocolPatterns.LIST_SERVICE_REQUEST)
    public void listSrvRequest(){

    }

    @Protocol(pattern = ClientDaemonProtocolPatterns.LIST_SERVICE_RESPONSE)
    public void listSrvResponse(){

    }

    @Protocol(pattern = ClientDaemonProtocolPatterns.STATE_SERVICE_REQUEST)
    public void stateSrvRequest(@Group(groupName = "ID") String id){

    }

    @Protocol(pattern = ClientDaemonProtocolPatterns.STATE_SERVICE_RESPONSE)
    public void stateSrvResponse(@Group(groupName = "ID") String id,@Group(groupName = "PROTOCOL") String protocolUrl,
                                 @Group(groupName = "HOST") String hostUrl,@Group(groupName = "PORT") String portUrl,
                                 @Group(groupName = "PATH") String pathUrl){
    }
}
