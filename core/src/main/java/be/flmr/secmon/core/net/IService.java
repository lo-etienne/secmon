package be.flmr.secmon.core.net;

public interface IService {
    String getID();
    String getURL();
    int getMin();
    int getMax();
    int getFrequency();

    String getAugmentedURL();
}
