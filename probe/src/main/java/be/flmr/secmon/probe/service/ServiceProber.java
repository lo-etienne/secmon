package be.flmr.secmon.probe.service;

import be.flmr.secmon.core.net.IService;

import java.io.IOException;

public interface ServiceProber {
    int get(IService service) throws IOException;
}
