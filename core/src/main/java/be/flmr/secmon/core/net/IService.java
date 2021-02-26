package be.flmr.secmon.core.net;

import java.net.URL;

public interface IService {
    String getID();
    URL getURL();
    int getMin();
    int getMax();
    int getFrequency();
}
