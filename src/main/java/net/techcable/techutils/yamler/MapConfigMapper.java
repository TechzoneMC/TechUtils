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
package net.techcable.techutils.yamler;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import net.techcable.techutils.yamler.Config;

import net.techcable.techutils.yamler.converter.Converter;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class MapConfigMapper extends YamlConfigMapper {
    public Map<String, Object> saveToMap(Class clazz) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        if (!clazz.getSuperclass().equals(Config.class) && !clazz.getSuperclass().equals(Object.class)) {
            Map<String, Object> map = saveToMap(clazz.getSuperclass());
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                returnMap.put( entry.getKey(), entry.getValue() );
            }
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (doSkip(field)) continue;

            String path = (CONFIG_MODE.equals(ConfigMode.DEFAULT)) ? field.getName().replaceAll("_", ".") : field.getName();

            if (field.isAnnotationPresent(Path.class)) {
                Path path1 = field.getAnnotation(Path.class);
                path = path1.value();
            }

            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }

            try {
                returnMap.put(path, field.get(this));
            } catch (IllegalAccessException e) { }
        }

        Converter mapConverter = converter.getConverter(Map.class);
        return (Map<String, Object>) mapConverter.toConfig(HashMap.class, returnMap, null);
    }

    public void loadFromMap(Map section, Class clazz) throws Exception {
        if (!clazz.getSuperclass().equals(Config.class) && !clazz.getSuperclass().equals(Config.class)) {
            loadFromMap(section, clazz.getSuperclass());
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (doSkip(field)) continue;

            String path = (CONFIG_MODE.equals(ConfigMode.DEFAULT)) ? field.getName().replaceAll("_", ".") : field.getName();

            if (field.isAnnotationPresent(Path.class)) {
                Path path1 = field.getAnnotation(Path.class);
                path = path1.value();
            }

            if(Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }

            converter.fromConfig((Config) this, field, ConfigSection.convertFromMap(section), path);
        }
    }
}
