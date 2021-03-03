package be.flmr.secmon.probe.service;

import be.flmr.secmon.core.net.IService;

import java.io.IOException;
import java.util.Objects;

public class ServiceThread implements Runnable {
    private final IService service;
    private ServiceProber prober;

    private String status = "NOSTATUS";

    private int currentValue;

    public ServiceThread(IService service, ServiceProber prober) {
        this.service = service;
        this.prober = prober;
    }

    @Override
    public void run() {
        try {
            currentValue = prober.get(service);
            if ((currentValue < service.getMin()) || (currentValue > service.getMax()))
                status = "ALARM";
            else
                status = "OK";
        } catch (IOException e) {
            status = "DOWN";
        }
    }

    public int getCurrentValue() {
        return this.currentValue;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceThread serviceThread = (ServiceThread) o;
        return Objects.equals(this.service, serviceThread.service);
    }

    @Override
    public int hashCode() {
        return Objects.hash(service);
    }

    public IService getService() {
        return this.service;
    }
}
