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
package net.techcable.techutils.entity.fake;

import lombok.*;

import java.lang.reflect.Field;

import net.techcable.techutils.packet.Packet;
import net.techcable.techutils.packet.PacketPlayOutEntityDestroy;
import net.techcable.techutils.packet.PacketPlayOutEntityMetadata;
import net.techcable.techutils.packet.PacketPlayOutEntityTeleport;
import net.techcable.techutils.packet.PacketPlayOutSpawnEntityLiving;
import net.techcable.techutils.packet.wrappers.WrappedDataWatcher;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static net.techcable.techutils.Reflection.*;

/**
 * Created by Nicholas Schlabach on 4/16/2015.
 */
@RequiredArgsConstructor
public abstract class FakeEntity {

    static {
        //Create fake Entity ID
        Field entityCountField = makeField(getNmsClass("Entity"), "entityCount");
        int entityCount = getField(entityCountField, null);
        fakeEntityId = entityCount++;
        setField(entityCountField, null, entityCount);
    }

    @Getter
    private boolean spawned = false;

    /**
     * Spawn the fake entity at the specified location
     *
     * @param location location to spawn this entity
     */
    public void spawn(Location location) {
        if (isSpawned()) throw new IllegalStateException("Already spawned");
        if (!location.getWorld().equals(player.getWorld())) throw new IllegalArgumentException("Must be in the same world as the player");
        this.location = location;
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(FakeEntity.fakeEntityId, getMobTypeId(), location, velocity, watcher);
        packet.sendTo(player);
        spawned = true;
    }

    public void despawn() {
        if (!isSpawned()) throw new IllegalStateException("Can only despawn a spawned entity");
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(FakeEntity.fakeEntityId);
        packet.sendTo(player);
        spawned = false;
    }

    public void update() {
        if (!isSpawned()) return;
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(FakeEntity.fakeEntityId, watcher);
        packet.sendTo(player);
    }

    public void move(Location location) {//Thread safe with lock
        if (!location.getWorld().equals(player.getWorld())) throw new IllegalArgumentException("Must be in the same world as the player");
        this.location = location;
        boolean onGround = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ()).getType().isSolid(); //Worst case scenario i see an inconsistent state
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(FakeEntity.fakeEntityId, location, onGround);
        packet.sendTo(player);
    }

    protected static final int fakeEntityId; //The entity id of all fake entities
    protected final Player player;
    protected final WrappedDataWatcher watcher = new WrappedDataWatcher();
    private final Vector velocity = new Vector(0, 0, 0);
    private Location location;

    public void setHealth(float health) {
        if (health < 0.1F) { //Don't let it die
            health = 0.1F;
        } else if (health > getMaxHealth()) {
            health = getMaxHealth();
        }
        watcher.setFloat(6, health);
    }

    public void setCustomName(String name) {
        if (Packet.getProtocolVersion(player) > 5) {
            watcher.setString(2, name);
        } else {
            watcher.setString(10, name);
        }
    }

    public void setInvisible(boolean invisible) {
        set0Flag(5, invisible);
    }
    
    public void setShowPotion(boolean showParticles) {
        watcher.setObject(8, showParticles);
    }
    
    public void setPotionEffectColor(int potionColor) {
        watcher.setObject(7, potionColor);
    }

    private void set0Flag(int flagIndex, boolean value) {
        byte b = watcher.hasIndex(0) ? watcher.getByte(0) : 0;

        if (value) {
            watcher.setByte(0, (byte) (b | 1 << flagIndex));
        } else {
            watcher.setByte(0, (byte) (b & ~(1 << flagIndex)));
        }
    }

    public abstract float getMaxHealth();

    public abstract byte getMobTypeId();
    
    public static int mapPotionRGB(byte r, byte g, byte b) {
        // From client source code
        return (int) r << 16 | (int) g << 8 | (int) b;
    }
}
