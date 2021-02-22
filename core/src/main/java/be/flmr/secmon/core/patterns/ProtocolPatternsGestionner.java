package be.flmr.secmon.core.patterns;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public enum ProtocolPatternsGestionner {
    ADD_SERVICE_REQ("ADDSRV" + ProtocolPatterns.SP + ProtocolPatterns.AUGMENTED_URL + ProtocolPatterns.CRLF),
    ADD_SERVICE_RESP("(\\+OK|-ERR)" + "(" + ProtocolPatterns.SP + ProtocolPatterns.MESSAGE + ")?" + ProtocolPatterns.CRLF),
    LIST_SERVICE_REQ("LISTSRV" + ProtocolPatterns.CRLF),
    LIST_SERVICE_RESP("SRV(" + ProtocolPatterns.SP + ProtocolPatterns.ID + "){0,100}" + ProtocolPatterns.CRLF),
    STATE_SERVICE_REQ("STATESRV" + ProtocolPatterns.SP + ProtocolPatterns.ID + ProtocolPatterns.CRLF),
    STATE_SERVICE_RESP("STATE" + ProtocolPatterns.SP + ProtocolPatterns.ID + ProtocolPatterns.SP + ProtocolPatterns.URL
            + ProtocolPatterns.SP + ProtocolPatterns.STATE + ProtocolPatterns.CRLF),

    CONFIG("CURCONFIG(" + ProtocolPatterns.SP + ProtocolPatterns.AUGMENTED_URL + "){0,100}" + ProtocolPatterns.CRLF),
    STATE_REQ("STATEREQ" + ProtocolPatterns.SP + ProtocolPatterns.ID + ProtocolPatterns.CRLF),
    STATE_RESP("STATERESP" + ProtocolPatterns.SP + ProtocolPatterns.ID + ProtocolPatterns.SP + ProtocolPatterns.STATE + ProtocolPatterns.CRLF),

    ANNOUNCE("IAMHERE" + ProtocolPatterns.SP + ProtocolPatterns.PROTOCOL + ProtocolPatterns.SP + ProtocolPatterns.PORT + ProtocolPatterns.CRLF),
    NOTIFICATION("NOTIFY" + ProtocolPatterns.SP + ProtocolPatterns.PROTOCOL + ProtocolPatterns.SP + ProtocolPatterns.PORT + ProtocolPatterns.CRLF);

    private String pattern;
    private List<PatternGroup> groupProtocols;

    private ProtocolPatternsGestionner(String pattern) {
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

    @NotNull
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
