package be.flmr.secmon.core.router;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class AbstractRouter {
    private Map<Method, String> protocols;

    public void execute(String input) {
        for(Map.Entry<Method, String> entry : protocols.entrySet()) {
            Method method = entry.getKey();
            String regex = entry.getValue();

            final var parameters = method.getParameters();
            if (Arrays.stream(parameters).anyMatch(p -> p.getAnnotation(Group.class) == null))
                throw new IllegalArgumentException("Tous les paramètres de '" + method.getName() + "' ne sont pas annotés avec Group");

            final var pattern = Pattern.compile(regex);
            final var matcher = pattern.matcher(input);
            final var matches = new String[parameters.length];

            if(matcher.matches()) {
                for (int i = 0; i < parameters.length; i++) {
                    final var parameter = parameters[i];
                    final var annotation = parameter.getAnnotation(Group.class);

                    try {
                        matches[i] = matcher.group(annotation.groupName());
                    } catch (IllegalArgumentException e) {
                        if (annotation.nullable())
                            matches[i] = null; // Quand le groupe n'est pas présent dans la regex, le string par défaut est null
                        else throw new RuntimeException("Le paramètre " + parameter.getName() + " a été renseigné comme étant non null", e);
                    }
                }

                try {
                    method.setAccessible(true);
                    method.invoke(this, (Object[]) matches);
                    method.setAccessible(false);
                } catch(IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}