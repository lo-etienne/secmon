package be.flmr.secmon.daemon.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DaemonJSONConfigurationReaderTest {
    private IDaemonConfigurationReader reader;

    @BeforeEach
    final void setup() {
        reader = new DaemonJSONConfigurationReader(new StringReader(new DaemonJSONTestConfiguration().config));
    }

    @Test
    final void readsConfiguration() {
        assertThat(reader.getName(), equalTo("monitor"));
    }
}
