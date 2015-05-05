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

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public interface Converter {
    /**
     * This method gets called on save. It gets the Fields Type and the object the Config wants to save into it. This
     * is needed to pretty print INTO the config.
     *
     * @param type The type (Class) of the Field
     * @param obj The object which is stored in the Config Object
     * @param parameterizedType If the Class has some generic Informations this is the Information otherwise this is null
     * @return An Object (mostly a Map or a List)
     * @throws Exception Some generic exception when something went wrong. This gets caught by the Converter
     */
    public Object toConfig(Class<?> type, Object obj, ParameterizedType parameterizedType) throws Exception;

    /**
     * This method gets called when we want to load something out of the File. You get that what you give into the Config
     * via toConfig as Object passed. The type is the Destination Field Type which this Object should be layed in.
     *
     * @param type The type (Class) of the Field
     * @param obj The Object from toConfig
     * @param parameterizedType If the Class has some generic Informations this is the Information otherwise this is null
     * @return The correct Object which can be hold by the Field
     * @throws Exception Some generic exception when something went wrong. This gets caught by the Converter
     */
    public Object fromConfig(Class<?> type, Object obj, ParameterizedType parameterizedType) throws Exception;

    /**
     * This checks if this Converter can convert the given Class
     *
     * @param type The type (Class) of the Field to check
     * @return true if this can convert that otherwise false
     */
    public boolean supports(Class<?> type);
}
