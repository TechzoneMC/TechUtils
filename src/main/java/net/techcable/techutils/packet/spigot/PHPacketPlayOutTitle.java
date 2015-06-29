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
package net.techcable.techutils.packet.spigot;

import java.lang.reflect.Constructor;

import net.techcable.techutils.Reflection;
import net.techcable.techutils.packet.PacketPlayOutTitle;

import org.bukkit.entity.Player;

import static net.techcable.techutils.Reflection.callConstructor;
import static net.techcable.techutils.Reflection.makeConstructor;

public class PHPacketPlayOutTitle extends PacketPlayOutTitle {

    public PHPacketPlayOutTitle(TitleAction action) {
        Constructor<?> constructor = makeConstructor(getPacketClass(), TitleAction.getNmsClass());
        Object packet = callConstructor(constructor, action.asNms());
        setHandle(packet);
    }

    public PHPacketPlayOutTitle(TitleAction action, String rawChat) {
        super(action, rawChat);
    }

    public PHPacketPlayOutTitle(TitleAction action, int fadeIn, int stay, int fadeOut) {
        Constructor<?> constructor = makeConstructor(getPacketClass(), TitleAction.getNmsClass(), int.class, int.class, int.class);
        Object packet = callConstructor(constructor, action.asNms(), fadeIn, stay, fadeOut);
        setHandle(packet);
    }

    @Override
    public void sendTo(Player p) {
        if (!ProtocolHack.is1_8(p)) return;
        super.sendTo(p);
    }

    @Override
    public Class<?> getPacketClass() {
        return Reflection.getClass("org.spigotmc.ProtocolInjector$PacketTitle");
    }
}
