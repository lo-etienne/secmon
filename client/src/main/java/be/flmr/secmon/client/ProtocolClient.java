package be.flmr.secmon.client;

import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.PatternGroup;
import be.flmr.secmon.core.pattern.PatternUtils;
import be.flmr.secmon.core.pattern.ProtocolPattern;
import be.flmr.secmon.core.router.AbstractRouter;
import be.flmr.secmon.core.router.Protocol;

import java.io.PrintStream;

/**
 *Class qui verifie les reponse du routeur et affiche une commande en fonction d'un packet
 */
public class ProtocolClient extends AbstractRouter {

    private PrintStream stream;

    public ProtocolClient(PrintStream writer){
        super();
        this.stream = writer;
    }

    @Protocol(pattern = ProtocolPattern.ADD_SERVICE_RESP_OK)
    private void respondServiceOk(Object obj, IProtocolPacket packet){
        String str = "+OK";

        if(!(packet.getValue(PatternGroup.MESSAGE) == null)){
            str += packet.getValue(PatternGroup.MESSAGE);
        }

        stream.println(str);
    }

    @Protocol(pattern = ProtocolPattern.ADD_SERVICE_RESP_ERR)
    private void respondServiceError(Object obj, IProtocolPacket packet){
        String str = "-ERR";

        if(!(packet.getValue(PatternGroup.MESSAGE) == null)){
            str += packet.getValue(PatternGroup.MESSAGE);
        }

        stream.println(str);
    }

    @Protocol(pattern = ProtocolPattern.LIST_SERVICE_RESP)
    private void respondList(Object obj, IProtocolPacket packet){

        var list = PatternUtils.findGroups(packet.getMessage(),PatternGroup.ID);

        String str = list.stream().reduce("SRV", (a, b) -> a + " " + b);

        stream.println(str);
    }

    @Protocol(pattern = ProtocolPattern.STATE_SERVICE_RESP)
    private void respondState(Object obj, IProtocolPacket packet){
        String str = "STATE ";

        str += packet.getValue(PatternGroup.ID) + " ";
        str += packet.getValue(PatternGroup.URL) + " ";
        str += packet.getValue(PatternGroup.STATE);

        stream.println(str);
    }
}
