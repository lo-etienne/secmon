package be.flmr.secmon.core.router;

import be.flmr.secmon.core.pattern.ProtocolPattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Protocol {
    /**
     * @return le {@link ProtocolPattern} qui détermine si la méthode devra être exécutée ou non par {@code AbstractRouter::execute}
     */
    ProtocolPattern pattern();
}