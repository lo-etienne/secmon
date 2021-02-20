package be.flmr.secmon.core;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientDaemonProtocolPatternsTests {

    final Stream<Arguments> ADD_SERVICE_REQUESTCases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("ADDSRV LaP0mm3!P0mm3://Sart0:mdp1234@abc.def.123:55555/LesP0mm3s.com!30!250!600\\r\\n", true),
                Arguments.of("ADDSRV P0mm3://abc.def.123/LesP0mm3s.com \\r\\n", false),
                Arguments.of("ADDSRV LaP0mm3!P0mm3://Sart0:mdp1234@abc.def.123:55555/LesP0mm3s.com!30!250!600", false),
                Arguments.of("P0mm3://abc.def.123/LesP0mm3s.com\\r\\n", false),
                Arguments.of("POMME", false)
        );
    }

    @ParameterizedTest
    @MethodSource("ADD_SERVICE_REQUESTCases")
    final void stringMatchesADD_SERVICE_REQUEST(String s, boolean b) {
        assertRegex(ClientDaemonProtocolPatterns.ADD_SERVICE_REQUEST, s, b);
    }

    final Stream<Arguments> ADD_SERVICE_RESPONSECases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("+OK\\r\\n", true),
                Arguments.of("+OK Les pommes sont magiques\\r\\n", true),
                Arguments.of("-ERR\\r\\n", true),
                Arguments.of("-ERR Les pommes sont magiques\\r\\n", true),
                Arguments.of("POMME", false)
        );
    }

    @ParameterizedTest
    @MethodSource("ADD_SERVICE_RESPONSECases")
    final void stringMatchesADD_SERVICE_RESPONSE(String s, boolean b) {
        assertRegex(ClientDaemonProtocolPatterns.ADD_SERVICE_RESPONSE, s, b);
    }

    final Stream<Arguments> LIST_SERVICE_REQUESTCases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("LISTSRV\\r\\n", true),
                Arguments.of("LISTSRV \\r\\n", false),
                Arguments.of("\\r\\n", false)
        );
    }

    @ParameterizedTest
    @MethodSource("LIST_SERVICE_REQUESTCases")
    final void stringMatchesLIST_SERVICE_REQUEST(String s, boolean b) {
        assertRegex(ClientDaemonProtocolPatterns.LIST_SERVICE_REQUEST, s, b);
    }

    final Stream<Arguments> LIST_SERVICE_RESPONSECases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("SRV\\r\\n", true),
                Arguments.of("SRV 012246\\r\\n", true),
                Arguments.of("SRV 012246 047925\\r\\n", true),
                Arguments.of("SRV 5487523 5465458\\r\\n", true),
                Arguments.of("POMME", false)
        );
    }

    @ParameterizedTest
    @MethodSource("LIST_SERVICE_RESPONSECases")
    final void stringMatchesLIST_SERVICE_RESPONSE(String s, boolean b) {
        assertRegex(ClientDaemonProtocolPatterns.LIST_SERVICE_RESPONSE, s, b);
    }

    final Stream<Arguments> STATE_SERVICE_REQUESTCases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("STATESRV 012246\\r\\n", true),
                Arguments.of("STATESRV\\r\\n", false),
                Arguments.of("STATESRV012246\\r\\n", false),
                Arguments.of("STATESRV 0122462456545\\r\\n", false),
                Arguments.of("STATESRV 0122462 \\r\\n", false),
                Arguments.of("POMME", false)
        );
    }

    @ParameterizedTest
    @MethodSource("STATE_SERVICE_REQUESTCases")
    final void stringMatchesSTATE_SERVICE_REQUEST(String s, boolean b) {
        assertRegex(ClientDaemonProtocolPatterns.STATE_SERVICE_REQUEST, s, b);
    }

    final Stream<Arguments> STATE_SERVICE_RESPONSECases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("STATE 0122462 P0mm3://abc.def.123/LesP0mm3s.com OK\\r\\n", true),
                Arguments.of("STATE 12345 P0mm3://Sart0:mdp1234@abc.def.12355555/LesP0mm3s.com ALARM\\r\\n", true),
                Arguments.of("STATE 12345 P0mm3://Sart0:mdp1234@abc.def.12355555/LesP0mm3s.com POIRE\\r\\n", false),
                Arguments.of("STATE P0mm3://Sart0:mdp1234@abc.def.12355555/LesP0mm3s.com 12345 ALARM\\r\\n", false),
                Arguments.of("STATE12345 P0mm3://Sart0:mdp1234@abc.def.12355555/LesP0mm3s.com ALARM\\r\\n", false),
                Arguments.of("STATE 12345 ALARM\\r\\n", false),
                Arguments.of("POMME", false)
        );
    }

    @ParameterizedTest
    @MethodSource("STATE_SERVICE_RESPONSECases")
    final void stringMatchesSTATE_SERVICE_RESPONSE(String s, boolean b) {
        assertRegex(ClientDaemonProtocolPatterns.STATE_SERVICE_RESPONSE, s, b);
    }

    private void assertRegex(String regex, String sequence, boolean expected) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sequence);
        assertThat("Sequence " + sequence + " matches " + regex + ": " + expected, matcher.matches(), equalTo(expected));
    }
}
