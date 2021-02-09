package be.flmr.secmon.core;

public final class ProtocolPatterns {
    public static final String LETTER = "[A-Za-z]";
    public static final String DIGIT = "\\d";
    public static final String LETTER_DIGIT = "[a-zA-Z0-9]";
    public static final String CRLF = "\\r\\n";
    public static final String PORT = "([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])";
    public static final String CHARACTER = "[\\x20-\\xFF]";

    private ProtocolPatterns(){

    }
}
