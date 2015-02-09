package net.techcable.techutils.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Setting {
    
    /**
     * Get the settings configuration key
     * 
     * @return this setting's configuraiton key
     */
    public String value();
}