package be.flmr.secmon.probe.net.server;

import be.flmr.secmon.probe.config.ProbeJSONConfigurationReader;
import be.flmr.secmon.probe.service.ServiceProber;

import javax.crypto.Cipher;

public class ProbeServerBuilder implements IProbeServerBuilder {

    @Override
    public ProbeServer build() {
        return null;
    }

    @Override
    public IProbeServerBuilder withThreads(int nbThread) {
        return null;
    }

    @Override
    public IProbeServerBuilder withEncryption(Cipher cipher) {
        return null;
    }

    @Override
    public IProbeServerBuilder withServiceProber(ServiceProber prober) {
        return null;
    }

    @Override
    public IProbeServerBuilder withJSONConfigReader(ProbeJSONConfigurationReader reader) {
        return null;
    }
}
