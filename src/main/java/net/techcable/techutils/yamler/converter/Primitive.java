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
import java.util.HashSet;
import net.techcable.techutils.yamler.InternalConverter;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Primitive implements Converter {
    private HashSet<String> types = new HashSet<String>() {{
        add("boolean");
        add("char");
        add("byte");
        add("short");
        add("int");
        add("long");
        add("float");
        add("double");
    }};

    private InternalConverter internalConverter;

    public Primitive(InternalConverter internalConverter) {
        this.internalConverter = internalConverter;
    }

    @Override
    public Object toConfig(Class<?> type, Object obj, ParameterizedType parameterizedType) throws Exception {
        return obj;
    }

    @Override
    public Object fromConfig(Class type, Object section, ParameterizedType genericType) throws Exception {
        switch(type.getSimpleName()) {
            case "short":
                return (section instanceof Short) ? section : new Integer((int) section).shortValue();
            case "byte":
                return (section instanceof Byte) ? section : new Integer((int) section).byteValue();
            case "float":
                if ( section instanceof Integer ) {
                    return new Double( (int) section ).floatValue();
                }

                return (section instanceof Float) ? section : new Double((double) section).floatValue();
            case "char":
                return (section instanceof Character) ? section : ((String) section).charAt(0);
            default:
                return section;
        }
    }

    @Override
    public boolean supports(Class<?> type) {
        return types.contains(type.getName());
    }
}
