package be.flmr.secmon.daemon.config;

import be.flmr.secmon.core.net.IProtocolPacketReceiver;
import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.PatternGroup;
import be.flmr.secmon.core.pattern.ProtocolPattern;
import be.flmr.secmon.daemon.net.NorthPole;
import be.flmr.secmon.daemon.net.ServiceStateStack;

import java.io.StringReader;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DaemonJSONTestConfiguration {
    String config =
            "{\n" +
            "    \"name\": \"monitor\",\n" +
            "    \"version\": \"1.0.0\",\n" +
            "    \"multicast_address\" : \"224.50.50.50\",\n" +
            "    \"multicast_port\" : \"60150\",\n" +
            "    \"client_port\" : \"42069\",\n" +
            "    \"tls\": \"false\",\n" +
            "    \"aes_key\": \"aPdSgVkYp3s6v9y$B&E(H+MbQeThWmZq\",\n" +
            "    \"probes\": [\n" +
            "        \"snmp1!snmp://public@192.168.128.38:161/1.3.6.1.4.1.2021.4.11.0!10000!99999999!120\",\n" +
            "        \"snmp2!snmp://public@192.168.128.38:161/1.3.6.1.4.1.2021.11.11.0!10!99999999!120\",\n" +
            "        \"http1!https://sensor.cg.helmo.be/api/get-temp/!5!35!60\",\n" +
            "        \"http2!https://sensor.cg.helmo.be/api/get-humidity/!0!80!60\"\n" +
            "    ]\n" +
            "}\n";

    public static void main(String[] args) throws Exception {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        IProtocolPacket packet = mock(IProtocolPacket.class);
        when(packet.getValue(PatternGroup.HOST)).thenReturn("localhost");
        when(packet.getValue(PatternGroup.PROTOCOL)).thenReturn("https");
        when(packet.getValue(PatternGroup.PORT)).thenReturn("60150");
        when(packet.getType()).thenReturn(ProtocolPattern.NOTIFICATION);
        when(packet.getSourceAddress()).thenReturn(InetAddress.getLocalHost());

        IProtocolPacketReceiver receiver = mock(IProtocolPacketReceiver.class);
        when(receiver.receive()).thenReturn(packet);

        var stack = new ServiceStateStack();

        NorthPole northPole = new NorthPole(receiver, new DaemonJSONConfigurationReader(new StringReader(new DaemonJSONTestConfiguration().config)), stack);

        executor.execute(northPole);
    }
}
