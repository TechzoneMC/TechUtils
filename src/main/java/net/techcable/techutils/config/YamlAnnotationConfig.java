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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import net.techcable.techutils.Reflection;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlAnnotationConfig extends AnnotationConfig {

    public void load(File configFile, URL defaultConfigUrl) throws IOException, InvalidConfigurationException {
        YamlConfiguration existingConfig;
        boolean shouldSave = false;
        if (configFile.exists()) {
            try {
                existingConfig = YamlConfiguration.loadConfiguration(Files.newReader(configFile, Charsets.UTF_8));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found after checked for existence", e);
            }
        } else {
            existingConfig = new YamlConfiguration();
            shouldSave = true;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigUrl.openStream(), Charsets.UTF_8));
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
            Object rawValue = config.get(key);
            Object javaValue = deserialize(rawValue, field, key);
            Reflection.setField(field, this, javaValue);
        }
        if (shouldSave) config.save(configFile);
    }

    @Override
    public void save(File configFile, URL defaultConfigUrl) throws IOException, InvalidConfigurationException {
        throw new UnsupportedOperationException("Saving is not supported for YAML configuration");
    }
}