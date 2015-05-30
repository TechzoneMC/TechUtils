package net.techcable.techutils.config.seralizers;

import net.techcable.techutils.config.ConfigSerializer;
import org.bukkit.configuration.InvalidConfigurationException;

public class FloatSerializer implements ConfigSerializer<Float> {
    @Override
    public Object serialize(Float aFloat) {
        return aFloat;
    }

    @Override
    public Float deserialize(Object yaml, Class<? extends Float> type) throws InvalidConfigurationException {
        return (Float) yaml;
    }

    @Override
    public boolean canHandle(Class<?> type) {
        return type == float.class || type == Float.class;
    }
}
