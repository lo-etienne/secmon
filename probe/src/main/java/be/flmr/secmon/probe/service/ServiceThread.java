package be.flmr.secmon.probe.service;

import be.flmr.secmon.core.net.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class ServiceThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ServiceThread.class);

    private Runnable onNewValue;
    private final IService service;
    private ServiceProber prober;

    private String status = "NOSTATUS";

    private int oldValue;

    public ServiceThread(IService service, ServiceProber prober) {
        this.service = service;
        this.prober = prober;
    }

    @Override
    public void run() {
        try {
            log.info("Getting status from {}", service.getURL());
            var newValue = prober.get(service);

            if ((newValue < service.getMin()) || (newValue > service.getMax())) {
                log.info("Status is outside of min/max bounds");
                status = "ALARM";
            } else {
                log.info("Status is within of min/max bounds");
                status = "OK";
            }

            if(oldValue != newValue) {
                onNewValue.run();
                oldValue = newValue;
            }
        } catch (IOException e) {
            log.warn("Status is down !", e);
            status = "DOWN";
        }
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

    public void setOnNewValue(Runnable runnable) {
        this.onNewValue = runnable;
    }
}
