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

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Config extends MapConfigMapper implements IConfig {
    public Config() {

    }

    public Config(String filename, String ... header) {
        CONFIG_FILE = new File(filename + (filename.endsWith(".yml") ? "" : ".yml"));
        CONFIG_HEADER = header;
    }

    @Override
    public void save() throws InvalidConfigurationException {
        if (CONFIG_FILE == null) {
            throw new IllegalArgumentException("Saving a config without given File");
        }

        if (root == null) {
            root = new ConfigSection();
        }

        clearComments();

        internalSave(getClass());
        saveToYaml();
    }

    private void internalSave(Class clazz) throws InvalidConfigurationException {
        if (!clazz.getSuperclass().equals(Config.class)) {
            internalSave(clazz.getSuperclass());
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (doSkip(field)) continue;

            String path = (CONFIG_MODE.equals(ConfigMode.DEFAULT)) ? field.getName().replaceAll("_", ".") : field.getName();

            ArrayList<String> comments = new ArrayList<>();
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation instanceof Comment) {
                    Comment comment = (Comment) annotation;
                    comments.add(comment.value());

                }

                if (annotation instanceof Comments) {
                    Comments comment = (Comments) annotation;
                    comments.addAll(Arrays.asList(comment.value()));
                }
            }

            if (field.isAnnotationPresent(Path.class)) {
                Path path1 = field.getAnnotation(Path.class);
                path = path1.value();
            }

            if (comments.size() > 0) {
                for (String comment : comments) {
                    addComment(path, comment);
                }
            }

            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }

            try {
                converter.toConfig(this, field, root, path);
                converter.fromConfig(this, field, root, path);
            } catch (Exception e) {
                if (!skipFailedObjects) {
                    throw new InvalidConfigurationException("Could not save the Field", e);
                }
            }
        }
    }

    @Override
    public void save(File file) throws InvalidConfigurationException {
        if (file == null) {
            throw new IllegalArgumentException("File argument can not be null");
        }

        CONFIG_FILE = file;
        save();
    }

    @Override
    public void init() throws InvalidConfigurationException {
        if (!CONFIG_FILE.exists()) {
            if (CONFIG_FILE.getParentFile() != null)
                CONFIG_FILE.getParentFile().mkdirs();

            try {
                CONFIG_FILE.createNewFile();
                save();
            } catch (IOException e) {
                throw new InvalidConfigurationException("Could not create new empty Config", e);
            }
        } else {
            load();
        }
    }

    @Override
    public void init(File file) throws InvalidConfigurationException {
        if (file == null) {
            throw new IllegalArgumentException("File argument can not be null");
        }

        CONFIG_FILE = file;
        init();
    }

    @Override
    public void reload() throws InvalidConfigurationException {
        loadFromYaml();
        internalLoad(getClass());
    }

    @Override
    public void load() throws InvalidConfigurationException {
        if (CONFIG_FILE == null) {
            throw new IllegalArgumentException("Loading a config without given File");
        }

        loadFromYaml();
        update(root);
        internalLoad(getClass());
    }

    private void internalLoad(Class clazz) throws InvalidConfigurationException {
        if (!clazz.getSuperclass().equals(Config.class)) {
            internalLoad(clazz.getSuperclass());
        }

        boolean save = false;
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

            if (root.has(path)) {
                try {
                    converter.fromConfig(this, field, root, path);
                } catch (Exception e) {
                    throw new InvalidConfigurationException("Could not set field", e);
                }
            } else {
                try {
                    converter.toConfig(this, field, root, path);
                    converter.fromConfig(this, field, root, path);

                    save = true;
                } catch (Exception e) {
                    if (!skipFailedObjects) {
                        throw new InvalidConfigurationException("Could not get field", e);
                    }
                }
            }
        }

        if (save) {
            saveToYaml();
        }
    }

    @Override
    public void load(File file) throws InvalidConfigurationException {
        if (file == null) {
            throw new IllegalArgumentException("File argument can not be null");
        }

        CONFIG_FILE = file;
        load();
    }
}
