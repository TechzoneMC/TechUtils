package net.techcable.techutils.config.seralizers;

import net.techcable.techutils.config.ConfigSerializer;
import org.bukkit.configuration.InvalidConfigurationException;

public class StringSerializer implements ConfigSerializer<String> {
    @Override
    public Object serialize(String s) {
        return s;
    }

    @Override
    public String deserialize(Object yaml, Class<? extends String> type) throws InvalidConfigurationException {
        return (String) yaml;
    }

    @Override
    public boolean canHandle(Class<?> type) {
        return type == String.class;
    }
}
