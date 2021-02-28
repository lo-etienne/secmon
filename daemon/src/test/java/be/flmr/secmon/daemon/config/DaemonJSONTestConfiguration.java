package be.flmr.secmon.daemon.config;

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
}
