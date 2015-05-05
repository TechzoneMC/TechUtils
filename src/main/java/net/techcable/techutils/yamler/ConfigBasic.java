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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class ConfigBasic {
    protected transient File CONFIG_FILE = null;
    protected transient String[] CONFIG_HEADER = null;
    protected transient ConfigMode CONFIG_MODE = ConfigMode.DEFAULT;
    protected transient boolean skipFailedObjects = false;

    protected transient InternalConverter converter = new InternalConverter();

    /**
     * This function gets called after the File has been loaded and before the Converter gets it.
     * This is used to manually edit the configSection when you updated the config or something
     * @param configSection The root ConfigSection with all Subnodes loaded into
     */
    public void update(ConfigSection configSection) {

    }

    /**
     * Add a Custom Converter. A Converter can take Objects and return a pretty Object which gets saved/loaded from
     * the Converter. How a Converter must be build can be looked up in the Converter Interface.
     *
     * @param addConverter Converter to be added
     * @throws InvalidConverterException If the Converter has any errors this Exception tells you what
     */
    public void addConverter(Class addConverter) throws InvalidConverterException {
        converter.addCustomConverter(addConverter);
    }

    protected boolean doSkip(Field field) {
        return Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())
                || Modifier.isFinal(field.getModifiers());
    }
}
