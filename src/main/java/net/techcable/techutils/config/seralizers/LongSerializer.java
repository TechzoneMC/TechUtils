package net.techcable.techutils.config.seralizers;

import net.techcable.techutils.config.ConfigSerializer;
import org.bukkit.configuration.InvalidConfigurationException;

public class LongSerializer implements ConfigSerializer<Long> {
    @Override
    public Object serialize(Long aLong) {
        return aLong;
    }

    @Override
    public Long deserialize(Object yaml, Class<? extends Long> type) throws InvalidConfigurationException {
        return (Long) yaml;
    }

    @Override
    public boolean canHandle(Class<?> type) {
        return type == long.class || type == Long.class;
    }
}
