package be.flmr.secmon.core.net;

import java.net.URL;

public interface IService {
    String getID();
    String getURL();
    int getMin();
    int getMax();
    int getFrequency();
}
