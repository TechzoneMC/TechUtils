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
package net.techcable.techutils.entity;

import lombok.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.techcable.techutils.TechPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Controls the plugin's TechPlayers
 */
public class PlayerManager<T extends TechPlayer> implements Listener {

    public PlayerManager(TechPlugin<T> plugin) {
        plugin.registerListener(this);
        this.plugin = plugin;
    }

    @Getter(AccessLevel.PACKAGE)
    private final TechPlugin<T> plugin;

    private final Map<UUID, T> players = new ConcurrentHashMap<>();

    public boolean isKnown(UUID id) {
        return players.containsKey(id);
    }

    public boolean isKnown(Player player) {
        return isKnown(player.getUniqueId());
    }

    public T getPlayer(UUID id) {
        if (Bukkit.getPlayer(id) == null) return null;
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
        players.remove(playerEntity.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent event) {
        onQuit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        onQuit(event.getPlayer());
    }
}