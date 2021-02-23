package be.flmr.secmon.core.pattern;

import java.util.HashMap;

/**
 * Classe permettant de construire une instance de {@link IProtocolPacket}
 */
public class ProtocolPacketBuilder implements IProtocolPacketBuilder {

    private IEnumPattern typeProtocol;
    private HashMap<IEnumPattern, String> groupWithVal = new HashMap<>();

    /**
     * Construit l'instance de {@link IProtocolPacket}
     * @return l'instance de {@link IProtocolPacket}
     */
    @Override
    public ProtocolPacket build() {
        ProtocolPacket packet = new ProtocolPacket();
        var classPacket = packet.getClass();

        try {
            var fieldProtocol = classPacket.getDeclaredField("protocol");
            fieldProtocol.setAccessible(true);
            fieldProtocol.set(packet, typeProtocol);
            fieldProtocol.setAccessible(false);

            var fieldMap = classPacket.getDeclaredField("values");
            fieldMap.setAccessible(true);
            fieldMap.set(packet,groupWithVal);
            fieldMap.setAccessible(false);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return packet;
    }

    /**
     * Ajoute une valeur dans le {@link IProtocolPacket} de type {@link IEnumPattern}
     * @param group le type de groupe
     * @param value la valeur
     * @return l'instance du builder
     */
    @Override
    public IProtocolPacketBuilder withGroup(IEnumPattern group, String value) {
        groupWithVal.put(group, value);
        return this;
    }

    /**
     * Spécifie le type de pattern du {@link IProtocolPacket}
     * @param type le type de pattern
     * @return l'instance du builder
     */
    @Override
    public IProtocolPacketBuilder withPatternType(IEnumPattern type) {
        this.typeProtocol = type;
        return this;
    }
}
