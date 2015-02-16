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
