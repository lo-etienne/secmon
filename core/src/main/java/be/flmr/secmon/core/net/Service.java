package be.flmr.secmon.core.net;

import be.flmr.secmon.core.pattern.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static be.flmr.secmon.core.pattern.PatternGroup.*;

public class Service implements IService {
    private String id;
    private String url;
    private String min;
    private String max;
    private String frequency;

    public static List<IService> from(IProtocolPacket packet) {
        List<IService> services = new ArrayList<>();

        List<String> augmentedURLS = PatternUtils.findGroups(packet.getMessage(), AUGMENTEDURL);

        for (var augmentedURL : augmentedURLS) {
            Service service = new Service(augmentedURL);
            services.add(service);
        }
        return services;
    }

    private Service(String augmentedURL) {
        if (augmentedURL != null) {
            id = PatternUtils.extractGroup(augmentedURL, AUGMENTEDURL, ID.name());
            url = PatternUtils.extractGroup(augmentedURL, AUGMENTEDURL, URL.name());
            min = PatternUtils.extractGroup(augmentedURL, AUGMENTEDURL, MIN.name());
            max = PatternUtils.extractGroup(augmentedURL, AUGMENTEDURL, MAX.name());
            frequency = PatternUtils.extractGroup(augmentedURL, AUGMENTEDURL, FREQUENCY.name());
        }
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public int getMin() {
        return Integer.parseInt(min);
    }

    @Override
    public int getMax() {
        return Integer.parseInt(max);
    }

    @Override
    public int getFrequency() {
        return Integer.parseInt(frequency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(getID(), service.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }
}
