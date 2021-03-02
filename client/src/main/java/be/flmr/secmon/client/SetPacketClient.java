package be.flmr.secmon.client;

import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.PatternGroup;
import be.flmr.secmon.core.pattern.ProtocolPacketBuilder;
import be.flmr.secmon.core.pattern.ProtocolPattern;

import java.util.Map;

/**
 * Class qui permet de configure les protocolPacket
 */
public class SetPacketClient {

    /**
     * Methode permetant de creer un packet en fonction des valeur d'une map
     * @param map Qui contient les entree utilisateur qui permet de creer le packet
     * @return ProtocolPacket qui sert a l'echange d'info
     */
    public static final IProtocolPacket setPacket(final Map<String, String> map){
        IProtocolPacket packet = null;

        if(!(map.get("add_service_req").isEmpty())){
            packet = new ProtocolPacketBuilder()
                    .withPatternType(ProtocolPattern.ADD_SERVICE_REQ)
                    .withGroup(PatternGroup.AUGMENTEDURL,map.get("add_service_req"))
                    .build();
        }
        if(map.get("list_service_req").isEmpty()){
            packet = new ProtocolPacketBuilder()
                    .withPatternType(ProtocolPattern.LIST_SERVICE_REQ)
                    .build();
        }
        if(!(map.get("state_service_req").isEmpty())){
            packet = new ProtocolPacketBuilder()
                    .withPatternType(ProtocolPattern.STATE_SERVICE_REQ)
                    .withGroup(PatternGroup.ID,map.get("state_service_req"))
                    .build();
        }

        if(packet == null)throw new IllegalArgumentException();
        return packet;
    }
}
