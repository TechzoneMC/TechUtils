package net.techcable.techutils.entity;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import net.techcable.techutils.TechPlugin;
import lombok.*;

@RequiredArgsConstructor
public class TechPlayer {
    private final UUID id;
    @SuppressWarnings("unchecked")
    private final TechPlugin<?> plugin;
    
    /**
     * Retreive the plugin that owns this player
     * 
     * @return this player's plugin
     */
    public TechPlugin<?> getPlugin() {
    	return this.plugin;
    }
    
    /**
     * Retreive this player's uuid
     * 
     * @return this player's uuid
     */
    public UUID getId() {
        return this.id;
    }
    
    /**
     * Retrieve the entity backing this techplayer
     * 
     * @return the accociated player entity
     */
    public Player getEntity() {
    	return Bukkit.getPlayer(getId());
    }
    
    /**
     * Retreive this player's username
     * 
     * @return this player's username 
     */
    public String getName() {
        String name = UUIDUtils.getName(getId());
        assert name != null : "Unable to lookup name";
        return name;
    }
    
    final void destroy() {
        cleanup();
    }
    
    /**
     * Perform any necessary cleanup actions
     * 
     */
    protected void cleanup() {}
    
    public final boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other instanceof TechPlayer) {
            UUID otherId = ((TechPlayer)other).getId();
            return getId().equals(otherId);
        }
        return false;
    }
    
    public final int hashCode() {
        return getId().hashCode();
    }
}