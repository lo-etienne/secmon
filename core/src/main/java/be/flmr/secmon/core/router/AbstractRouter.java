package be.flmr.secmon.core.router;

import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.PatternUtils;
import be.flmr.secmon.core.pattern.ProtocolPacket;
import be.flmr.secmon.core.pattern.ProtocolPattern;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public abstract class AbstractRouter {
    // Injected
    private Map<Method, ProtocolPattern> protocols;
    private Object sender;

    public final void execute(Object sender, IProtocolPacket input) {
        for (Map.Entry<Method, ProtocolPattern> entry : protocols.entrySet()) {
            Method method = entry.getKey();
            ProtocolPattern pattern = entry.getValue();

            if (pattern.equals(input.getType())) {
                try {
                    method.setAccessible(true);
                    method.invoke(this, sender, input);
                    method.setAccessible(false);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public final Object getSender() {
        return sender;
    }
}