package net.techcable.techutils;

import java.util.UUID;

import net.techcable.techutils.entity.TechPlayer;
import net.techcable.techutils.entity.PlayerManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A plugin using techutils
 * 
 */
public abstract class TechPlugin<T extends TechPlayer> extends JavaPlugin {
    
    private PlayerManager<T> playerManager;
    
    /**
     * Startup techutils
     * 
     * Put your plugin's startup code in the startup method
     * 
     */
    public final void onEnable() {
        this.playerManager = new PlayerManager<>(this);
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
}
