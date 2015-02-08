package net.techcable.techutils.entity;

import lombok.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.techcable.techutils.TechPlugin;

/**
 * Controls the plugin's TechPlayers
 * 
 */
public class PlayerManager<T extends TechPlayer> implements Listener {
    public PlayerManager(TechPlugin<T> plugin) {
        plugin.registerListener(this);
        this.plugin = plugin;
    }
    
    @Getter(AccessLevel.PACKAGE)
    private final TechPlugin<T> plugin;
    
    private final Map<UUID, T> players = new HashMap<>();
    
    public boolean isKnown(UUID id) {
        return players.containsKey(id);
    }
    
    public boolean isKnown(Player player) {
        return isKnown(player.getUniqueId());
    }
    
    public T getPlayer(UUID id) {
        if (!isKnown(id)) {
            T created = getPlugin().createPlayer(id);
            players.put(id, created);
        }
        return players.get(id);
    }
    
    public T getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }
    
    public void onShutdown() {
        for (T player : players.values()) {
            player.destroy();
        }
        players.clear();
    }
    
    public void onQuit(Player playerEntity) {
        if (!isKnown(playerEntity)) return;
        T player = getPlayer(playerEntity);
        player.destroy();
        knownPlayer.remove(playerEntity.getUniqueId());
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onKick(PlayerKickEvent event) {
        onQuit(event.getPlayer());
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onQuit(PlayerLeaveEvent event) {
        onQuit(event.getPlayer());
    }
}