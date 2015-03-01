package net.techcable.techutils.collect;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ForwardingSet;

public class CaseInsensitveStringSet extends ForwardingSet<String> {
	private final CaseInsensitiveStringMap<Object> backing = new CaseInsensitiveStringMap<>();
	public static final Object VALUE = new Object();
	@Override
	protected Set<String> delegate() {
		return backing.keySet();
	}
	
	@Override
	public boolean add(String element) {
		try {
			backing.put(element, VALUE);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public boolean addAll(Collection<? extends String> collection) {
		return standardAddAll(collection);
	}
}
