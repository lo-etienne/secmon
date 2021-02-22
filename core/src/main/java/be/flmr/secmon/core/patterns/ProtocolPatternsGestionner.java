package be.flmr.secmon.core.patterns;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public enum ProtocolPatternsGestionner {
    ADD_SERVICE_REQ("ADDSRV" + PatternGroup.SP + PatternGroup.AUGMENTEDURL + PatternGroup.CRLF),
    ADD_SERVICE_RESP_OK("\\+OK" + "(" + PatternGroup.SP + PatternGroup.MESSAGE + ")?" + PatternGroup.CRLF),
    ADD_SERVICE_RESP_ERR("-ERR" + "(" + PatternGroup.SP + PatternGroup.MESSAGE + ")?" + PatternGroup.CRLF),
    LIST_SERVICE_REQ("LISTSRV" + PatternGroup.CRLF),
    LIST_SERVICE_RESP("SRV(" + PatternGroup.SP + PatternGroup.ID + "){0,100}" + PatternGroup.CRLF),
    STATE_SERVICE_REQ("STATESRV" + PatternGroup.SP + PatternGroup.ID + PatternGroup.CRLF),
    STATE_SERVICE_RESP("STATE" + PatternGroup.SP + PatternGroup.ID + PatternGroup.SP + PatternGroup.URL
            + PatternGroup.SP + PatternGroup.STATE + PatternGroup.CRLF),

    CONFIG("CURCONFIG(" + PatternGroup.SP + PatternGroup.AUGMENTEDURL + "){0,100}" + PatternGroup.CRLF),
    STATE_REQ("STATEREQ" + PatternGroup.SP + PatternGroup.ID + PatternGroup.CRLF),
    STATE_RESP("STATERESP" + PatternGroup.SP + PatternGroup.ID + PatternGroup.SP + PatternGroup.STATE + PatternGroup.CRLF),

    ANNOUNCE("IAMHERE" + PatternGroup.SP + PatternGroup.PROTOCOL + PatternGroup.SP + PatternGroup.PORT + PatternGroup.CRLF),
    NOTIFICATION("NOTIFY" + PatternGroup.SP + PatternGroup.PROTOCOL + PatternGroup.SP + PatternGroup.PORT + PatternGroup.CRLF);

    private final String pattern;
    private final List<PatternGroup> groupProtocols;

    ProtocolPatternsGestionner(String pattern) {
        this.pattern = pattern;
        this.groupProtocols = new ArrayList<>();
        var matcher = Pattern.compile("\\(\\?<(?<group>\\w+)>.*\\)").matcher(pattern);
        if (!matcher.matches()) throw new IllegalArgumentException();
        while(matcher.find()){
            groupProtocols.add(PatternGroup.valueOf(matcher.group("group")));
        }
    }

    public String getPattern() {
        return this.pattern;
    }

    public List<PatternGroup> getGroupProtocols() {
        return ImmutableList.copyOf(groupProtocols);
    }

    public static ProtocolPatternsGestionner getProtocol(String input) {
        for (ProtocolPatternsGestionner protocol : ProtocolPatternsGestionner.values()) {
            if (Pattern.compile(protocol.pattern).matcher(input).matches()) return protocol;
        }
        throw new IllegalArgumentException("Le paramettre ne correspont pas!");
    }
}
