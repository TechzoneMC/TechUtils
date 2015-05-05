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
package net.techcable.techutils.yamler.converter;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import net.techcable.techutils.yamler.ConfigSection;
import net.techcable.techutils.yamler.InternalConverter;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Config implements Converter {
    private InternalConverter internalConverter;

    public Config(InternalConverter internalConverter) {
        this.internalConverter = internalConverter;
    }

    @Override
    public Object toConfig(Class<?> type, Object obj, ParameterizedType parameterizedType) throws Exception {
        return (obj instanceof Map) ? obj : ((net.techcable.techutils.yamler.Config) obj).saveToMap( obj.getClass() );
    }

    @Override
    public Object fromConfig(Class type, Object section, ParameterizedType genericType) throws Exception {
        net.techcable.techutils.yamler.Config obj = (net.techcable.techutils.yamler.Config) newInstance(type);

        // Inject Converter stack into subconfig
        for (Class aClass : internalConverter.getCustomConverters()) {
            obj.addConverter(aClass);
        }

        obj.loadFromMap((section instanceof Map) ? (Map) section : ((ConfigSection) section).getRawMap(), type);
        return obj;
    }
    
    // recursively handles enclosed classes
    public Object newInstance(Class type) throws Exception {
        Class enclosingClass = type.getEnclosingClass();
        if (enclosingClass != null) {
            Object instanceOfEnclosingClass = newInstance(enclosingClass);
            return type.getConstructor(enclosingClass).newInstance(instanceOfEnclosingClass);
        } else {
            return type.newInstance();
        }
    }

    @Override
    public boolean supports(Class<?> type) {
        return net.techcable.techutils.yamler.Config.class.isAssignableFrom(type);
    }
}
