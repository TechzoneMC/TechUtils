package net.techcable.techutils.config.seralizers;

import net.techcable.techutils.config.ConfigSerializer;
import org.bukkit.configuration.InvalidConfigurationException;

public class DoubleSerializer implements ConfigSerializer<Double> {
    @Override
    public Object serialize(Double aDouble) {
        return aDouble;
    }

    @Override
    public Double deserialize(Object yaml, Class<? extends Double> type) throws InvalidConfigurationException {
        return (Double) yaml;
    }

    @Override
    public boolean canHandle(Class<?> type) {
        return type == double.class || type == Double.class;
    }
}
