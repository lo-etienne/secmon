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
public class DaemonProbeProtocolPatternsTest {

    final Stream<Arguments> CONFIGCases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("CURCONFIG LaP0mm3!P0mm3://Sart0:mdp1234@abc.def.123:55555/LesP0mm3s.com!30!250!600\\r\\n", true),
                Arguments.of("CURCONFIG LaP0mm3!P0mm3://Sart0:mdp1234@abc.def.123:55555/LesP0mm3s.com!30!250!600 LaP0mm3!P0mm3://Sart0:mdp1234@abc.def.123:55555/LesP0mm3s.com!30!250!600\\r\\n", true),
                Arguments.of("CURCONFIG P0mm3://abc.def.123/LesP0mm3s.com \\r\\n", false),
                Arguments.of("CURCONFIG LaP0mm3!P0mm3://Sart0:mdp1234@abc.def.123:55555/LesP0mm3s.com!30!250!600", false),
                Arguments.of("P0mm3://abc.def.123/LesP0mm3s.com\\r\\n", false),
                Arguments.of("POMME", false)
        );
    }

    @ParameterizedTest
    @MethodSource("CONFIGCases")
    final void stringMatchesCONFIG(String s, boolean b) {
        assertRegex(DaemonProbeProtocolPatterns.CONFING, s, b);
    }

    final Stream<Arguments> STATREQCases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("STATEREQ pommme\\r\\n", true),
                Arguments.of("STATEREQ 12345\\r\\n", true),
                Arguments.of("STATEREQ pomme15\\r\\n", true),
                Arguments.of("STATEREQ 0000000000000000000\\r\\n", false),
                Arguments.of("STATEREQ jnkadgfnjgfadjnadfgnjjnadgfbnj", false),
                Arguments.of("POMME", false)
        );
    }

    @ParameterizedTest
    @MethodSource("STATREQCases")
    final void stringMatchesSTATREQ(String s, boolean b) {
        assertRegex(DaemonProbeProtocolPatterns.STATE_REQ, s, b);
    }

    final Stream<Arguments> STATRESPCases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("STATERESP pommme OK\\r\\n", true),
                Arguments.of("STATERESP 12345 ALARM\\r\\n", true),
                Arguments.of("STATERESP pomme15 DOWN\\r\\n", true),
                Arguments.of("STATERESP pomme15 POMME\\r\\n", false),
                Arguments.of("STATERESP 0000000000000000000 OK\\r\\n", false),
                Arguments.of("STATERESP jnkadgfnjgfadjnadfgnjjnadgfbnj", false),
                Arguments.of("POMME", false)
        );
    }

    @ParameterizedTest
    @MethodSource("STATRESPCases")
    final void stringMatchesSTATRESP(String s, boolean b) {
        assertRegex(DaemonProbeProtocolPatterns.STATE_RESP, s, b);
    }

    private void assertRegex(String regex, String sequence, boolean expected) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sequence);
        assertThat("Sequence " + sequence + " matches " + regex + ": " + expected, matcher.matches(), equalTo(expected));
    }
}
