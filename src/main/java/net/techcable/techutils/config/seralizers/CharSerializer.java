package net.techcable.techutils.config.seralizers;

import net.techcable.techutils.config.ConfigSerializer;
import org.bukkit.configuration.InvalidConfigurationException;

public class CharSerializer implements ConfigSerializer<Character> {
    @Override
    public Object serialize(Character character) {
        return character;
    }

    @Override
    public Character deserialize(Object yaml, Class<? extends Character> type) throws InvalidConfigurationException {
        return (Character) yaml;
    }

    @Override
    public boolean canHandle(Class<?> type) {
        return type == char.class || type == Character.class;
    }
}
