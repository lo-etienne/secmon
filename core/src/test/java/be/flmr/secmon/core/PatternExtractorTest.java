package be.flmr.secmon.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatternExtractorTest {
    @Mock
    private PatternExtractor patternExtractor;

    @Test
    final void testExtraction() {
        when(patternExtractor.getPattern()).thenReturn("(?<this_is_a_group>\\w+)");
        assertThat(patternExtractor.extract("Hello", "this_is_a_group"), equalTo("Hello"));
    }
}
