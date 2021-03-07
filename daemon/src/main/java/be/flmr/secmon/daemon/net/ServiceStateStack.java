package be.flmr.secmon.daemon.net;

import be.flmr.secmon.core.net.IService;
import be.flmr.secmon.core.net.ServiceState;
import be.flmr.secmon.core.util.Tuple;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceStateStack {
    private Map<IService, Stack<ServiceState>> servicesStacks;

    public ServiceStateStack() {
        servicesStacks = new ConcurrentHashMap<>();
    }

    public ServiceState getLastState(final IService service) {
        var stack = servicesStacks.get(service);
        return stack.peek();
    }

    public void pushState(final IService service, final ServiceState state) {
        servicesStacks.get(service).push(state);
    }

    public void registerService(final IService service) {
        if(!hasService(service)) {
            Stack<ServiceState> states = new Stack<>();
            states.add(ServiceState.UNLOADED);
            servicesStacks.put(service, states);
        }
    }

    public boolean hasService(final IService service) {
        return servicesStacks.containsKey(service);
    }
}
