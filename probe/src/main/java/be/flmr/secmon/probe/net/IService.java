package be.flmr.secmon.probe.net;

public interface IService {
    String getID();
    String getURL();
    int getMin();
    int getMax();
    int getFrequency();
}
