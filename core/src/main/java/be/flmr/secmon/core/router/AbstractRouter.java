package be.flmr.secmon.core.router;

import be.flmr.secmon.core.pattern.IProtocolPacket;
import be.flmr.secmon.core.pattern.ProtocolPattern;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe abstraite à hériter pour utiliser les annotations {@link Protocol}. Ces annotations sont utilisés lorsque,
 * quand {@code execute} est executé avec un {@link IProtocolPacket} de type X, les méthodes annotées par {@link Protocol}
 * et renseignée avec un {@link ProtocolPattern} de type X, alors cette méthode est executée
 */
public abstract class AbstractRouter {
    private Map<Method, ProtocolPattern> protocols;

    /**
     * Constructeur de l'abstract router. Il faut absolument qu'il soit exécuté sinon les routers ne foncitonnent pas.
     */
    public AbstractRouter() {
        var type = getClass();
        protocols = new HashMap<>();

        Method[] methods = type.getDeclaredMethods();

        for (final Method method : methods) {
            if (method.getAnnotation(Protocol.class) != null) {
                protocols.put(method, method.getAnnotation(Protocol.class).pattern());
            }
        }
    }

    /**
     * Méthode d'exécution des méthodes annotées par {@link Protocol}.
     * @param sender objet "sender", parfois utile dans certains cas
     * @param input le packet qui provoquera l'éxécution de telle ou telle méthode.
     */
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
}