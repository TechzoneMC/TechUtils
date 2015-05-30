package net.techcable.techutils.config.seralizers;

import net.techcable.techutils.config.ConfigSerializer;
import org.bukkit.configuration.InvalidConfigurationException;

public class EnumSerializer implements ConfigSerializer<Enum> {
    @Override
    public Object serialize(Enum e) {
        return e.name().toLowerCase();
    }

    @Override
    public Enum deserialize(Object yaml, Class<? extends Enum> type) throws InvalidConfigurationException {
        Enum[] types = type.getEnumConstants();
        for (Enum constant : types) {
            if (constant.name().equalsIgnoreCase((String)yaml)) return constant;
        }
        throw new InvalidConfigurationException("Unable to find matching enum constant for " + yaml);
    }

    @Override
    public boolean canHandle(Class<?> type) {
        return Enum.class.isAssignableFrom(type);
    }
}
