package be.flmr.secmon.probe.service;

import be.flmr.secmon.probe.net.IService;

public interface ServiceProber {
    String get(IService service);
}
