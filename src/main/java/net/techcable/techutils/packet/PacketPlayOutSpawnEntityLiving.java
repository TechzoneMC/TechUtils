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
package net.techcable.techutils.packet;

import lombok.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import net.techcable.techutils.packet.wrappers.WrappedDataWatcher;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import static net.techcable.techutils.Reflection.*;

public class PacketPlayOutSpawnEntityLiving extends Packet {

    public static final Class<?> PACKET_CLASS = getNmsClass("PacketPlayOutSpawnEntityLiving");

    static {
        Field[] fields = PACKET_CLASS.getDeclaredFields();
        entityIdField = fields[0];
        entityTypeField = fields[1];
        entityXField = fields[2];
        entityYField = fields[3];
        entityZField = fields[4];
        entityVelocityXField = fields[5];
        entityVelocityYField = fields[6];
        entityVelocityZField = fields[7];
        entityYawField = fields[8];
        entityPitchField = fields[9];
        entityHeadPitchField = fields[10];
        entityDataWatcherField = fields[11];
    }

    private static Field entityIdField;
    private static Field entityTypeField;
    private static Field entityXField;
    private static Field entityYField;
    private static Field entityZField;
    private static Field entityVelocityXField;
    private static Field entityVelocityYField;
    private static Field entityVelocityZField;
    private static Field entityYawField;
    private static Field entityPitchField;
    private static Field entityHeadPitchField;
    private static Field entityDataWatcherField;
    private static final Constructor constructor = makeConstructor(PACKET_CLASS);

    @Getter
    private final Object handle;

    public PacketPlayOutSpawnEntityLiving(int entityId, byte entityType, Location location, Vector bukkitVelocity, WrappedDataWatcher watcher) {
        this.handle = callConstructor(constructor);
        setField(entityIdField, handle, entityId);
        setField(entityTypeField, handle, (int) entityType);
        setField(entityXField, handle, toFixedPoint(location.getX()));
        setField(entityYField, handle, toFixedPoint(location.getY()));
        setField(entityZField, handle, toFixedPoint(location.getZ()));
        int[] velocity = doVelocityMagic(bukkitVelocity.getX(), bukkitVelocity.getY(), bukkitVelocity.getZ());
        setField(entityVelocityXField, handle, velocity[0]);
        setField(entityVelocityYField, handle, velocity[1]);
        setField(entityVelocityZField, handle, velocity[2]);
        setField(entityYawField, handle, toByteAngle(location.getYaw()));
        setField(entityPitchField, handle, toByteAngle(location.getPitch()));
        setField(entityHeadPitchField, handle, toByteAngle(location.getPitch())); //Meh
        setField(entityDataWatcherField, handle, watcher.getHandle());
    }

    @Override
    public Class<?> getPacketClass() {
        return PACKET_CLASS;
    }
}
