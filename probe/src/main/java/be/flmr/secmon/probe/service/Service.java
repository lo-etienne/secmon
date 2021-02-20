package be.flmr.secmon.probe.service;

import be.flmr.secmon.core.PatternExtractor;
import be.flmr.secmon.core.ProtocolPatterns;

public class Service {
    private String id;
    private int min;
    private int max;
    private int frequency;

    private String user;
    private String ip;
    private String oid;

    /*public Service(String augmented_url) {
        id = PatternExtractor.extract(augmented_url, ProtocolPatterns.AUGMENTED_URL, "ID");
        min = PatternExtractor.extract(augmented_url, ProtocolPatterns.AUGMENTED_URL, "MIN");
        max = PatternExtractor.extract(augmented_url, ProtocolPatterns.AUGMENTED_URL, "MAX");
        frequency = PatternExtractor.extract(augmented_url, ProtocolPatterns.AUGMENTED_URL, "FREQUENCY");
        user = PatternExtractor.extract(augmented_url, ProtocolPatterns.AUGMENTED_URL, "USERNAME");
        ip = PatternExtractor.extract(augmented_url, ProtocolPatterns.AUGMENTED_URL, "HOST");
        oid = PatternExtractor.extract(augmented_url, ProtocolPatterns.AUGMENTED_URL, "PATH");
    }*/

    public Service(String ip, String community, String oid) {
        this.ip = ip;
        this.user = community;
        this.oid = oid;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getIp() {
        return ip;
    }

    public String getOid() {
        return oid;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getFrequency() {
        return frequency;
    }
}
