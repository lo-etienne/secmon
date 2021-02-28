package be.flmr.secmon.daemon.config;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DaemonJSONConfigurationWriterTest {
    private IDaemonConfigurationWriter writer;
    private StringWriter stringWriter;

    @BeforeEach
    final void setup() {
        stringWriter = new StringWriter();

        String str = new DaemonJSONTestConfiguration().config;
        writer = new DaemonJSONConfigurationWriter(new StringReader(str), stringWriter);
    }

    @Test
    final void writesNewProbe() {
        writer.beginTransaction()
                .addService("new service")
                .endTransaction();

        assertThat(stringWriter.toString(), containsString("new service"));
    }

    @Test
    final void throwsExceptionOnAddingServiceWithoutTransaction() {
        assertThrows(IllegalStateException.class, () -> writer.addServices(""));
    }
}
