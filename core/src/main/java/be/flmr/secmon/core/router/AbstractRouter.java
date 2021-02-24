package be.flmr.secmon.core.router;

import be.flmr.secmon.core.pattern.PatternUtils;
import be.flmr.secmon.core.pattern.ProtocolPacket;
import be.flmr.secmon.core.pattern.ProtocolPattern;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public abstract class AbstractRouter {
    private Map<Method, ProtocolPattern> protocols;

    public void execute(String input) {
        for (Map.Entry<Method, ProtocolPattern> entry : protocols.entrySet()) {
            Method method = entry.getKey();
            ProtocolPattern pattern = entry.getValue();

            final var parameters = method.getParameters();
            if (Arrays.stream(parameters).anyMatch(p -> p.getAnnotation(Group.class) == null))
                throw new IllegalArgumentException("Tous les paramètres de '" + method.getName() + "' ne sont pas annotés avec Group");

            final var matches = new String[parameters.length];

            if (input.matches(pattern.getPattern())) {
                for (int i = 0; i < parameters.length; i++) {
                    final var parameter = parameters[i];
                    final var annotation = parameter.getAnnotation(Group.class);

                    try {
                        matches[i] = PatternUtils.extractGroup(input, pattern.getPattern(), annotation.group().name());
                    } catch (IllegalArgumentException e) {
                        if (annotation.nullable())
                            matches[i] = null; // Quand le groupe n'est pas présent dans la regex, le string par défaut est null
                        else
                            throw new RuntimeException("Le paramètre " + parameter.getName() + " a été renseigné comme étant non null", e);
                    }
                }

                try {
                    method.setAccessible(true);
                    method.invoke(this, (Object[]) matches);
                    method.setAccessible(false);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}