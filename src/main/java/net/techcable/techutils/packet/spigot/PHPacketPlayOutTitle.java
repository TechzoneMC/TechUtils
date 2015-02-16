package net.techcable.techutils.packet.spigot;

import static net.techcable.techutils.Reflection.*;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;

import net.techcable.techutils.Reflection;
import net.techcable.techutils.packet.PacketPlayOutTitle;

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
