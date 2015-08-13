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
import java.util.ArrayList;
import java.util.List;

import net.techcable.techutils.config.AnnotationConfig;
import net.techcable.techutils.config.ConfigSerializer;
import net.techcable.techutils.config.ListOf;

import org.bukkit.configuration.InvalidConfigurationException;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class ListSerializer implements ConfigSerializer<List<?>> {

    @Override
    public Object serialize(List<?> list, final Annotation[] annotations) {
        ListOf annotationTemp = null;
        for (Annotation a : annotations) {
            if (a instanceof ListOf) annotationTemp = (ListOf) a;
        }
        final ListOf annotation = annotationTemp;
        return Lists.transform(list, new Function<Object, Object>() {

            @Override
            @SneakyThrows // Bleeping Function
            public Object apply(Object java) {
                ConfigSerializer serializer = AnnotationConfig.getSerializer(java.getClass(), annotations); // Use the annotation array of the field, so every object in the list has the same serialization properties
                if (serializer == null) throw new InvalidConfigurationException("Unable to serialize: " + java.getClass().getSimpleName());
                Annotation[] annotationsOn = annotation == null ? annotation.annotations() : new Annotation[0];
                return serializer.serialize(java, annotationsOn);
            }
        });
    }

    @Override
    public List<?> deserialize(Object yaml, Class<? extends List<?>> ignored, final Annotation[] annotations) throws InvalidConfigurationException {
        final List<?> yamlList = (List) yaml;
        ListOf annotation = null;
        for (Annotation a : annotations) {
            if (a instanceof ListOf) annotation = (ListOf) a;
        }
        Class<?> deserializeTo = annotation != null ? annotation.value() : null;
        Annotation[] annotationsOn = annotation != null ? annotation.annotations() : new Annotation[0];
        return deserializeList(yamlList, deserializeTo, annotationsOn);
    }

    public List<?> deserializeList(List<?> yamlList, final Class<?> deserializeTo, final Annotation[] annotations) throws InvalidConfigurationException{
        List java = new ArrayList<>();
        for (Object yaml : yamlList) {
            if (yaml instanceof List) {
                List subList = deserializeList((List<?>)yamlList, deserializeTo, annotations);
                java.add(subList);
            } else {
                ConfigSerializer serializer = AnnotationConfig.getDeserializer(yaml.getClass(), deserializeTo, annotations);
                Object element = serializer.deserialize(yaml, deserializeTo, annotations);
                java.add(element);
            }
        }
        return java;
    }


    @Override
    public boolean canDeserialize(Class<?> type, Class<?> into) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public boolean canSerialize(Class<?> type) {
        return List.class.isAssignableFrom(type);
    }
}
