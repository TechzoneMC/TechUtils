package techcable.minecraft.techutils.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.*;

public abstract class EasyCache<K, V> {
    public static <K, V> EasyCache<K, V> makeCache(Loader<K, V> loader) {
    	try {
    		return new Guava17EasyCache<K, V>(loader);
    	} catch (Exception ex) { //Guava wrong version
    		return new AlwaysLoadEasyCache<K, V>(loader);
    	}
    }

	public abstract V get(K key);
	

    public static interface Loader<K, V> {
        public V load(K key);
    }
}