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
import java.util.WeakHashMap;

import net.techcable.techutils.TechPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Controls the plugin's TechPlayers
 */
public class PlayerManager<T extends TechPlayer> implements Listener {

    public PlayerManager(TechPlugin<T> plugin) {
        this.plugin = plugin;
        plugin.registerListener(this);
    }

    @Getter(AccessLevel.PACKAGE)
    private final TechPlugin<T> plugin;

    private final Map<Player, T> players = new WeakHashMap<>();

    public boolean isKnown(UUID id) {
        return isKnown(Bukkit.getPlayer(id));
    }

    public boolean isKnown(Player player) {
        return players.containsKey(player);
    }

    public T getPlayer(UUID id) {
        Player player = Bukkit.getPlayer(id);
        return getPlayer(player);
    }

    public T getPlayer(Player player) {
        if (player == null) return null;
        if (!isKnown(player)) {
            T created = getPlugin().createPlayer(player.getUniqueId());
            players.put(player, created);
        }
        return players.get(player);
    }

    public void onShutdown() {
        for (T player : players.values()) {
            player.destroy();
        }
        players.clear();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final TechPlayer player = getPlayer(event.getPlayer());
        new BukkitRunnable() {

            @Override
            public void run() {
                player.destroy();
            }
        }.runTaskLater(plugin, 1);
    }


    @EventHandler
    public void onQuit(PlayerKickEvent event) {
        final TechPlayer player = getPlayer(event.getPlayer());
        new BukkitRunnable() {

            @Override
            public void run() {
                player.destroy();
            }
        }.runTaskLater(plugin, 1);
    }
}