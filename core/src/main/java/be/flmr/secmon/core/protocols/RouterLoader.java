package be.flmr.secmon.core.protocols;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RouterLoader {
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

        final Map<Method, String> protocols = new HashMap<>();

        if(type.getAnnotation(Router.class) == null) return null;
        Method[] methods = type.getDeclaredMethods();

        for (final Method method : methods) {
            if(method.getAnnotation(Protocol.class) != null) {
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
