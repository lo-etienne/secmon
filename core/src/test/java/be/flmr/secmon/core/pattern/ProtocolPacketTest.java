package be.flmr.secmon.core.pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class ProtocolPacketTest {
    private ProtocolPacket packet;

    @Test
    final void createsAMessageFromValues() {
        packet = new ProtocolPacketBuilder()
                .withType(ProtocolPattern.ANNOUNCE)
                .with(PatternGroup.PROTOCOL, "world")
                .with(PatternGroup.PORT, "42")
                .build();

        assertThat(packet.buildMessage(), equalTo("IAMHERE world 42\r\n"));
    }

    @Test
    final void createsValuesFromMessage() {
        packet = ProtocolPacket.from("IAMHERE snmp 161\r\n");

        assertThat(packet.getValue(PatternGroup.PROTOCOL), equalTo("snmp"));
        assertThat(packet.getValue(PatternGroup.PORT), equalTo("161"));
    }
}
