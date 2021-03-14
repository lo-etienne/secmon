package be.flmr.secmon.core.router;

import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.PatternGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static be.flmr.secmon.core.pattern.ProtocolPattern.ANNOUNCE;
import static be.flmr.secmon.core.pattern.ProtocolPattern.NOTIFICATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RouterTest {
    @Mock
    public IProtocolPacket packet;

    private static boolean verify;
    private static String parameter;
    private AbstractRouter router;

    @BeforeEach
    final void setup() {
        verify = false;
        parameter = "";
        router = new RouterStub();
    }

    @Test
    final void executesMethodWhenRegexMatches() {
        when(packet.getType()).thenReturn(ANNOUNCE);
        when(packet.getValue(PatternGroup.PROTOCOL)).thenReturn("world");

        router.execute(this, packet);
        assertThat(verify, equalTo(true));
    }

    @Test
    final void passesGroupedParameterAsAnnotatedParameter() {
        when(packet.getType()).thenReturn(ANNOUNCE);
        when(packet.getValue(PatternGroup.PROTOCOL)).thenReturn("world");

        router.execute(this, packet);
        assertThat(parameter, equalTo("world"));
    }

    @Test
    final void doesNotExecuteMethodWhenNoValueIsSpecified() {
        router.execute(this, packet);
        assertThat(verify, equalTo(false));
    }

    @Test
    final void throwsExceptionWhenMethodDoesNotContainGroupAnnotation() {
        when(packet.getType()).thenReturn(NOTIFICATION);

        assertThrows(IllegalArgumentException.class, () -> router.execute(this, packet));
    }

    private static class RouterStub extends AbstractRouter {
        public RouterStub() {
            super();
        }

        @Protocol(pattern = ANNOUNCE)
        public void hello(Object sender, IProtocolPacket packet) {
            verify = true;
            parameter = packet.getValue(PatternGroup.PROTOCOL);
        }

        @Protocol(pattern = NOTIFICATION)
        public void noGroup(String test) {

        }
    }
}

