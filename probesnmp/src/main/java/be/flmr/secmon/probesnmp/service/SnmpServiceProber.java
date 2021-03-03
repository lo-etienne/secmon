package be.flmr.secmon.probesnmp.service;

import be.flmr.secmon.core.net.IService;
import be.flmr.secmon.core.pattern.PatternUtils;
import be.flmr.secmon.probe.service.ServiceProber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

import static be.flmr.secmon.core.pattern.PatternGroup.*;

public class SnmpServiceProber implements ServiceProber {
    public static final int DEFAULT_VERSION = SnmpConstants.version2c;
    public static final String DEFAULT_PROTOCOL = "udp";
    public static final int DEFAULT_PORT = 161;
    public static final long DEFAULT_TIMEOUT = 3 * 1000L;
    public static final int DEFAULT_RETRY = 3;

    private static final Logger log = LoggerFactory.getLogger(SnmpServiceProber.class);     

    private CommunityTarget<Address> createDefault(String ip, String community) {
        Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + ip + "/" + DEFAULT_PORT);
        CommunityTarget<Address> target = new CommunityTarget<>();

        target.setCommunity(new OctetString(community));
        target.setAddress(address);
        target.setVersion(DEFAULT_VERSION);
        target.setTimeout(DEFAULT_TIMEOUT);
        target.setRetries(DEFAULT_RETRY);

        return target;
    }

    @Override
    public int get(IService service) throws IOException {
        CommunityTarget<Address> target = createDefault(PatternUtils.extractGroup(service.getURL(), URL, HOST.name()), PatternUtils.extractGroup(service.getURL(), URL, USERNAME.name()));

        try(Snmp snmp = new Snmp(new DefaultUdpTransportMapping())) {
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(PatternUtils.extractGroup(service.getURL(), URL, PATH.name()).split("/")[1])));

            snmp.listen();

            pdu.setType(PDU.GET);
            ResponseEvent<Address> responseEvent = snmp.send(pdu, target);

            PDU response = responseEvent.getResponse();

            if(response != null) {
                // if response == null, il y a eu un timeout
                VariableBinding vb = response.get(0);
                var temp = vb.getVariable().toString();
                log.debug("Current value : {}", temp);
                return Integer.parseInt(temp);
            }
        }

        throw new IOException("Erreur lors de la lecture");
    }
}
