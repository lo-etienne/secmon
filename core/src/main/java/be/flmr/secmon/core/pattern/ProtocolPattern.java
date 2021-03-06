package be.flmr.secmon.core.pattern;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static be.flmr.secmon.core.pattern.PatternGroup.CRLF;
import static be.flmr.secmon.core.pattern.PatternGroup.SP;

/**
 * Énumération regroupant les différentes définitions des patterns utilisés par l'application
 */
public enum ProtocolPattern implements IEnumPattern {
    ADD_SERVICE_REQ("ADDSRV", PatternGroup.AUGMENTEDURL),
    ADD_SERVICE_RESP_OK("\\+OK", false, PatternGroup.OPTIONALMESSAGE),
    ADD_SERVICE_RESP_ERR("-ERR", false, PatternGroup.OPTIONALMESSAGE),
    LIST_SERVICE_REQ("LISTSRV"),
    LIST_SERVICE_RESP("SRV", false, PatternGroup.SRVLIST),
    STATE_SERVICE_REQ("STATESRV", PatternGroup.ID),
    STATE_SERVICE_RESP("STATE", PatternGroup.ID, PatternGroup.URL, PatternGroup.STATE),

    CONFIG("CURCONFIG", false, PatternGroup.CONFIG),
    STATE_REQ("STATEREQ", PatternGroup.ID),
    STATE_RESP("STATERESP", PatternGroup.ID, PatternGroup.STATE),

    ANNOUNCE("IAMHERE", PatternGroup.PROTOCOL, PatternGroup.PORT),
    NOTIFICATION("NOTIFY", PatternGroup.PROTOCOL, PatternGroup.PORT);

    private final String prefix;
    private final List<PatternGroup> groupProtocols;
    private final boolean automaticSpace;
    private final boolean automaticCRLF;

    ProtocolPattern(String prefix, boolean automaticSpace, boolean automaticCRLF, PatternGroup... groups) {
        this.prefix = prefix;
        groupProtocols = List.of(groups);
        this.automaticCRLF = automaticCRLF;
        this.automaticSpace = automaticSpace;
    }

    ProtocolPattern(String prefix, boolean automaticSpace, PatternGroup... groups) {
        this(prefix, automaticSpace, true, groups);
    }

    ProtocolPattern(String prefix, PatternGroup... groups) {
        this(prefix, true, true, groups);
    }

    /**
     * Retourne le pattern de l'instance créés de façon "lazy" grâce aux PatternGroups.
     * @return le pattern de l'instance
     */
    @Override
    public String getPattern() {
        return prefix + groupProtocols.stream()
                .filter(Objects::nonNull)
                .map(IEnumPattern::getPattern)
                .reduce("" , (ac, pattern) -> ac + (automaticSpace ? SP.getPattern() : "") + pattern)
                + (automaticCRLF ? CRLF.getPattern() : "");
    }

    /**
     * Retourne les différents groupes utilisés par le pattern en particulier
     * @return une liste des groupes utilisés par le pattern
     */
    public List<PatternGroup> getGroupProtocols() {
        return ImmutableList.copyOf(groupProtocols);
    }

    /**
     * Construit le message en substituant les groupes par leur valeurs.
     * @param values la liste des valeurs dans l'ordre
     * @return le message à envoyer.
     */
    public String buildMessage(List<String> values) {
        String prefix = this.prefix.replace("\\", "");
        return prefix + values.stream()
                .filter(Objects::nonNull)
                .reduce("", (ac, value) -> ac + " " + value) + "\r\n";
    }

    /**
     * Détecte à quel protocol l'input appartient
     * @param input un message reçu
     * @return le protocol auquel l'input appartient.
     */
    public static ProtocolPattern getProtocol(String input) {
        for (ProtocolPattern protocol : ProtocolPattern.values()) {
            if (input.matches(protocol.getPattern())) return protocol;
        }
        throw new IllegalArgumentException(String.format("Le paramètre %s ne correspond à aucun patterns !", input));
    }

    public static void main(String[] args) {
        Arrays.stream(ProtocolPattern.values())
                .forEach(p -> System.out.printf("%s: %s\n", p.name(), p.getPattern().replace("\\", "\\\\")));
    }
}
