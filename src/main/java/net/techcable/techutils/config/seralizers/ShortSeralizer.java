package net.techcable.techutils.config.seralizers;

import net.techcable.techutils.config.ConfigSerializer;
import org.bukkit.configuration.InvalidConfigurationException;

public class ShortSeralizer implements ConfigSerializer<Short> {
    @Override
    public Object serialize(Short aShort) {
        return aShort;
    }

    @Override
    public Short deserialize(Object yaml, Class<? extends Short> type) throws InvalidConfigurationException {
        return (Short) yaml;
    }

    @Override
    public boolean canHandle(Class<?> type) {
        return type == short.class || type == Short.class;
    }
}
