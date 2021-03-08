package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.IService;
import be.flmr.secmon.core.net.ServiceState;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe contenant différents stacks associés à un service. Permets de garder une trace de leur états.
 */
public class ServiceStateStack {
    private Map<IService, Stack<ServiceState>> servicesStacks;

    public ServiceStateStack() {
        servicesStacks = new ConcurrentHashMap<>();
    }

    /**
     * Retourne le dernier état d'un service donné
     * @param service le service en question
     * @return son dernier état
     */
    public ServiceState getLastState(final IService service) {
        var stack = servicesStacks.get(service);
        return stack.peek();
    }

    /**
     * Push un état dans un service
     * @param service le service
     * @param state l'état
     */
    public void pushState(final IService service, final ServiceState state) {
        servicesStacks.get(service).push(state);
    }

    /**
     * Enregistre un service si ce dernier n'as pas de stack trace
     * @param service le service à enregistrer
     */
    public void registerService(final IService service) {
        if(!hasService(service)) {
            Stack<ServiceState> states = new Stack<>();
            states.add(ServiceState.UNLOADED);
            servicesStacks.put(service, states);
        }
    }

    /**
     * Vérfie si le service donné a été enregistré
     * @param service le service donné
     * @return si il a été enregistré
     */
    public boolean hasService(final IService service) {
        return servicesStacks.containsKey(service);
    }
}
