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

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import com.torchmind.candle.Candle;
import com.torchmind.candle.api.IArrayPropertyNode;
import com.torchmind.candle.api.IDocumentNode;
import com.torchmind.candle.api.INamedNode;
import com.torchmind.candle.api.INode;
import com.torchmind.candle.api.IObjectNode;
import com.torchmind.candle.api.IPropertyNode;
import com.torchmind.candle.api.error.CandleException;
import com.torchmind.candle.node.property.BooleanPropertyNode;
import com.torchmind.candle.node.property.EnumPropertyNode;
import com.torchmind.candle.node.property.FloatPropertyNode;
import com.torchmind.candle.node.property.IntegerPropertyNode;
import com.torchmind.candle.node.property.StringPropertyNode;
import com.torchmind.candle.node.property.array.BooleanArrayPropertyNode;
import com.torchmind.candle.node.property.array.EnumArrayPropertyNode;
import com.torchmind.candle.node.property.array.FloatArrayPropertyNode;
import com.torchmind.candle.node.property.array.IntegerArrayPropertyNode;
import com.torchmind.candle.node.property.array.NullArrayPropertyNode;
import com.torchmind.candle.node.property.array.StringArrayPropertyNode;
import net.techcable.techutils.Reflection;
import net.techcable.techutils.collect.Collections3;
import net.techcable.techutils.config.seralizers.BooleanSerializer;
import net.techcable.techutils.config.seralizers.ByteSeralizer;
import net.techcable.techutils.config.seralizers.CharSerializer;
import net.techcable.techutils.config.seralizers.DoubleSerializer;
import net.techcable.techutils.config.seralizers.EnumSerializer;
import net.techcable.techutils.config.seralizers.FloatSerializer;
import net.techcable.techutils.config.seralizers.IntSerializer;
import net.techcable.techutils.config.seralizers.ListSerializer;
import net.techcable.techutils.config.seralizers.LongSerializer;
import net.techcable.techutils.config.seralizers.ShortSeralizer;
import net.techcable.techutils.config.seralizers.StringSerializer;
import net.techcable.techutils.config.seralizers.TimeSerializer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AnnotationConfig {

    private static final ListMultimap<Class<? extends Annotation>, ConfigSerializer> serializers = Collections3.newCopyOnWritetListMultimap();
    private static final Map<Class<? extends Annotation>, Lock> locks = Collections.synchronizedMap(new HashMap<Class<? extends Annotation>, Lock>());

    public static void addSerializer(Class<? extends Annotation> annotation, ConfigSerializer serializer) {
        Lock lock = getOrCreateLock(annotation);
        lock.lock();
        try {
            if (hasSerializer(annotation, serializer.getClass()))
                throw new IllegalStateException("Serializer of type " + serializer.getClass().getSimpleName() + " already exists" + (Setting.class == annotation ? "." : " for annotation " + annotation.getSimpleName() + "."));
            serializers.put(annotation, serializer);
        } finally {
            lock.unlock();
        }
    }

    public static Lock getOrCreateLock(Class<? extends Annotation> annotation) {
        Lock lock = locks.get(annotation);
        if (lock == null) {
            synchronized (locks) {
                lock = locks.get(annotation); // refresh in case someone else created
                if (lock == null) { // if someone else hasn't created, we do
                    lock = new ReentrantLock();
                    locks.put(annotation, lock);
                }
            }
        }
        return lock;
    }

    public static void addSerializerBefore(ConfigSerializer serializer, Class<? extends ConfigSerializer> before) {
        addSerializerBefore(Setting.class, serializer, before);
    }

    public static void addSerializerBefore(Class<? extends Annotation> annotation, ConfigSerializer serializer, Class<? extends ConfigSerializer> after) {
        Lock lock = getOrCreateLock(annotation);
        lock.lock();
        try {
            if (hasSerializer(annotation, serializer.getClass()))
                throw new IllegalStateException("Serializer of type " + serializer.getClass().getSimpleName() + " already exists" + (Setting.class == annotation ? "." : " for annotation " + annotation.getSimpleName() + "."));
            List<ConfigSerializer> serializers = AnnotationConfig.serializers.get(annotation);
            for (int i = 0; i < serializers.size(); i++) {
                ConfigSerializer existing = serializers.get(i);
                if (existing.getClass() != after) continue;
                serializers.add(i, serializer); // Shifts current to the right
                return; // finally block unlocks
            }
            throw new IllegalStateException("Can insert serializer after " + after.getSimpleName());
        } finally {
            lock.unlock();
        }
    }

    public static void addSerializer(ConfigSerializer serializer) {
        addSerializer(Setting.class, serializer);
    }

    public static boolean hasSerializer(Class<? extends Annotation> annotation, Class<? extends ConfigSerializer> serializerType) {
        Lock lock = getOrCreateLock(annotation);
        lock.lock();
        try {
            for (ConfigSerializer existing : serializers.get(annotation)) {
                if (existing.getClass() == serializerType) return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    static {
        addSerializer(new BooleanSerializer());
        addSerializer(new ByteSeralizer());
        addSerializer(new CharSerializer());
        addSerializer(new DoubleSerializer());
        addSerializer(new FloatSerializer());
        addSerializer(new IntSerializer());
        addSerializer(new ListSerializer());
        addSerializer(new LongSerializer());
        addSerializer(new ShortSeralizer());
        addSerializer(new StringSerializer());
        addSerializer(Time.class, new TimeSerializer());
        addSerializerBefore(new EnumSerializer(), StringSerializer.class);
    }

    public static ConfigSerializer getDeserializer(Class<?> rawType, Class<?> into, Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if (Setting.class.isAssignableFrom(annotation.annotationType())) continue;
            for (ConfigSerializer<?> serializer : serializers.get(annotation.annotationType())) {
                if (serializer.canDeserialize(rawType, into)) {
                    return serializer;
                }
            }
        }
        for (ConfigSerializer<?> serializer : serializers.get(Setting.class)) {
            if (serializer.canDeserialize(rawType, into)) {
                return serializer;
            }
        }
        return null;
    }

    public static ConfigSerializer getSerializer(Class<?> type, Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if (Setting.class.isInstance(annotation)) continue;
            for (ConfigSerializer<?> serializer : serializers.get(annotation.getClass())) {
                if (serializer.canSerialize(type)) {
                    return serializer;
                }
            }
        }
        for (ConfigSerializer<?> serializer : serializers.get(Setting.class)) {
            if (serializer.canSerialize(type)) {
                return serializer;
            }
        }
        return null;
    }

    public abstract void load(File configFile, URL defaultConfigUrl) throws IOException, InvalidConfigurationException;

    public abstract void save(File configFile, URL defaultConfigUrl) throws IOException, InvalidConfigurationException;

    protected Object deserialize(Object rawValue, Field field, String key) throws InvalidConfigurationException {
        Class<?> rawType = Primitives.unwrap(rawValue.getClass());
        ConfigSerializer serializer = getDeserializer(rawType, field.getType(), field.getDeclaredAnnotations());
        if (serializer == null)
            throw new InvalidConfigurationException("No serializer for the type " + field.getType().getSimpleName());
        Object java = serializer.deserialize(rawValue, field.getType(), field.getDeclaredAnnotations());
        Class<?> javaType = Primitives.unwrap(field.getType());
        if (!Primitives.isWrapperType(java.getClass()) && !javaType.isPrimitive() && !javaType.isInstance(java)) { // Ignore primitives, due to widening references, although it isn't the best solution
            throw new InvalidConfigurationException(key + " is not instanceof " + field.getType().getSimpleName() + ", it is " + java.getClass().getSimpleName());
        }
        return java;
    }

    protected Object serialize(Object javaValue, Field field) throws InvalidConfigurationException {
        Class<?> javaType = Primitives.unwrap(field.getType());
        ConfigSerializer serializer = getSerializer(javaType, field.getDeclaredAnnotations());
        if (serializer == null)
            throw new InvalidConfigurationException("No seralizer for the type " + javaType.getSimpleName());
        return serializer.serialize(javaValue, field.getDeclaredAnnotations());
    }

}
