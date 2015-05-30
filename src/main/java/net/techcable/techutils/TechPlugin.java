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
package net.techcable.techutils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.techcable.techutils.entity.TechPlayer;
import net.techcable.techutils.entity.PlayerManager;

import net.techcable.techutils.scoreboard.GlobalScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A plugin using TechUtils
 * 
 */
public abstract class TechPlugin<T extends TechPlayer> extends JavaPlugin {
    
    private PlayerManager<T> playerManager;
    
    private MetricsLite metrics;

    private GlobalScoreboard scoreboard;

    /**
     * Gets the global scoreboard
     * <p>
     * A global scoreboard is shared across all players
     * It overides any player specific scoreboard
     * </p>
     *
     * @return the global scoreboard
     */
    public GlobalScoreboard getScoreboard() {
        if (scoreboard == null) scoreboard = new GlobalScoreboard(this);
        return scoreboard;
    }

    /**
     * Startup techutils
     * 
     * Put your plugin's startup code in the startup method
     * 
     */
    public final void onEnable() {
        this.playerManager = new PlayerManager<>(this);
        try {
            if (metrics == null) {
                metrics = new MetricsLite(this);
            }
            metrics.start();
        } catch (IOException e) {
            warning("Unable to send metrics for TechUtils");
        }
        startup();
    }
    
    /**
     * Cleanup techutils
     * 
     * Put your plugin's cleanup code in the shutdown method
     * 
     */
    public final void onDisable() {
        shutdown();
        playerManager.onShutdown();
    }
    
    /**
     * Get the TechPlayer with the given uuid
     * 
     * @return the player with the given uuid
     */
    public T getPlayer(UUID id) {
        return playerManager.getPlayer(id);
    }
    
    public T getPlayer(Player id) {
        return playerManager.getPlayer(id);
    }
    
    /**
     * Create a player with the given uuid
     * 
     * Plugins adding custom players must overide this method
     * 
     * @throws ClassCastException if you add custom players and don't overide this method
     */
    @SuppressWarnings("unchecked")
	public T createPlayer(UUID id) {
        assert !playerManager.isKnown(id) : "This player has already been created!";
        return (T) new TechPlayer(id, this);
    }
    
    /**
     * Startup this plugin
     * 
     */
    protected abstract void startup();
    
    /**
     * Shutdown this plugin
     */
    protected abstract void shutdown();
    
    /**
     * Regestiers the specified listener
     * 
     * @param listener the listener to register
     */
    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
    
    //Logging
    
    public void severe(String format, Object... args) {
        Bukkit.getLogger().severe(getPrefix() + String.format(format, args));
    }
    
    public void warning(String format, Object... args) {
        Bukkit.getLogger().warning(getPrefix() + String.format(format, args));
    }
    
    public void info(String format, Object... args) {
        Bukkit.getLogger().info(getPrefix() + String.format(format, args));
    }
    
    private String getPrefix() {
        return "[" + getName() + "] ";
    }

    public Collection<T> getOnlinePlayers() {
        Set<T> players = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(getPlayer(player));
        }
        return players;
    }
}
