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

public class AnnotationConfig {

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

    public static void addSerializer(ConfigSerializer serializer) {
        serializers.add(serializer);
    }

    private static final Set<ConfigSerializer> serializers = Collections3.newConcurrentHashSet();

    public static ConfigSerializer getSerializer(Class<?> type) {
        for (ConfigSerializer serializer : serializers) {
            if (serializer.canHandle(type)) return serializer;
        }
        return null;
    }

    public void load(File configFile, URL defaultConfigUrl) throws IOException, InvalidConfigurationException {
        YamlConfiguration existing;
        if (configFile.exists()) {
            existing = YamlConfiguration.loadConfiguration(configFile);
        } else {
            existing = new YamlConfiguration();
        }
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigUrl.openStream());
        for (String key : defaultConfig.getKeys(true)) {
            if (existing.contains(key)) {
                defaultConfig.set(key, existing.get(key));
            }
        }
        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Setting.class)) continue;
            String key = field.getAnnotation(Setting.class).value();
            if (!defaultConfig.contains(key)) {
                throw new InvalidConfigurationException("Unknown key: " + key);
            }
            if (!field.getType().isInstance(defaultConfig.get(key))) {
                throw new InvalidConfigurationException(key + " is not instanceof " + field.getType().getSimpleName());
            }
            Object yaml = defaultConfig.get(key);
            if (getSerializer(field.getType()) == null) throw new InvalidConfigurationException("No seralizer for the type " + field.getType().getSimpleName());
            Object java = getSerializer(field.getType()).deserialize(yaml, field.getType());
            Reflection.setField(field, this, 1);
        }
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
            if (getSerializer(rawValue.getClass()) == null) throw new InvalidConfigurationException("No seralizer for the type " + rawValue.getClass().getSimpleName());
            Object yamlValue = getSerializer(rawValue.getClass()).serialize(rawValue);
            defaultConfig.set(key, yamlValue);
        }
    }
}
