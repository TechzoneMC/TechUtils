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

import java.lang.annotation.Annotation;

import net.techcable.techutils.config.ConfigSerializer;

import org.bukkit.configuration.InvalidConfigurationException;

public class FloatSerializer implements ConfigSerializer<Float> {

    @Override
    public Object serialize(Float aFloat, Annotation[] annotations) {
        return aFloat;
    }

    @Override
    public Float deserialize(Object yaml, Class<? extends Float> type, Annotation[] annotations) throws InvalidConfigurationException {
        return (Float) yaml;
    }

    @Override
    public boolean canDeserialize(Class<?> type) {
        return type == float.class || type == Float.class;
    }

    @Override
    public boolean canSerialize(Class<?> type) {
        return canDeserialize(type);
    }
}
