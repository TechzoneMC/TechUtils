package net.techcable.techutils.config.seralizers;

import net.techcable.techutils.config.ConfigSerializer;
import org.bukkit.configuration.InvalidConfigurationException;

public class ByteSeralizer implements ConfigSerializer<Byte> {
    @Override
    public Object serialize(Byte o) {
        return o;
    }

    @Override
    public Byte deserialize(Object yaml, Class<? extends Byte> type) throws InvalidConfigurationException {
        return (Byte) yaml;
    }

    public boolean canHandle(Class<?> type) {
        return type == byte.class || type == Byte.class;
    }
}
