package be.flmr.secmon.probe.net.server;

import be.flmr.secmon.probe.config.ProbeJSONConfigurationReader;
import be.flmr.secmon.probe.service.ServiceProber;

import javax.crypto.Cipher;

public interface IProbeServerBuilder {
    ProbeServer build();
    IProbeServerBuilder withThreads(int nbThread);
    IProbeServerBuilder withEncryption(Cipher cipher);
    IProbeServerBuilder withServiceProber(ServiceProber prober);
    IProbeServerBuilder withJSONConfigReader(ProbeJSONConfigurationReader reader);
}
