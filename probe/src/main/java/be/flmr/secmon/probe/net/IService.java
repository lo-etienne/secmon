package be.flmr.secmon.probe.net;

import java.net.URL;

public interface IService {
    String getID();
    URL getURL();
    int getMin();
    int getMax();
    int getFrequency();
}
