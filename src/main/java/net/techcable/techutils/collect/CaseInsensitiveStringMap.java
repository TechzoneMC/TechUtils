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
package net.techcable.techutils.collect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ForwardingMap;

public class CaseInsensitiveStringMap<V> extends ForwardingMap<String, V> {

    private final ConcurrentHashMap<String, V> delegate = new ConcurrentHashMap<>();

    @Override
    protected Map<String, V> delegate() {
        return delegate;
    }

    @Override
    public V remove(Object key) {
        if (key instanceof String) key = ((String) key).toLowerCase();
        return delegate().remove(key);
    }

    @Override
    public V get(Object key) {
        if (key instanceof String) key = ((String) key).toLowerCase();
        return delegate().get(key);
    }

    @Override
    public V put(String key, V value) {
        key = key.toLowerCase();
        return delegate().put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> map) {
        standardPutAll(map);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String) key = ((String) key).toLowerCase();
        return delegate().containsKey(key);
    }
}
