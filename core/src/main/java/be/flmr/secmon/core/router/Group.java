package be.flmr.secmon.core.router;

import be.flmr.secmon.core.pattern.PatternGroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Group {
    PatternGroup group();
    boolean nullable() default false;
}
