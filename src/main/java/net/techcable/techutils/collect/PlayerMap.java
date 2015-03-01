package net.techcable.techutils.collect;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import net.techcable.techutils.entity.TechPlayer;
import net.techcable.techutils.entity.UUIDUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.ForwardingMap;

public class PlayerMap<V> extends ForwardingMap<Player, V> {
	private WeakHashMap<Player, V> delegate = new WeakHashMap<Player, V>();

	public V get(UUID id) {
		return get(Bukkit.getPlayer(id));
	}
	
	public V get(String name) {
		return get(UUIDUtils.getPlayerExact(name));
	}
	
	public V get(TechPlayer p) {
		return get(p.getId());
	}
	
	@Override
	protected Map<Player, V> delegate() {
		return delegate;
	}
}