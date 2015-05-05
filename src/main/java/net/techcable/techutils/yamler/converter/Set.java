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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import net.techcable.techutils.yamler.InternalConverter;

public class Set implements Converter {
    private InternalConverter internalConverter;

    public Set(InternalConverter internalConverter) {
        this.internalConverter = internalConverter;
    }

    @Override
    public Object toConfig(Class<?> type, Object obj, ParameterizedType genericType) throws Exception {
        java.util.Set<Object> values = (java.util.Set<Object>) obj;
        java.util.List newList = new ArrayList();

        Iterator<Object> iterator = values.iterator();
        while(iterator.hasNext()) {
            Object val = iterator.next();

            Converter converter = internalConverter.getConverter(val.getClass());

            if (converter != null)
                newList.add(converter.toConfig(val.getClass(), val, null));
            else
                newList.add(val);
        }

        return newList;
    }

    @Override
    public Object fromConfig(Class type, Object section, ParameterizedType genericType) throws Exception {
        java.util.List<Object> values = (java.util.List<Object>) section;
        java.util.Set<Object> newList = new HashSet<>();

        try {
            newList = (java.util.Set<Object>) type.newInstance();
        } catch (Exception e) { }

        if (genericType != null && genericType.getActualTypeArguments()[0] instanceof Class) {
            Converter converter = internalConverter.getConverter((Class) genericType.getActualTypeArguments()[0]);

            if (converter != null) {
                for ( int i = 0; i < values.size(); i++ ) {
                    newList.add( converter.fromConfig( ( Class ) genericType.getActualTypeArguments()[0], values.get( i ), null ) );
                }
            } else {
                newList.addAll( values );
            }
        } else {
            newList.addAll( values );
        }

        return newList;
    }

    @Override
    public boolean supports(Class<?> type) {
        return java.util.Set.class.isAssignableFrom(type);
    }

}
