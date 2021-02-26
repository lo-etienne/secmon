package be.flmr.secmon.probe.service;

import be.flmr.secmon.core.net.IService;

public interface ServiceProber {
    String get(IService service);
}
