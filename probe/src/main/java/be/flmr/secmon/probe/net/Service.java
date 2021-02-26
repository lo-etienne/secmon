package be.flmr.secmon.probe.net;

import be.flmr.secmon.core.pattern.*;

import java.util.ArrayList;
import java.util.List;

import static be.flmr.secmon.core.pattern.PatternGroup.*;

public class Service implements IProtocolPacket, IService {
    private IProtocolPacket packet;

    public static List<Service> from(IProtocolPacket packet) {
        List<Service> services = new ArrayList<>();

        List<String> augmentedURLS = PatternUtils.findGroups(packet.getMessage(), AUGMENTEDURL);

        for (var augmentedURL : augmentedURLS) {
            Service service = new Service(packet, augmentedURL);
            services.add(service);
        }
        return services; // TODO: remplacer par ImmutableList
    }

    private Service(IProtocolPacket packet, String augmentedURL) {
        this.packet = packet;

        if (augmentedURL != null) {
            if (getValue(ID) == null) setValue(ID, PatternUtils.extractGroup(augmentedURL, AUGMENTEDURL, ID.name()));
            if (getValue(URL) == null) setValue(URL, PatternUtils.extractGroup(augmentedURL, AUGMENTEDURL, URL.name()));
            if (getValue(MIN) == null) setValue(MIN, PatternUtils.extractGroup(augmentedURL, AUGMENTEDURL, MIN.name()));
            if (getValue(MAX) == null) setValue(MAX, PatternUtils.extractGroup(augmentedURL, AUGMENTEDURL, MAX.name()));
            if (getValue(FREQUENCY) == null) setValue(FREQUENCY, PatternUtils.extractGroup(augmentedURL, AUGMENTEDURL, FREQUENCY.name()));
        }
    }

    @Override
    public String getID() {
        return getValue(ID);
    }

    @Override
    public String getURL() {
        return getValue(URL);
    }

    @Override
    public int getMin() {
        return Integer.parseInt(getValue(MIN));
    }

    @Override
    public int getMax() {
        return Integer.parseInt(getValue(MAX));
    }

    @Override
    public int getFrequency() {
        return Integer.parseInt(getValue(FREQUENCY));
    }

    @Override
    public String getValue(IEnumPattern pattern) {
        return packet.getValue(pattern);
    }

    @Override
    public void setValue(IEnumPattern pattern, String value) {
        packet.setValue(pattern, value);
    }

    @Override
    public String buildMessage() {
        return packet.buildMessage();
    }

    @Override
    public IEnumPattern getType() {
        return packet.getType();
    }

    @Override
    public String getMessage() {
        return packet.getMessage();
    }
}
