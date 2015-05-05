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

import net.techcable.techutils.Reflection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

public class AnnotationConfig {

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
            Reflection.setField(field, this, defaultConfig.get(key));
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
            if (!field.getType().isInstance(defaultConfig.get(key))) {
                throw new InvalidConfigurationException(key + " is not instanceof " + field.getType().getSimpleName());
            }
            Object value = Reflection.getField(field, this);
            defaultConfig.set(key, value);
        }
    }
}
