package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.IService;
import be.flmr.secmon.core.net.ServiceState;
import be.flmr.secmon.core.util.Tuple;

import java.util.Map;
import java.util.Stack;

public class ServiceStateStack {
    private Map<Object, Stack<ServiceState>> servicesStacks; // TODO: Remplacer ? par IService

    public Stack<ServiceState> getStates(Object service) {
        return servicesStacks.get(service);
    }

    public ServiceState getLastState(Object service) {
        var stack = servicesStacks.get(service);
        return stack.peek();
    }

    public void pushState(Object service, ServiceState state) {
        servicesStacks.get(service).push(state);
    }

    public void registerService(Object service) {
        servicesStacks.put(service, new Stack<>());
    }

    public boolean hasService(IService service) {
        return servicesStacks.containsKey(service);
    }
}
