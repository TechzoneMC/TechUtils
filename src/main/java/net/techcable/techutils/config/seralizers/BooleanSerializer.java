package net.techcable.techutils.config.seralizers;

import net.techcable.techutils.config.ConfigSerializer;
import org.bukkit.configuration.InvalidConfigurationException;

public class BooleanSerializer implements ConfigSerializer<Boolean> {
    @Override
    public Object serialize(Boolean aBoolean) {
        return aBoolean;
    }

    @Override
    public Boolean deserialize(Object yaml, Class<? extends Boolean> type) throws InvalidConfigurationException {
        return (Boolean) yaml;
    }

    @Override
    public boolean canHandle(Class<?> type) {
        return type == boolean.class || type == Boolean.class;
    }
}
