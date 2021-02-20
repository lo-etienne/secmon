package be.flmr.secmon.core;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static be.flmr.secmon.core.ProtocolPatternsTests.assertRegex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MulticastProtocolPatternsTest {

    final Stream<Arguments> ANNOUCECases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("IAMHERE Pomme5 666\\r\\n", true),
                Arguments.of("IAMHERE 12345p 1\\r\\n", true),
                Arguments.of("IAMHERE pomme15 65535\\r\\n", true),
                Arguments.of("IAMHERE pomme15 65536\\r\\n", false),
                Arguments.of("IAMHERE pomme15 0\\r\\n", false),
                Arguments.of("IAMHERE 0000000000000000000 145\\r\\n", false),
                Arguments.of("IAMHERE pomme15\\r\\n", false),
                Arguments.of("IAMHERE jnkadgfnjgfadjnadfgnjjnadgfbnj", false),
                Arguments.of("POMME", false)
        );
    }

    @ParameterizedTest
    @MethodSource("ANNOUCECases")
    final void stringMatchesANNOUCE(String s, boolean b) {
        assertRegex(MulticastProtocolPatterns.ANNOUCE, s, b);
    }

    final Stream<Arguments> NOTIFICATIONCases() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("NOTIFY Pomme5 666\\r\\n", true),
                Arguments.of("NOTIFY 12345p 1\\r\\n", true),
                Arguments.of("NOTIFY pomme15 65535\\r\\n", true),
                Arguments.of("NOTIFY pomme15 65536\\r\\n", false),
                Arguments.of("NOTIFY pomme15 0\\r\\n", false),
                Arguments.of("NOTIFY 0000000000000000000 145\\r\\n", false),
                Arguments.of("NOTIFY pomme15\\r\\n", false),
                Arguments.of("NOTIFY jnkadgfnjgfadjnadfgnjjnadgfbnj", false),
                Arguments.of("POMME", false)
        );
    }

    @ParameterizedTest
    @MethodSource("NOTIFICATIONCases")
    final void stringMatchesNOTIFICATION(String s, boolean b) {
        assertRegex(MulticastProtocolPatterns.NOTIFICATION, s, b);
    }
}
