package be.flmr.secmon.core.router;

import be.flmr.secmon.core.pattern.ProtocolPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RouterLoader {
    private static final Logger log = LoggerFactory.getLogger(RouterLoader.class);
    public static <T> T loadRouter(final Class<T> type) {
        T instance;

        try {
            final Constructor<T> noArgConstructor = type.getDeclaredConstructor();

            noArgConstructor.setAccessible(true);
            instance = noArgConstructor.newInstance();
            noArgConstructor.setAccessible(false);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Problème lors de la création de l'instance du Constructeur.", e);
        }

        final Map<Method, ProtocolPattern> protocols = new HashMap<>();

        if (type.getAnnotation(Router.class) == null)
            log.warn("La classe " + type.getName() + " n'est pas annotée avec l'annotation 'Router'. Il est conseillé de l'annoter.");
        Method[] methods = type.getDeclaredMethods();

        for (final Method method : methods) {
            if (method.getAnnotation(Protocol.class) != null) {
                protocols.put(method, method.getAnnotation(Protocol.class).pattern());
            }
        }

        try {
            Field map = type.getSuperclass().getDeclaredField("protocols");

            map.setAccessible(true);
            map.set(instance, protocols);
            map.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Il y a eu un problème avec l'injection de la map de protocols", e);
        }

        return instance;
    }
}