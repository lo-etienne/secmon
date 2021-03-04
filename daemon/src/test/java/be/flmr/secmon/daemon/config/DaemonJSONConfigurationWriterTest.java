package be.flmr.secmon.daemon.config;

import be.flmr.secmon.core.net.IService;
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
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    final void writesNewService() {
        String url = "new!new://service.test/path!42!42!42";

        IService service = mock(IService.class);
        when(service.getAugmentedURL()).thenReturn(url);
        writer.addService(service);

        assertThat(stringWriter.toString(), containsString(url));
    }

    @Test
    final void writesNewServices() {
        IService service = mock(IService.class);
        when(service.getAugmentedURL()).thenReturn("new1!new://service.test/path!42!42!42");

        IService service2 = mock(IService.class);
        when(service2.getAugmentedURL()).thenReturn("new2!new://service.test/path!42!42!42");

        writer.addServices(service, service2);

        assertThat(stringWriter.toString(), allOf(
                containsString("new1"),
                containsString("new2")
        ));
    }
}
