package be.flmr.secmon.probe.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProbeJSONConfigurationReaderTest {
    private ProbeConfigurationReader parser;

    @BeforeEach
    final void setup() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("good.conf.json");
        parser = new ProbeJSONConfigurationReader(new InputStreamReader(is));
    }

    @Test
    final void parserReturnsTheRightName() {
        assertThat(parser.getName(), equalTo("Hello world !"));
    }

    @Test
    final void parserReturnsTheRightVersion() {
        assertThat(parser.getVersion(), equalTo("6.9.0"));
    }

    @Test
    final void parserReturnsTheRightMulticastAddress() {
        assertThat(parser.getMulticastAddress(), equalTo("127.0.0.1"));
    }

    @Test
    final void parserReturnsTheRightMulticastPort() {
        assertThat(parser.getMulticastPort(), equalTo("42069"));
    }

    @Test
    final void parserReturnsTheRightAESKey() {
        assertThat(parser.getAesKey(), equalTo("aeskey"));
    }
}
