package be.flmr.secmon.core.pattern;

import java.util.*;

/**
 * Classe "data" contenant les données d'un message reçu ou d'un message à envoyer. Permet de construire un message à
 * partir de données, tel que PORT: 161 ou alors PROTOCOL: snmp. Et permet de construire des données à partir d'un
 * message, tel que IAMHERE snmp 161.
 */
public class ProtocolPacket implements IProtocolPacket {
    private Map<IEnumPattern, String> values;
    private ProtocolPattern protocol;
    private String message;

    public ProtocolPacket() {}

    /**
     * Crée une instance de {@link ProtocolPacket} à partir d'un message.
     * @param message le message (i.e. {@code IAMHERE snmp 161})
     * @return l'instance de protocol pattern avec les valeurs spécifiées.
     */
    public static ProtocolPacket from(String message) {
        ProtocolPacket packet = new ProtocolPacket();
        packet.message = message;
        packet.values = new HashMap<>();
        packet.protocol = ProtocolPattern.getProtocol(message);

        for (PatternGroup group : packet.protocol.getGroupProtocols()) {
            String extractedValue = PatternUtils.extractGroup(message, packet.protocol.getPattern(), group.name());
            packet.values.put(group, extractedValue);
        }
        return packet;
    }

    /**
     * Renvoie la valeur d'un groupe. (i.e. {@code getValue({@link PatternGroup.PORT}) -> 161}
     * @param group le type de valeur (i.e. {@link PatternGroup.PORT}
     * @return sa valeur (i.e. 161)
     */
    @Override
    public String getValue(IEnumPattern group) {
        return values.get(group);
    }

    @Override
    public void setValue(IEnumPattern pattern, String value) {
        values.put(pattern, value);
    }

    /**
     * Construit le message du packet à partir de valeurs.
     * @return le message (i.e. IAMHERE snmp 161)
     * @throws NullPointerException si une valeur de groupe n'as pas été spécifiée (même si elle est vide).
     */
    @Override
    public String buildMessage() {
        List<PatternGroup> order = protocol.getGroupProtocols();
        List<String> orderedValues = new ArrayList<>();

        for (PatternGroup group : order) {
            String value = values.get(group);
            if (value == null)
                throw new NullPointerException("La valeur pour le groupe " + group + " est nulle. Veuillez la spécifier même si elle doit être vide.");
            orderedValues.add(value);
        }

        return (message = protocol.buildMessage(orderedValues));
    }

    @Override
    public IEnumPattern getType() {
        return protocol;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Renvoie la map des valeurs
     * @return la map des valeurs
     */
    protected Map<IEnumPattern, String> getValues() {
        return values;
    }
}