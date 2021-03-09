package be.flmr.secmon.probe.net.server;

import be.flmr.secmon.core.net.IService;
import be.flmr.secmon.probe.service.ServiceProber;
import be.flmr.secmon.probe.service.ServiceThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.*;

public class ProbeServiceCommunicator implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(ProbeServiceCommunicator.class);
    private ScheduledExecutorService executor;

    private Map<ServiceThread, ScheduledFuture<?>> services;

    private ServiceProber prober;

    private Runnable onNewValue;

    public ProbeServiceCommunicator(ServiceProber prober) {
        services = new ConcurrentHashMap<>();
        this.prober = prober;
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public String getServiceState(String id) {
        log.info("Réception de l'état du service {}", id);
        var matches = services.keySet().stream()
                .filter(servThread -> servThread.getService().getID().equals(id))
                .map(ServiceThread::getStatus).findFirst();

        if(matches.isPresent()) {
            return matches.get();
        } else {
            throw new NoSuchElementException("Mauvaise valeur d'identifiant spécifiée");
        }
    }

    public void addService(IService service) {
        for (ServiceThread entry : services.keySet()) {
            if(entry.getService().equals(service)) {
                updateService(service);
                return;
            }
        }

        log.info("Ajout d'un service ({})", service);
        var serviceTh = new ServiceThread(service, prober);
        addServiceThread(serviceTh);
    }

    public void updateService(IService service) {
        log.info("Mise à jour des valeurs du service {}", service);
        var serviceTh = new ServiceThread(service, prober);
        this.services.get(serviceTh).cancel(true);
        this.services.remove(serviceTh);
        addServiceThread(serviceTh);
    }

    private void addServiceThread(ServiceThread serviceThread) {
        log.info("Commencement du thread qui probe le service");
        serviceThread.setOnNewValue(onNewValue);
        var future = executor.scheduleWithFixedDelay(serviceThread, 0, serviceThread.getService().getFrequency(), TimeUnit.SECONDS);
        services.put(serviceThread, future);
    }

    public void setOnNewValue(Runnable runnable) {
        this.onNewValue = runnable;
    }

    @Override
    public void close() {
        log.info("Fermeture de la communication des services...");
        executor.shutdown();
    }
}
