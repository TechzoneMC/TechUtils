package net.techcable.techutils.config;

import java.lang.reflect.Field;

public class ConfigField {
    public ConfigField(Field field) {
        this.field = field;
    }
    private final Field field;
    
    public String getName() {
        if (!getAnnotation().value().isEmpty()) {
            return getAnnotation().value();
        }
        return field.getName();
    }
    
    public void setValue(Object instance, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public Object getValue(Object instance) {
        try {
            field.setAccessible(true);
            field.get(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public ConfigOption getAnnotation() {
        return field.getAnnotation(ConfigOption.class);
    }
}