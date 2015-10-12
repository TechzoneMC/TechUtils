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
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
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

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.configuration.InvalidConfigurationException;

public class CandleAnnotationConfig extends AnnotationConfig {

    public void load(File configFile, URL defaultConfigUrl) throws IOException, InvalidConfigurationException {
        Candle config = new Candle();
        boolean shouldSave = false;
        if (configFile.exists()) {
            try {
                config.read(configFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found after checked for existence", e);
            } catch (CandleException e) {
                String realMsg = getReason(e);
                throw new InvalidConfigurationException("Unable to parse config" + (realMsg != null ? ": " + realMsg : ""), e);
            }
        } else {
            shouldSave = true;
        }
        Candle defaultConfig;
        try {
            defaultConfig = new Candle().read(defaultConfigUrl.openStream());
        } catch (CandleException e) {
            String realMsg = getReason(e);
            throw new InvalidConfigurationException("Invalid default config" + (realMsg != null ? ": " + realMsg : ""), e);
        }

        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Setting.class)) continue;
            String key = field.getAnnotation(Setting.class).value();
            if (!contains(defaultConfig, key)) {
                throw new InvalidConfigurationException("Unknown key: " + key);
            }
            INamedNode candle;
            if (contains(config, key)) {
                candle = config.get(key, INamedNode.class);
            } else {
                candle = defaultConfig.get(key, INamedNode.class);
            }
            Object rawValue = fromCandle(candle);
            Object java = deserialize(rawValue, field, key);
            Reflection.setField(field, this, java);
        }
        if (shouldSave) save(configFile, defaultConfigUrl);
    }

    public void save(File configFile, URL defaultConfigUrl) throws IOException, InvalidConfigurationException {
        Candle defaultConfig;
        try {
            defaultConfig = new Candle().read(defaultConfigUrl.openStream());
        } catch (CandleException e) {
            String realMsg = getReason(e);
            throw new InvalidConfigurationException("Invalid default config" + (realMsg != null ? ": " + realMsg : ""), e);
        }
        Candle config = new Candle();
        Map<String, INamedNode> newValues = new HashMap<>();
        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Setting.class)) continue;
            String key = field.getAnnotation(Setting.class).value();
            try {
                defaultConfig.get(key);
            } catch (NoSuchElementException e) {
                throw new InvalidConfigurationException("Unknown key: " + key, e);
            }
            Object rawValue = Reflection.getField(field, this);
            if (rawValue == null) continue;
            Object yamlValue = serialize(rawValue, field);
            INamedNode candleValue = toCandle(config, key, yamlValue); // Compatibility
            newValues.put(key, candleValue);
        }
        List<INode> ordered = new ArrayList<>(defaultConfig.size());
        defaultConfig.forEach(ordered::add);
        ordered.stream().filter(INamedNode.class::isInstance).map(INamedNode.class::cast).filter((node) -> newValues.containsKey(node.name())).forEach((node) -> {
            int index = ordered.indexOf(node);
            INamedNode newValue = newValues.get(node.name());
            ordered.set(index, newValue);
        });
        config.clear();
    }

    public static final Object EMPTY_COLLECTION_FIRST_OBJECT = new Object();

    private INamedNode toCandle(IDocumentNode root, String key, Object yamlValue) throws InvalidConfigurationException {
        key = key.substring(key.lastIndexOf('.') + 1); // Use the last element in the key
        if (yamlValue instanceof Collection) {
            Collection<?> c = (Collection) yamlValue;
            c = new ArrayList<>(c); // I'm paranoid, lets make a copy!
            c = Collections.unmodifiableCollection(c); // Now prevent modifications
            Object first = Iterables.getFirst(c, EMPTY_COLLECTION_FIRST_OBJECT);
            if (first instanceof Collection)
                throw new IllegalStateException("Candle does not support arrays of arrays");
            if (first instanceof Map) throw new IllegalStateException("Candle does not support arrays of objects");
            if (first == null || first == EMPTY_COLLECTION_FIRST_OBJECT) {
                if (first == null) {
                    // In a non-empty set, check that all elements are null
                    c.forEach((e) -> Preconditions.checkState(e == null, "Not everything in the array is null. One is: %s", e.getClass().getSimpleName()));
                }
                // It doesn't matter what type we use because there are no elements
                return new NullArrayPropertyNode(root, key);
            }
            c.forEach((e) -> {
                String firstName = first.getClass().getSimpleName();
                String elementName = e == null ? "null" : e.getClass().getSimpleName();
                boolean isFirstType = (first.getClass().isInstance(e));
                Preconditions.checkState(isFirstType, "Not everything in the array is a %s. One is: %s", firstName, elementName);
            });
            if (first instanceof Boolean) {
                Boolean[] array = c.toArray(new Boolean[c.size()]);
                return new BooleanArrayPropertyNode(root, key, array);
            } else if (first instanceof Enum) { // Candle has enums, although we should use our own serializers, since they support lowercase values and spaces
                Enum[] array = c.toArray(new Enum[c.size()]);
                return new EnumArrayPropertyNode(root, key, array);
            } else if (first instanceof Float) {
                Float[] array = c.toArray(new Float[c.size()]);
                return new FloatArrayPropertyNode(root, key, array);
            } else if (first instanceof Integer) {
                Integer[] array = c.toArray(new Integer[c.size()]);
                return new IntegerArrayPropertyNode(root, key, array);
            } else if (first instanceof String) {
                String[] array = c.toArray(new String[c.size()]);
                return new StringArrayPropertyNode(root, key, array);
            } else {
                throw new IllegalStateException("Unsupported type: " + first.getClass().getSimpleName());
            }
        } else if (yamlValue instanceof Map) {
            throw new UnsupportedOperationException("Cannot currently convert maps to candle");
        } else if (yamlValue instanceof Boolean) {
            return new BooleanPropertyNode(root, key, (Boolean) yamlValue);
        } else if (yamlValue instanceof Enum) {
            return new EnumPropertyNode(root, key, (Enum) yamlValue);
        } else if (yamlValue instanceof Float) {
            return new FloatPropertyNode(root, key, (Float) yamlValue);
        } else if (yamlValue instanceof Integer) {
            return new IntegerPropertyNode(root, key, (Integer) yamlValue);
        } else if (yamlValue instanceof String) {
            return new StringPropertyNode(root, key, (String) yamlValue);
        } else {
            throw new UnsupportedOperationException("Cannot convert " + yamlValue.getClass().getSimpleName() + " into candle");
        }
    }

    private Object fromCandle(INamedNode candle) {
        if (candle instanceof IArrayPropertyNode) {
            switch (((IArrayPropertyNode) candle).itemType()) {
                case NULL:
                    return new ArrayList<>();
                case BOOLEAN:
                    BooleanArrayPropertyNode booleanArray = (BooleanArrayPropertyNode) candle;
                    boolean[] primitiveBooleans = booleanArray.array();
                    Boolean[] wrapperBooleans = ArrayUtils.toObject(primitiveBooleans);
                    return Lists.newArrayList(wrapperBooleans);
                case ENUM:
                    EnumArrayPropertyNode enumArray = (EnumArrayPropertyNode) candle;
                    String[] enumNames = enumArray.array(); // Our deserializer can handle strings (if the field is annotated correctly)
                    return Lists.newArrayList(enumNames);
                case FLOAT:
                    FloatArrayPropertyNode floatArray = (FloatArrayPropertyNode) candle;
                    float[] primitiveFloats = floatArray.array();
                    Float[] wrapperFloats = ArrayUtils.toObject(primitiveFloats);
                    return Lists.newArrayList(wrapperFloats);
                case INTEGER:
                    IntegerArrayPropertyNode integerArray = (IntegerArrayPropertyNode) candle;
                    int[] primitiveInts = integerArray.array();
                    Integer[] wrapperIntegers = ArrayUtils.toObject(primitiveInts);
                    return Lists.newArrayList(wrapperIntegers);
                case STRING:
                    StringArrayPropertyNode stringArray = (StringArrayPropertyNode) candle;
                    String[] strings = stringArray.array();
                    return Lists.newArrayList(strings);
                case ARRAY:
                    throw new IllegalArgumentException("Cannot have arrays of arrays");
                default:
                    throw new UnsupportedOperationException("Unknown type: " + ((IArrayPropertyNode) candle).itemType().name());
            }
        } else if (candle instanceof IObjectNode) {
            throw new UnsupportedOperationException("Cannot currently deserialize objects");
        } else if (candle instanceof IPropertyNode) {
            switch (((IPropertyNode) candle).valueType()) {
                case NULL:
                    return null;
                case BOOLEAN:
                    BooleanPropertyNode booleanNode = (BooleanPropertyNode) candle;
                    return booleanNode.value();
                case ENUM:
                    EnumPropertyNode enumNode = (EnumPropertyNode) candle;
                    return enumNode.value(); // Our deserializer can handle strings
                case FLOAT:
                    FloatPropertyNode floatNode = (FloatPropertyNode) candle;
                    return floatNode.value();
                case INTEGER:
                    IntegerPropertyNode intNode = (IntegerPropertyNode) candle;
                    return intNode.value();
                case STRING:
                    StringPropertyNode stringNode = (StringPropertyNode) candle;
                    return stringNode.value();
                case ARRAY:
                    throw new AssertionError("Already checked for array");
                default:
                    throw new UnsupportedOperationException("Unknown type: " + ((IPropertyNode) candle).type().name());
            }
        } else {
            throw new UnsupportedOperationException("Unknown node type: " + candle.getClass().getName());
        }
    }

    // Utilities

    private static boolean contains(IObjectNode what, String key) {
        try {
            return what.get(key) != null;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private static void copy(INamedNode from, INamedNode to) {
        to.ensureType(from.type());
        if (from instanceof IPropertyNode) {
            ((IPropertyNode) to).ensureValueType(((IPropertyNode) from).valueType());
            switch (((IPropertyNode) from).valueType()) {
                case NULL:
                    return;
                case BOOLEAN:
                    boolean b = ((BooleanPropertyNode) from).value();
                    ((BooleanPropertyNode) to).value(b);
                    return;
                case ENUM:
                    String e = ((EnumPropertyNode) from).value();
                    ((EnumPropertyNode) to).value(e);
                    return;
                case FLOAT:
                    float f = ((FloatPropertyNode) from).value();
                    ((FloatPropertyNode) to).value(f);
                    return;
                case INTEGER:
                    int i = ((IntegerPropertyNode) from).value();
                    ((IntegerPropertyNode) to).value(i);
                    return;
                case STRING:
                    String s = ((StringPropertyNode) from).value();
                    ((StringPropertyNode) to).value(s);
                    return;
                case ARRAY:
                    switch (((IArrayPropertyNode) from).itemType()) {
                        case NULL:
                            return;
                        case BOOLEAN:
                            boolean[] booleans = ((BooleanArrayPropertyNode) from).array();
                            ((BooleanArrayPropertyNode) to).array(booleans);
                            return;
                        case ENUM:
                            String[] enums = ((StringArrayPropertyNode) from).array();
                            ((StringArrayPropertyNode) to).array(enums);
                            return;
                        case FLOAT:
                            float[] floats = ((FloatArrayPropertyNode) from).array();
                            ((FloatArrayPropertyNode) to).array(floats);
                            return;
                        case INTEGER:
                            int[] ints = ((IntegerArrayPropertyNode) from).array();
                            ((IntegerArrayPropertyNode) to).array(ints);
                            return;
                        case STRING:
                            String[] strings = ((StringArrayPropertyNode) from).array();
                            ((StringArrayPropertyNode) to).array(strings);
                            return;
                        case ARRAY:
                            throw new AssertionError("Candle does not support arrays of arrays");
                        default:
                            throw new UnsupportedOperationException("Unsupported type" + ((IArrayPropertyNode) from).itemType());
                    }
                default:
                    throw new UnsupportedOperationException("Unsupported type" + ((IPropertyNode) from).valueType());
            }
        } else if (from instanceof IObjectNode) {
            IObjectNode fromObj = ((IObjectNode) from);
            IObjectNode toObj = ((IObjectNode) to);
            toObj.clear();
            fromObj.forEach(toObj::append);
        }
    }

    private static String getReason(final Exception e) {
        if (e instanceof RecognitionException) {
            RecognitionException recognitionException = (RecognitionException) e;
            Token token = recognitionException.getOffendingToken();
            return "line " + token.getLine() + ':' + token.getCharPositionInLine() + " '" + token.getText() + "'";
        } else if (e.getCause() != null) {
            Throwable t = e;
            do {
                t = t.getCause();
            } while (!(t instanceof Exception));
            return getReason((Exception) t);
        } else {
            return e.getMessage();
        }
    }
}
