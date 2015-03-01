package net.techcable.techutils.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
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
		if (key instanceof String) key = ((String)key).toLowerCase();
		return delegate().remove(key);
	}
	
	@Override
	public V get(Object key) {
		if (key instanceof String) key = ((String)key).toLowerCase();
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
		if (key instanceof String) key = ((String)key).toLowerCase();
		return delegate().containsKey(key);
	}
}
