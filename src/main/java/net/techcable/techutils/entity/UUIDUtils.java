package net.techcable.techutils.entity;

import java.util.UUID;

import com.google.common.base.Charsets;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

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
        if (Bukkit.getPlayer(name) != null) {
            return Bukkit.getPlayer(name).getUniqueId();
        }
        if (Bukkit.getOnlineMode()) {
            PlayerProfile profile = ProfileUtils.lookup(name);
            if (profile == null) return null;
            return profile.getId();
        } else {
            return UUID.nameUUIDFromBytes(("OfflinePlayer:" + getName()).getBytes(Charsets.UTF_8));
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
        if (Bukkit.getPlayer(id) != null) {
            return Bukkit.getPlayer(name).getName();
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
    
}