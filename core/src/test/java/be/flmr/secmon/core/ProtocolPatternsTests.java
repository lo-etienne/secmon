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
public class ProtocolPatternsTests {

    final Stream<Arguments> letterCases() {
        return Stream.of(
                Arguments.of('a', true),
                Arguments.of('A', true),
                Arguments.of('@', false),
                Arguments.of('5', false),
                Arguments.of('(', false),
                Arguments.of('z', true),
                Arguments.of('e', true)
        );
    }

    @ParameterizedTest
    @MethodSource("letterCases")
    final void letterPatternMatchesAllAlphabeticalCharacters(char c, boolean b) {
        assertRegex(ProtocolPatterns.LETTER, String.format("%c", c), b);
    }

    final Stream<Arguments> digitCases() {
        return Stream.of(
                Arguments.of('0', true),
                Arguments.of('1', true),
                Arguments.of('2', true),
                Arguments.of('3', true),
                Arguments.of('4', true),
                Arguments.of('5', true),
                Arguments.of('6', true),
                Arguments.of('7', true),
                Arguments.of('8', true),
                Arguments.of('9', true),
                Arguments.of('a', false),
                Arguments.of('z', false),
                Arguments.of('#', false),
                Arguments.of('^', false),
                Arguments.of('Y', false)
        );
    }

    @ParameterizedTest
    @MethodSource("digitCases")
    final void digitPatternMatchesAllDigits(char c, boolean b) {
        assertRegex(ProtocolPatterns.DIGIT, String.format("%c", c), b);
    }

    final Stream<Arguments> alphanumericalCases() {
        return Stream.of(
                Arguments.of('0', true),
                Arguments.of('2', true),
                Arguments.of('5', true),
                Arguments.of('8', true),
                Arguments.of('9', true),
                Arguments.of('a', true),
                Arguments.of('z', true),
                Arguments.of('#', false),
                Arguments.of('^', false),
                Arguments.of('Y', true)
        );
    }

    @ParameterizedTest
    @MethodSource("alphanumericalCases")
    final void letterDigitMatchesAlphanumericals(char c, boolean b) {
        assertRegex(ProtocolPatterns.LETTER_DIGIT, String.format("%c", c), b);
    }

    final Stream<Arguments> crlfCases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("\\r", false),
                Arguments.of("\\n", false),
                Arguments.of("\\r\\n", true)
        );
    }

    @ParameterizedTest
    @MethodSource("crlfCases")
    final void stringMatchesCRLF(String s, boolean b) {
        assertRegex(ProtocolPatterns.CRLF, s, b);
    }

    final Stream<Arguments> portCases() {
        return Stream.of(
                Arguments.of("0", false),
                Arguments.of("1", true),
                Arguments.of("9", true),
                Arguments.of("10", true),
                Arguments.of("11", true),
                Arguments.of("100", true),
                Arguments.of("999", true),
                Arguments.of("1000", true),
                Arguments.of("1001", true),
                Arguments.of("9999", true),
                Arguments.of("10000", true),
                Arguments.of("10001", true),
                Arguments.of("11111", true),
                Arguments.of("22222", true),
                Arguments.of("33333", true),
                Arguments.of("44444", true),
                Arguments.of("55555", true),
                Arguments.of("65534", true),
                Arguments.of("65535", true),
                Arguments.of("65536", false),
                Arguments.of("99999", false),
                Arguments.of("111111", false)
        );
    }

    @ParameterizedTest
    @MethodSource("portCases")
    final void stringMatchesPortNumber(String s, boolean b) {
        assertRegex(ProtocolPatterns.PORT, s, b);
    }

    private void assertRegex(String regex, String sequence, boolean expected) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sequence);
        assertThat("Sequence " + sequence + " matches " + regex + ": " + expected, matcher.matches(), equalTo(expected));
    }
}
