package net.techcable.techutils.config.seralizers;

import net.techcable.techutils.config.ConfigSerializer;
import org.bukkit.configuration.InvalidConfigurationException;

public class IntSeralizer implements ConfigSerializer<Integer> {
    @Override
    public Object serialize(Integer integer) {
        return integer;
    }

    @Override
    public Integer deserialize(Object yaml, Class<? extends Integer> type) throws InvalidConfigurationException {
        return (Integer) yaml;
    }

    @Override
    public boolean canHandle(Class<?> type) {
        return type == int.class || type == Integer.class;
    }
}
