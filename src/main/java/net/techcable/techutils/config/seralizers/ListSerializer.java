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
package net.techcable.techutils.config.seralizers;

import lombok.*;

import java.lang.annotation.Annotation;
import java.util.List;

import net.techcable.techutils.config.AnnotationConfig;
import net.techcable.techutils.config.ConfigSerializer;

import org.bukkit.configuration.InvalidConfigurationException;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class ListSerializer implements ConfigSerializer<List<?>> {

    @Override
    public Object serialize(List<?> list, final Annotation[] annotations) {
        return Lists.transform(list, new Function<Object, Object>() {

            @Override
            @SneakyThrows // Bleeping Function
            public Object apply(Object java) {
                ConfigSerializer serializer = AnnotationConfig.getSerializer(java.getClass(), annotations); // Use the annotation array of the field, so every object in the list has the same serialization properties
                if (serializer == null) throw new InvalidConfigurationException("Unable to serialize: " + java.getClass().getSimpleName());
                return serializer.serialize(java, annotations);
            }
        });
    }

    @Override
    public List<?> deserialize(Object yaml, final Class<? extends List<?>> deserializeTo, final Annotation[] annotations) throws InvalidConfigurationException {
        List<?> yamlList = (List) yaml;
        return Lists.transform(yamlList, new Function<Object, Object>() {

            @Override
            @SneakyThrows // Bleeping Function
            public Object apply(Object yaml) {
                ConfigSerializer serializer = AnnotationConfig.getDeserializer(yaml.getClass(), annotations); // Use the annotation array of the field, so every object in the list has the same serialization properties
                if (serializer == null) throw new InvalidConfigurationException("Unable to deserialize: " + yaml.getClass().getSimpleName());
                return serializer.deserialize(yaml, deserializeTo, annotations);
            }
        });
    }

    @Override
    public boolean canDeserialize(Class<?> type) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public boolean canSerialize(Class<?> type) {
        return List.class.isAssignableFrom(type);
    }
}
