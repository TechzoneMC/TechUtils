package net.techcable.techutils.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface ConfigSerializer<T> {
    /**
     * Serialize the specified object to a yaml representation
     *
     * @param t the object to seralize
     * @return the yaml representation of the object
     */
    public Object serialize(T t);
    public T deserialize(Object yaml, Class<? extends T> type) throws InvalidConfigurationException;
    public boolean canHandle(Class<?> type);
}
