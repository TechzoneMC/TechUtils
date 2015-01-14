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
package techcable.minecraft.techutils.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.*;

@Getter
public class Guava17EasyCache<K, V> extends EasyCache<K, V> {
	private final LoadingCache<K, V> backing;
    public Guava17EasyCache(Loader<K, V> loader) {
        backing = CacheBuilder.newBuilder().weakKeys().weakValues().build(new LoaderCacheLoader<K, V>(loader));
    }
    
    @Override
    public V get(K key) {
        return backing.getUnchecked(key);
    }
    
    @Getter
    private static class LoaderCacheLoader<K, V> extends CacheLoader<K, V> {
        
        private LoaderCacheLoader(Loader<K, V> backing) {
            this.backing = backing;
        }
        private final Loader<K, V> backing;
        
        @Override
        public V load(K key) {
            return backing.load(key);
        }
    }
}
