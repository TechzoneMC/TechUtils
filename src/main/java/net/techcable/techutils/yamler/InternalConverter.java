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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import net.techcable.techutils.yamler.converter.Array;
import net.techcable.techutils.yamler.converter.Converter;
import net.techcable.techutils.yamler.converter.List;
import net.techcable.techutils.yamler.converter.Map;
import net.techcable.techutils.yamler.converter.Primitive;
import net.techcable.techutils.yamler.converter.Set;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class InternalConverter {
    private LinkedHashSet<Converter> converters = new LinkedHashSet<>();
    private java.util.List<Class> customConverters = new ArrayList<>();

    public InternalConverter() {
        try {
            addConverter(Primitive.class);
            addConverter(net.techcable.techutils.yamler.converter.Config.class);
            addConverter(List.class);
            addConverter(Map.class);
            addConverter(Array.class);
            addConverter(Set.class);
        } catch (InvalidConverterException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void addConverter(Class converter) throws InvalidConverterException {
        if (!Converter.class.isAssignableFrom(converter)) {
            throw new InvalidConverterException(converter.getName() + " does not implement the Interface Converter");
        }

        try {
            Converter converter1 = (Converter) converter.getConstructor(InternalConverter.class).newInstance(this);
            converters.add(converter1);
        } catch (NoSuchMethodException e) {
            throw new InvalidConverterException("Converter does not implement a Constructor which takes the InternalConverter instance", e);
        } catch (InvocationTargetException e) {
            throw new InvalidConverterException("Converter could not be invoked", e);
        } catch (InstantiationException e) {
            throw new InvalidConverterException("Converter could not be instantiated", e);
        } catch (IllegalAccessException e) {
            throw new InvalidConverterException("Converter does not implement a public Constructor which takes the InternalConverter instance", e);
        }
    }

    public Converter getConverter(Class type) {
        for(Converter converter : converters) {
            if (converter.supports(type)) {
                return converter;
            }
        }

        return null;
    }

    public void fromConfig(Config config, Field field, ConfigSection root, String path) throws Exception {
        Object obj = field.get(config);

        Converter converter;

        if (obj != null) {
            converter = getConverter(obj.getClass());

            if (converter != null) {
                field.set(config, converter.fromConfig(obj.getClass(), root.get(path), (field.getGenericType() instanceof ParameterizedType) ? (ParameterizedType) field.getGenericType() : null));
                return;
            } else {
                converter = getConverter(field.getType());
                if (converter != null) {
                    field.set(config, converter.fromConfig(field.getType(), root.get(path), (field.getGenericType() instanceof ParameterizedType) ? (ParameterizedType) field.getGenericType() : null));
                    return;
                }
            }
        } else {
            converter = getConverter(field.getType());

            if (converter != null) {
                field.set(config, converter.fromConfig(field.getType(), root.get(path), (field.getGenericType() instanceof ParameterizedType) ? (ParameterizedType) field.getGenericType() : null));
                return;
            }
        }

        field.set(config, root.get(path));
    }

    public void toConfig(Config config, Field field, ConfigSection root, String path) throws Exception {
        Object obj = field.get(config);

        Converter converter;

        if (obj != null) {
            converter = getConverter(obj.getClass());

            if (converter != null) {
                root.set(path, converter.toConfig(obj.getClass(), obj, (field.getGenericType() instanceof ParameterizedType) ? (ParameterizedType) field.getGenericType() : null));
                return;
            } else {
                converter = getConverter(field.getType());
                if (converter != null) {
                    root.set(path, converter.toConfig(field.getType(), obj, (field.getGenericType() instanceof ParameterizedType) ? (ParameterizedType) field.getGenericType() : null));
                    return;
                }
            }
        }

        root.set(path, obj);
    }

    public java.util.List<Class> getCustomConverters() {
        return new ArrayList<>(customConverters);
    }

    public void addCustomConverter(Class addConverter) throws InvalidConverterException {
        addConverter(addConverter);
        customConverters.add(addConverter);
    }
}
