/**
 * The MIT License
 * Copyright (c) 2014-2015 Techcable
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.techcable.techutils.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Set;

import net.techcable.techutils.Reflection;
import net.techcable.techutils.collect.Collections3;
import net.techcable.techutils.config.seralizers.BooleanSerializer;
import net.techcable.techutils.config.seralizers.ByteSeralizer;
import net.techcable.techutils.config.seralizers.CharSerializer;
import net.techcable.techutils.config.seralizers.DoubleSerializer;
import net.techcable.techutils.config.seralizers.FloatSerializer;
import net.techcable.techutils.config.seralizers.IntSeralizer;
import net.techcable.techutils.config.seralizers.ListSerializer;
import net.techcable.techutils.config.seralizers.LongSerializer;
import net.techcable.techutils.config.seralizers.ShortSeralizer;
import net.techcable.techutils.config.seralizers.StringSerializer;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.primitives.Primitives;

public class AnnotationConfig {

    private static final Set<ConfigSerializer> serializers = Collections3.newConcurrentHashSet();

    public static void addSerializer(ConfigSerializer serializer) {
        serializers.add(serializer);
    }

    static {
        addSerializer(new BooleanSerializer());
        addSerializer(new ByteSeralizer());
        addSerializer(new CharSerializer());
        addSerializer(new DoubleSerializer());
        addSerializer(new FloatSerializer());
        addSerializer(new IntSeralizer());
        addSerializer(new ListSerializer());
        addSerializer(new LongSerializer());
        addSerializer(new ShortSeralizer());
        addSerializer(new StringSerializer());
    }

    public static ConfigSerializer getSerializer(Class<?> type) {
        for (ConfigSerializer serializer : serializers) {
            if (serializer.canHandle(type)) return serializer;
        }
        return null;
    }

    public void load(File configFile, URL defaultConfigUrl) throws IOException, InvalidConfigurationException {
        YamlConfiguration existingConfig;
        boolean shouldSave = false;
        if (configFile.exists()) {
            existingConfig = YamlConfiguration.loadConfiguration(configFile);
        } else {
            existingConfig = new YamlConfiguration();
            shouldSave = true;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(defaultConfigUrl.openStream());
        for (String key : config.getKeys(true)) {
            if (!existingConfig.contains(key)) {
                shouldSave = true;
            } else {
                config.set(key, existingConfig.get(key));
            }
        }
        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Setting.class)) continue;
            String key = field.getAnnotation(Setting.class).value();
            if (!config.contains(key)) {
                throw new InvalidConfigurationException("Unknown key: " + key);
            }
            Object yaml = config.get(key);
            Class<?> yamlType = Primitives.isWrapperType(yaml.getClass()) ? Primitives.unwrap(yaml.getClass()) : yaml.getClass();
            if (getSerializer(field.getType()) == null) throw new InvalidConfigurationException("No seralizer for the type " + field.getType().getSimpleName());
            Object java = getSerializer(field.getType()).deserialize(yaml, field.getType());
            Class<?> javaType = Primitives.isWrapperType(java.getClass()) ? Primitives.unwrap(java.getClass()) : java.getClass();
            if (!field.getType().isAssignableFrom(javaType)) {
                throw new InvalidConfigurationException(key + " is not instanceof " + field.getType().getSimpleName());
            }
            Reflection.setField(field, this, java);
        }
        if (shouldSave) config.save(configFile);
    }

    public void save(File configFile, URL defaultConfigUrl) throws IOException, InvalidConfigurationException {
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigUrl.openStream());
        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Setting.class)) continue;
            String key = field.getAnnotation(Setting.class).value();
            if (!defaultConfig.contains(key)) {
                throw new InvalidConfigurationException("Unknown key: " + key);
            }
            Object rawValue = Reflection.getField(field, this);
            Class<?> javaType = rawValue.getClass().isPrimitive() ? Primitives.wrap(rawValue.getClass()) : rawValue.getClass();
            if (getSerializer(javaType) == null) throw new InvalidConfigurationException("No seralizer for the type " + rawValue.getClass().getSimpleName());
            Object yamlValue = getSerializer(rawValue.getClass()).serialize(rawValue);
            defaultConfig.set(key, yamlValue);
        }
        defaultConfig.save(configFile);
    }
}
