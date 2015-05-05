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

import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import net.techcable.techutils.Reflection;
import static net.techcable.techutils.Reflection.*;

public class ProtocolHack {
	private ProtocolHack() {}
	
	public static final Class<?> PACKET_TITLE_ACTION = Reflection.getClass("org.spigotmc.ProtocolInjector$PacketTitle$Action");
	public static final Class<?> PACKET_TITLE = Reflection.getClass("org.spigotmc.ProtocolInjector$PacketTitle");
	
	public static boolean isProtocolHack() {
		return Reflection.getClass("org.spigotmc.ProtocolData") != null;
	}
	
	public static boolean is1_8(Player p) {
		if (!isProtocolHack()) return false;
		return getProtocolVersion(p) >= 47;
	}
	
	public static int getProtocolVersion(Player player) {
        Object handle = getHandle(player);
        Object connection = getField(makeField(handle.getClass(), "playerConnection"), handle);
        Object networkManager = getField(makeField(connection.getClass(), "networkManager"), connection);
        Method getVersion = makeMethod(networkManager.getClass(), "getVersion");
        return callMethod(getVersion, networkManager);
	}
}
