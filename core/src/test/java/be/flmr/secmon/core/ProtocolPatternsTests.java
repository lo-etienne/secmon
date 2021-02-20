package be.flmr.secmon.core;

import be.flmr.secmon.core.patterns.ProtocolPatterns;
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

    final Stream<Arguments> characterCases() {
        return Stream.of(
                Arguments.of("\\n", false),
                Arguments.of("a", true),
                Arguments.of("b", true),
                Arguments.of("C", true),
                Arguments.of("D", true),
                Arguments.of("0", true),
                Arguments.of("5", true),
                Arguments.of("9", true),
                Arguments.of("+", true),
                Arguments.of("-", true),
                Arguments.of("*", true),
                Arguments.of("/", true),
                Arguments.of("[", true),
                Arguments.of("]", true),
                Arguments.of("{", true),
                Arguments.of("}", true),
                Arguments.of("(", true),
                Arguments.of(")", true),
                Arguments.of(",", true),
                Arguments.of("<", true),
                Arguments.of(">", true),
                Arguments.of(".", true),
                Arguments.of("`", true),
                Arguments.of("@", true),
                Arguments.of("%", true),
                Arguments.of("!", true),
                Arguments.of("#", true),
                Arguments.of("$", true),
                Arguments.of(";", true),
                Arguments.of(":", true),
                Arguments.of("^", true),
                Arguments.of("_", true),
                Arguments.of("|", true),
                Arguments.of("~", true),
                Arguments.of("&", true),
                Arguments.of("\"", true),
                Arguments.of(" ", true),
                Arguments.of("\\r", false)
        );
    }

    @ParameterizedTest
    @MethodSource("characterCases")
    final void stringMatchesCharacter(String s, boolean b) {
        assertRegex(ProtocolPatterns.CHARACTER, s, b);
    }

    final Stream<Arguments> character_passCases() {
        return Stream.of(
                Arguments.of("\\n", false),
                Arguments.of("a", true),
                Arguments.of("b", true),
                Arguments.of("C", true),
                Arguments.of("D", true),
                Arguments.of("0", true),
                Arguments.of("5", true),
                Arguments.of("9", true),
                Arguments.of("+", true),
                Arguments.of("-", true),
                Arguments.of("*", true),
                Arguments.of("/", true),
                Arguments.of("[", true),
                Arguments.of("]", true),
                Arguments.of("{", true),
                Arguments.of("}", true),
                Arguments.of("(", true),
                Arguments.of(")", true),
                Arguments.of(",", true),
                Arguments.of("<", true),
                Arguments.of(">", true),
                Arguments.of(".", true),
                Arguments.of("`", true),
                Arguments.of("@", true),
                Arguments.of("%", true),
                Arguments.of("!", true),
                Arguments.of("#", true),
                Arguments.of("$", true),
                Arguments.of(";", true),
                Arguments.of(":", true),
                Arguments.of("^", true),
                Arguments.of("_", true),
                Arguments.of("|", true),
                Arguments.of("~", true),
                Arguments.of("&", true),
                Arguments.of("\"", true),
                Arguments.of(" ", false),
                Arguments.of("\\r", false)
        );
    }

    @ParameterizedTest
    @MethodSource("character_passCases")
    final void stringMatchesCharacter_pass(String s, boolean b) {
        assertRegex(ProtocolPatterns.CHARACTER_PASS, s, b);
    }

    final Stream<Arguments> SPCases() {
        return Stream.of(
                Arguments.of("\\n", false),
                Arguments.of("a", false),
                Arguments.of("C", false),
                Arguments.of("0", false),
                Arguments.of("9", false),
                Arguments.of("+", false),
                Arguments.of("-", false),
                Arguments.of("*", false),
                Arguments.of("&", false),
                Arguments.of(" ", true),
                Arguments.of("\\r", false)
        );
    }

    @ParameterizedTest
    @MethodSource("SPCases")
    final void stringMatchesSP(String s, boolean b) {
        assertRegex(ProtocolPatterns.SP, s, b);
    }

    final Stream<Arguments> IDCases() {
        return Stream.of(
                Arguments.of("0", false),
                Arguments.of("1", false),
                Arguments.of("1000", false),
                Arguments.of("10000", true),
                Arguments.of("44444", true),
                Arguments.of("111155555", true),
                Arguments.of("1111155555", true),
                Arguments.of("9999999999", true),
                Arguments.of("10000000000", false),
                Arguments.of("0", false),
                Arguments.of("a", false),
                Arguments.of("1b", false),
                Arguments.of("1gn", false),
                Arguments.of("10gr", false),
                Arguments.of("1bjd1", true),
                Arguments.of("4nfh44", true),
                Arguments.of("Pomme", true),
                Arguments.of("Pomme5", true),
                Arguments.of("1155555", true),
                Arguments.of("11155555", true),
                Arguments.of("11Pomme55", true),
                Arguments.of("1111155555", true),
                Arguments.of("Pomme99999", true),
                Arguments.of("Pomme000007", false)
        );
    }

    @ParameterizedTest
    @MethodSource("IDCases")
    final void stringMatchesID(String s, boolean b) {
        assertRegex(ProtocolPatterns.ID, s, b);
    }

    final Stream<Arguments> PROTOCOLCases() {
        return Stream.of(
                Arguments.of("0", false),
                Arguments.of("1", false),
                Arguments.of("1000", true),
                Arguments.of("10000", true),
                Arguments.of("44444", true),
                Arguments.of("111155555", true),
                Arguments.of("1111155555", true),
                Arguments.of("9999999999", true),
                Arguments.of("10000000000", true),
                Arguments.of("0", false),
                Arguments.of("a", false),
                Arguments.of("1b", false),
                Arguments.of("1gn", true),
                Arguments.of("10gr", true),
                Arguments.of("1bjd1", true),
                Arguments.of("4nfh44", true),
                Arguments.of("Pomme", true),
                Arguments.of("Pomme5", true),
                Arguments.of("1155555", true),
                Arguments.of("11155555", true),
                Arguments.of("11Pomme55", true),
                Arguments.of("1111155555", true),
                Arguments.of("Pomme99999", true),
                Arguments.of("Pomme000007", true),
                Arguments.of("PommePomme", true),
                Arguments.of("Pomme0Pomme", true),
                Arguments.of("Pomme00Pomme", true),
                Arguments.of("Pomme000Pomme", true),
                Arguments.of("Pomme0000Pomme", true),
                Arguments.of("Pomme00007Pomme", true)
        );
    }

    @ParameterizedTest
    @MethodSource("PROTOCOLCases")
    final void stringMatchesPROTOCOL(String s, boolean b) {
        assertRegex(ProtocolPatterns.PROTOCOL, s, b);
    }

    final Stream<Arguments> USERNAMECases() {
        return Stream.of(
                Arguments.of("0", false),
                Arguments.of("a", false),
                Arguments.of("1b", false),
                Arguments.of("1gn", true),
                Arguments.of("10gr", true),
                Arguments.of("1bjd1", true),
                Arguments.of("4nfh44", true),
                Arguments.of("Pomme", true),
                Arguments.of("Pomme5", true),
                Arguments.of("1155555", true),
                Arguments.of("11155555", true),
                Arguments.of("Pomme00007PommePomme", true),
                Arguments.of("Pomme00007PommePommePomme", true),
                Arguments.of("Pomme00007PommePommePomme000", true),
                Arguments.of("Pomme00007PommePomme00007Pomme", true),
                Arguments.of("Pomme00007PommePomme00007PommePomme00007Pomme", true),
                Arguments.of("Pomme00007PommePomme00007PommePomme00007PommePomme", true),
                Arguments.of("Pomme00007PommePomme00007PommePomme00007PommePomme0", false)
        );
    }

    @ParameterizedTest
    @MethodSource("USERNAMECases")
    final void stringMatchesUSERNAME(String s, boolean b) {
        assertRegex(ProtocolPatterns.USERNAME, s, b);
    }

    final Stream<Arguments> PASSWORDCases() {
        return Stream.of(
                Arguments.of("0", false),
                Arguments.of("a", false),
                Arguments.of("1b", false),
                Arguments.of("1gn", true),
                Arguments.of("10gr", true),
                Arguments.of("1bjd1", true),
                Arguments.of("4n:;fh44", true),
                Arguments.of("Pomme", true),
                Arguments.of("Pomme5", true),
                Arguments.of("115\"5\"555", true),
                Arguments.of("11155555", true),
                Arguments.of("111 55555", false),
                Arguments.of("Pomme00007PommePomme", true),
                Arguments.of("Pomme00007Pomme(*)_+PommePomme", true),
                Arguments.of("Pomme000{}[]07PommePo@mmePomme000", true),
                Arguments.of("Pomme00007PommePomm*e00007Pomme", true),
                Arguments.of("Pomme00007PommePomme000+07PommePomme00007Pomme", true),
                Arguments.of("Pomme00007PommePomme00007PommePomme00007PommePomme", true),
                Arguments.of("Pomme00007PommePomme00007PommePomme00007PommePomme0", false)
        );
    }

    @ParameterizedTest
    @MethodSource("PASSWORDCases")
    final void stringMatchesPASSWORD(String s, boolean b) {
        assertRegex(ProtocolPatterns.PASSWORD, s, b);
    }

    final Stream<Arguments> HOSTCases() {
        return Stream.of(
                Arguments.of("0", false),
                Arguments.of("a", false),
                Arguments.of("1b", false),
                Arguments.of("1gn", true),
                Arguments.of("10gr", true),
                Arguments.of("1bjd1", true),
                Arguments.of("4nfh44", true),
                Arguments.of("Pomme", true),
                Arguments.of("Pomme5", true),
                Arguments.of("1155555", true),
                Arguments.of("11155555", true),
                Arguments.of("111 55555", false),
                Arguments.of("Pomme00007PommePomme", true),
                Arguments.of("Pomme00007Pomm_ePommePomme", true),
                Arguments.of("Pomme00007P-ommePommePomme000", true),
                Arguments.of("Pomme00007PommePomme.00007Pomme", true),
                Arguments.of("Pomme00007Po.mmePomme00007PommePomme00007Pomme", true),
                Arguments.of("Pomme00007PommePomme00_07PommePomme00007PommePomme", true),
                Arguments.of("Pomme00007PommePomme00007PommePomme00007PommePomme0", false)
        );
    }

    @ParameterizedTest
    @MethodSource("HOSTCases")
    final void stringMatchesHOST(String s, boolean b) {
        assertRegex(ProtocolPatterns.HOST, s, b);
    }

    final Stream<Arguments> PATHCases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("/", true),
                Arguments.of("/a", true),
                Arguments.of("/1b", true),
                Arguments.of("/1gn", true),
                Arguments.of("/10gr", true),
                Arguments.of("/1bjd1", true),
                Arguments.of("/4nfh44", true),
                Arguments.of("/Pomme", true),
                Arguments.of("/Pomme5", true),
                Arguments.of("/1155555", true),
                Arguments.of("/11155555", true),
                Arguments.of("/111 55555", false),
                Arguments.of("/Pomme00007PommePomme", true),
                Arguments.of("/Pomm/e00007PommePommePomme", true),
                Arguments.of("/Pomme0_0007PommePommePomme000", true),
                Arguments.of("/Pomme00007PommePomme00007Pomme", true),
                Arguments.of("/Pomme00007Pomm_Pomme0-00\\PommePomme00007Pomme", true),
                Arguments.of("/Pomme00007PommePomme0.007PommePomme00007PommePomme.07PommePomme_07PommePomme", true),
                Arguments.of("/Pomme00007PommePomme00007PommePomme00007PommePommePomme00007PommePomme00007PommePomme00007PommePomme0", false)
        );
    }

    @ParameterizedTest
    @MethodSource("PATHCases")
    final void stringMatchesPATH(String s, boolean b) {
        assertRegex(ProtocolPatterns.PATH, s, b);
    }

    final Stream<Arguments> URLCases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("P0mm3://abc.def.123/LesP0mm3s.com", true),
                Arguments.of("P0mm3://Sart0@abc.def.123/LesP0mm3s.com", true),
                Arguments.of("P0mm3://Sart0:mdp1234@abc.def.123/LesP0mm3s.com", true),
                Arguments.of("P0mm3://Sart0:mdp1234@abc.def.123:55555/LesP0mm3s.com", true),
                Arguments.of("P0mm3://Sart0:mdp1234abc.def.123:55555/LesP0mm3s.com", false),
                Arguments.of("P0mm3://Sart0:mdp1234@abc.def.12355555/LesP0mm3s.com", true)
        );
    }

    @ParameterizedTest
    @MethodSource("URLCases")
    final void stringMatchesURL(String s, boolean b) {
        assertRegex(ProtocolPatterns.URL, s, b);
    }

    final Stream<Arguments> MINCases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("0", true),
                Arguments.of("80000000", true),
                Arguments.of("10", true),
                Arguments.of("2568", true),
                Arguments.of("999999999", false)
        );
    }

    @ParameterizedTest
    @MethodSource("MINCases")
    final void stringMatchesMIN(String s, boolean b) {
        assertRegex(ProtocolPatterns.MIN, s, b);
    }

    final Stream<Arguments> MAXCases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("0", true),
                Arguments.of("80000000", true),
                Arguments.of("10", true),
                Arguments.of("2568", true),
                Arguments.of("999999999", false)
        );
    }

    @ParameterizedTest
    @MethodSource("MAXCases")
    final void stringMatchesMAX(String s, boolean b) {
        assertRegex(ProtocolPatterns.MAX, s, b);
    }

    final Stream<Arguments> FREQUENCYCases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("0", true),
                Arguments.of("80000000", true),
                Arguments.of("10", true),
                Arguments.of("2568", true),
                Arguments.of("999999999", false)
        );
    }

    @ParameterizedTest
    @MethodSource("FREQUENCYCases")
    final void stringMatchesFREQUENCY(String s, boolean b) {
        assertRegex(ProtocolPatterns.FREQUENCY, s, b);
    }

    final Stream<Arguments> AUGMENTED_URLCases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("LaP0mm3!P0mm3://abc.def.123/LesP0mm3s.com!30!250!600", true),
                Arguments.of("LaP0mm3!P0mm3://Sart0@abc.def.123/LesP0mm3s.com!30!250!600", true),
                Arguments.of("LaP0mm3!P0mm3://Sart0:mdp1234@abc.def.123/LesP0mm3s.com!30!250!600", true),
                Arguments.of("LaP0mm3!P0mm3://Sart0:mdp1234@abc.def.123:55555/LesP0mm3s.com!30!250!600", true),
                Arguments.of("LaP0mm3!P0mm3://Sart0:mdp1234abc.def.123:55555/LesP0mm3s.com!30!250!600", false),
                Arguments.of("LaP0mm3!P0mm3://Sart0:mdp1234@abc.def.12355555/LesP0mm3s.com!30!250!600", true)
        );
    }

    @ParameterizedTest
    @MethodSource("AUGMENTED_URLCases")
    final void stringMatchesAUGMENTED_URL(String s, boolean b) {
        assertRegex(ProtocolPatterns.AUGMENTED_URL, s, b);
    }

    final Stream<Arguments> STATECases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("ALARM", true),
                Arguments.of("OK", true),
                Arguments.of("DOWN", true),
                Arguments.of("POMME", false),
                Arguments.of("OK ARLARM", false)
        );
    }

    @ParameterizedTest
    @MethodSource("STATECases")
    final void stringMatchesSTATE(String s, boolean b) {
        assertRegex(ProtocolPatterns.STATE, s, b);
    }

    final Stream<Arguments> MESSAGECases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("Les pommes verte et les jolies pommes rouge sont joyeuses dans leur vergers", true),
                Arguments.of("OK", true),
                Arguments.of("DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_" +
                        "DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_" +
                        "DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN_DOWN", false),
                Arguments.of("POMME", true)
        );
    }

    @ParameterizedTest
    @MethodSource("MESSAGECases")
    final void stringMatchesMESSAGE(String s, boolean b) {
        assertRegex(ProtocolPatterns.MESSAGE, s, b);
    }

    private void assertRegex(String regex, String sequence, boolean expected) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sequence);
        assertThat("Sequence " + sequence + " matches " + regex + ": " + expected, matcher.matches(), equalTo(expected));
    }
}
