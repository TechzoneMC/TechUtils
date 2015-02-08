package net.techcable.techutils.entity;

import java.util.UUID;

import com.google.common.base.Preconditions;

import net.techcable.techutils.TechPlugin;

import lombok.*;

@RequiredArgConstructor
public class TechPlayer {
    private final UUID id;
    @SuppressWarnings("unchecked")
    private final TechPlugin plugin;
    
    /**
     * Retreive this player's uuid
     * 
     * @return this player's uuid
     */
    public UUID getId() {
        return this.id;
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