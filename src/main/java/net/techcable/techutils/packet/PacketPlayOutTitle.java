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

import java.lang.reflect.Constructor;

import static net.techcable.techutils.Reflection.*;
import net.techcable.techutils.Reflection;
import net.techcable.techutils.packet.spigot.PHPacketPlayOutTitle;
import net.techcable.techutils.packet.spigot.ProtocolHack;

public class PacketPlayOutTitle extends Packet {

	protected PacketPlayOutTitle() {}
	
	private PacketPlayOutTitle(TitleAction action) {
		this(action, null);
	}

	public PacketPlayOutTitle(TitleAction action, String rawChat) {
		Object[] chatArray = Converters.getIChatBaseComponentConverter().toNms(rawChat);
		Object chat = null;
		if (chatArray != null && chatArray.length > 0) chat = chatArray[0];
		Constructor<?> chatConstructor = makeConstructor(getPacketClass(), TitleAction.getNmsClass(), Reflection.getNmsClass("IChatBaseComponent"));
		Object packet = callConstructor(chatConstructor, action.asNms(), chat);
		setHandle(packet);
	}
	
	public PacketPlayOutTitle(TitleAction action, int fadeIn, int stay, int fadeOut) {
		Constructor<?> constructor = makeConstructor(getPacketClass(), int.class, int.class, int.class);
		Object packet = callConstructor(constructor, fadeIn, stay, fadeOut);
		setHandle(packet);
	}
	
	public static PacketPlayOutTitle create(TitleAction action) {
		if (ProtocolHack.isProtocolHack()) {
			return new PHPacketPlayOutTitle(action);
		} else if (getNmsClass("PacketPlayOutTitle") != null) {
			return new PacketPlayOutTitle(action);
		} else return null;
	}
	
	public static PacketPlayOutTitle create(TitleAction action, String chat) {
		if (ProtocolHack.isProtocolHack()) {
			return new PHPacketPlayOutTitle(action, chat);
		} else if (getNmsClass("PacketPlayOutTitle") != null) {
			return new PacketPlayOutTitle(action, chat);
		} else return null;
	}
	
	public static PacketPlayOutTitle create(TitleAction action, int fadeIn, int stay, int fadeOut) {
		if (ProtocolHack.isProtocolHack()) {
			return new PHPacketPlayOutTitle(action);
		} else if (getNmsClass("PacketPlayOutTitle") != null) {
			return new PacketPlayOutTitle(action, fadeIn, stay, fadeOut);
		} else return null;
	}
	
	public static boolean isSupported() {
		return ProtocolHack.isProtocolHack() || getNmsClass("PacketPlayOutTitle") != null;
	}
	
	public static enum TitleAction {
		SET_TITLE, SET_SUBTITLE, DISPLAY, HIDE, RESET;
		
		public Object asNms() {
			return getNmsClass().getEnumConstants()[this.ordinal()];
		}
		
		public static Class<?> getNmsClass() {
			return ProtocolHack.isProtocolHack() ? ProtocolHack.PACKET_TITLE_ACTION : Reflection.getNmsClass("EnumTitleAction");
		}
	}

	@Override
	public Class<?> getPacketClass() {
		return getNmsClass("PacketPlayOutTitle");
	}
}