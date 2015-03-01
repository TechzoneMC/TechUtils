package net.techcable.techutils.entity;

import java.util.UUID;

import com.google.common.base.Charsets;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.techcable.techutils.entity.ProfileUtils.PlayerProfile;

public class UUIDUtils {
    private UUIDUtils() {}
    
    /**
     * Retreive a player's UUID based on it's name
     * 
     * Returns null if lookup failed
     * 
     * @param name the player's name
     * @return the player's uuid, or null if failed
     */
    public static UUID getId(String name) {
    	if (ProfileUtils.getIfCached(name) != null) return ProfileUtils.getIfCached(name).getId(); //Previously cached by UUIDUtils.getPlayerExact()
    	if (getPlayerExact(name) != null) {
    		return getPlayerExact(name).getUniqueId();
    	}
        if (Bukkit.getOnlineMode()) {
            PlayerProfile profile = ProfileUtils.lookup(name);
            if (profile == null) return null;
            return profile.getId();
        } else {
            return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
        }
    }
    
    /**
     * Retreive a player's name based on it's uuid
     * 
     * Returns null if lookup failed
     * 
     * @param name the player's uuid
     * @return the player's name, or null if failed
     */
    public static String getName(UUID id) {
        if (ProfileUtils.getIfCached(id) != null) return ProfileUtils.getIfCached(id).getName();
    	if (Bukkit.getPlayer(id) != null) {
            String name = Bukkit.getPlayer(id).getName();
            ProfileUtils.addToCache(id, name); //Saves us a potential lookup by staying in the cache after player leaves
            return name;
        }
        if (Bukkit.getOnlineMode()) {
            PlayerProfile profile = ProfileUtils.lookup(id);
            if (profile == null) return null;
            return profile.getName();
        } else {
            OfflinePlayer player = Bukkit.getOfflinePlayer(id);
            return player.getName();
        }
    }
    
    /**
     * A faster version of Bukkit.getPlayerExact()
     * <p>
     * Bukkit.getPlayerExact() iterates through all online players <br>
     * This caches results from Bukkit.getPlayerExact() to speed up lookups
     * 
     * @see Bukkit#getPlayerExact(String)
     * 
     * @param name get player with this name
     * @return player with specified name
     */
     public static Player getPlayerExact(String name) {
     	if (ProfileUtils.getIfCached(name) != null) return Bukkit.getPlayer(ProfileUtils.getIfCached(name).getId());
     	if (Bukkit.getPlayerExact(name) != null) {
            UUID id = Bukkit.getPlayerExact(name).getUniqueId();
            /*
             * Calling Bukkit.getPlayerExact() iterates through all online players, making it far slower than hashmap retreival
             * This has the added benefit of remaining in the cache even after the player leaves; potentially saving a mojang lookup for uuid fetching
             */
            ProfileUtils.addToCache(id, name); 
            return Bukkit.getPlayer(id);
        }
     	return null;
     }
}