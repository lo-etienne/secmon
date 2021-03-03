package be.flmr.secmon.probesnmp;

import be.flmr.secmon.core.multicast.ConnectionBroadcaster;
import be.flmr.secmon.core.net.IIntervalProtocolPacketSender;
import be.flmr.secmon.probe.config.ProbeJSONConfigurationReader;
import be.flmr.secmon.probe.net.server.ProbeServer;
import be.flmr.secmon.probesnmp.service.SnmpServiceProber;

import java.io.InputStreamReader;

public class Program {
    public static void main(String[] args) {
        var temp = Program.class.getClassLoader().getResourceAsStream("probe.conf.json");
        ProbeJSONConfigurationReader reader = new ProbeJSONConfigurationReader(new InputStreamReader(temp));
        IIntervalProtocolPacketSender multicastSender = new ConnectionBroadcaster(reader.getMulticastAddress(), Integer.parseInt(reader.getMulticastPort()));

        ProbeServer server = new ProbeServer(reader, multicastSender, new SnmpServiceProber());

        server.run();
    }
}
