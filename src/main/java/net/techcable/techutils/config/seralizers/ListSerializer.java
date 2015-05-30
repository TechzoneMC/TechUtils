package net.techcable.techutils.config.seralizers;

import net.techcable.techutils.config.ConfigSerializer;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;

public class ListSerializer implements ConfigSerializer<List<?>> {
    @Override
    public Object serialize(List<?> objects) {
        return objects;
    }

    @Override
    public List<?> deserialize(Object yaml, Class<? extends List<?>> type) throws InvalidConfigurationException {
        return (List<?>) yaml;
    }

    @Override
    public boolean canHandle(Class<?> type) {
        return List.class.isAssignableFrom(type);
    }
}
