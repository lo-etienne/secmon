package be.flmr.secmon.core.router;

import be.flmr.secmon.core.pattern.PatternGroup;
import be.flmr.secmon.core.pattern.ProtocolPattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RouterTest {
    private static boolean verify;
    private static String parameter;
    private AbstractRouter router;

    @BeforeEach
    final void setup() {
        verify = false;
        parameter = "";
        router = RouterLoader.loadRouter(RouterStub.class);
    }

    @Test
    final void executesMethodWhenRegexMatches() {
        router.execute("IAMHERE world 142\r\n");
        assertThat(verify, equalTo(true));
    }

    @Test
    final void passesGroupedParameterAsAnnotatedParameter() {
        router.execute("IAMHERE world 123\r\n");
        assertThat(parameter, equalTo("world"));
    }

    @Test
    final void doesNotExecuteMethodWhenRegexDoesntMatch() {
        router.execute("this does not match");
        assertThat(verify, equalTo(false));
    }

    private static class RouterStub extends AbstractRouter {
        @Protocol(pattern = ProtocolPattern.ANNOUNCE)
        public void hello(@Group(group = PatternGroup.PROTOCOL) String world) {
            verify = true;
            parameter = world;
        }
    }
}

