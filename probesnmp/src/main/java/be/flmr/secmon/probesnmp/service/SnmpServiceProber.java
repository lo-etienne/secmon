package be.flmr.secmon.probesnmp.service;

import be.flmr.secmon.core.net.IService;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import be.flmr.secmon.probe.service.ServiceProber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
    public String get(IService service) {
        CommunityTarget<Address> target = createDefault(service.getURL().getHost(), service.getURL().getUserInfo());

        try(Snmp snmp = new Snmp(new DefaultUdpTransportMapping())) {
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(service.getURL().getPath())));

            snmp.listen();

            pdu.setType(PDU.GET);
            ResponseEvent<Address> responseEvent = snmp.send(pdu, target);

            PDU response = responseEvent.getResponse();

            if(response != null) {
                // if response == null, il y a eu un timeout
                VariableBinding vb = response.get(0);
                return vb.getVariable().toString();
            }
        } catch(IOException e) {
            System.out.println("Erreur lors de l'écoute.");
            e.printStackTrace();
        }

        return null;
    }

    /*public static void main(String[] args) {
        Service service = new Service("192.168.128.38", "public", "1.3.6.1.4.1.2021.11.11.0");

        SnmpServiceProber prober = new SnmpServiceProber();
        prober.get(service);
    }*/


}
