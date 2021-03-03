package be.flmr.secmon.probe.net.server;

import be.flmr.secmon.core.net.IService;
import be.flmr.secmon.probe.service.ServiceProber;
import be.flmr.secmon.probe.service.ServiceThread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ProbeServiceCommunicator {
    private ScheduledExecutorService executor;

    private Map<ServiceThread, ScheduledFuture<?>> services;

    private ServiceProber prober;

    public ProbeServiceCommunicator(ServiceProber prober) {
        services = new ConcurrentHashMap<>();
        this.prober = prober;
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public String getServiceState(String id) {
        return services.keySet().stream()
                .filter(scheduledFuture -> scheduledFuture.getService().getID().equals(id))
                .map(ServiceThread::getStatus)
                .findFirst().orElse("DOWN");
    }

    public void addService(IService service) {
        var serviceTh = new ServiceThread(service, prober);
        addServiceThread(serviceTh);
    }

    public void updateService(IService service) {
        var serviceTh = new ServiceThread(service, prober);
        this.services.get(serviceTh).cancel(true);
        this.services.remove(serviceTh);
        addServiceThread(serviceTh);
    }

    private void addServiceThread(ServiceThread serviceThread) {
        var future = executor.scheduleWithFixedDelay(serviceThread, 0, serviceThread.getService().getFrequency(), TimeUnit.SECONDS);
        services.put(serviceThread, future);
    }
}
