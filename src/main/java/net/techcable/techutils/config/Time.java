package net.techcable.techutils.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Marks a value as a type
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Time {

    /**
     * Get the unit to use when the unit specifies none
     * <p/>
     * Defaults to seconds
     *
     * @return the unit to use when the unit specifies none
     */
    public TimeUnit value() default TimeUnit.SECONDS;
    public TimeUnit as() default TimeUnit.MILLISECONDS;
}
