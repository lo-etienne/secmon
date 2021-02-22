package be.flmr.secmon.core.patterns;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public enum ProtocolPatternsGestionner {
    ADD_SERVICE_REQ("ADDSRV", PatternGroup.AUGMENTEDURL),
    ADD_SERVICE_RESP_OK("\\+OK", PatternGroup.OPTIONALMESSAGE),
    ADD_SERVICE_RESP_ERR("-ERR", PatternGroup.OPTIONALMESSAGE),
    LIST_SERVICE_REQ("LISTSRV"),
    LIST_SERVICE_RESP("SRV", PatternGroup.SRVLIST),
    STATE_SERVICE_REQ("STATESRV", PatternGroup.ID),
    STATE_SERVICE_RESP("STATE", PatternGroup.ID, PatternGroup.URL, PatternGroup.STATE),

    CONFIG("CURCONFIG", PatternGroup.CONFIG),
    STATE_REQ("STATEREQ", PatternGroup.ID),
    STATE_RESP("STATERESP", PatternGroup.ID, PatternGroup.STATE),

    ANNOUNCE("IAMHERE", PatternGroup.PROTOCOL, PatternGroup.PORT),
    NOTIFICATION("NOTIFY", PatternGroup.PROTOCOL, PatternGroup.PORT);

    private final String prefix;
    private final List<PatternGroup> groupProtocols;

    ProtocolPatternsGestionner(String prefix, PatternGroup... groups) {
        this.prefix = prefix;
        groupProtocols = List.of(groups);

    }

    public String getPattern() {
        return prefix + groupProtocols.stream()
                .filter(Objects::nonNull)
                .map(PatternGroup::getPattern)
                .reduce("" , (str, pattern) -> str + PatternGroup.SP + pattern) + PatternGroup.CRLF;
    }

    public List<PatternGroup> getGroupProtocols() {
        return ImmutableList.copyOf(groupProtocols);
    }

    public String buildMessage(List<String> values) {
        return prefix + values.stream()
                .filter(Objects::nonNull)
                .reduce("", (str, value) -> str + " " + value) + "\r\n";
    }

    public static void main(String[] args) {
        Arrays.stream(ProtocolPatternsGestionner.values())
                .map(ProtocolPatternsGestionner::getPattern)
                .forEach(System.out::println);
    }
}
