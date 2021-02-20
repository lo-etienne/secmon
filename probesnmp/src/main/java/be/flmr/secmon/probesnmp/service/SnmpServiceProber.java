package be.flmr.secmon.probesnmp.service;

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

import be.flmr.secmon.probe.service.Service;
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

    private CommunityTarget createDefault(String ip, String community) {
        Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + ip + "/" + DEFAULT_PORT);
        CommunityTarget target = new CommunityTarget();

        target.setCommunity(new OctetString(community));
        target.setAddress(address);
        target.setVersion(DEFAULT_VERSION);
        target.setTimeout(DEFAULT_TIMEOUT);
        target.setRetries(DEFAULT_RETRY);

        return target;
    }

    @Override
    public String get(Service service) {
        CommunityTarget target = createDefault(service.getIp(), service.getUser());
        Snmp snmp = null;

        try {
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(service.getOid())));

            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();

            pdu.setType(PDU.GET);
            ResponseEvent responseEvent = snmp.send(pdu, target);

            PDU response = responseEvent.getResponse();

            if(response != null) {
                // if response == null, il y a eu un timeout
                if (response != null) {
                    System.out.println("response pdu size is " + response.size());
                    for (int i = 0; i < response.size(); i++) {
                        VariableBinding vb = response.get(i);
                        System.out.println(vb.getOid() + " = " + vb.getVariable());
                    }
                }
            }
        } catch(IOException e) {
            System.out.println("Erreur lors de l'écoute.");
            e.printStackTrace();
        } finally {
            if(snmp != null) {
                try {
                    snmp.close();
                } catch(IOException exc) {
                    snmp = null;
                }
            }
        }

        return null;
    }

    public static void main(String[] args) {
        Service service = new Service("192.168.128.38", "public", "1.3.6.1.4.1.2021.4.11.0");

        SnmpServiceProber prober = new SnmpServiceProber();
        prober.get(service);
    }


}
