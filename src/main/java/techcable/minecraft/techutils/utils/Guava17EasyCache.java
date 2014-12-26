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
