package net.techcable.techutils.packet;

import java.lang.reflect.Method;

import net.techcable.techutils.Reflection;
import static net.techcable.techutils.Reflection.*;

import org.bukkit.entity.Player;





import lombok.*;

@Getter
@Setter
public abstract class Packet {
	private Object handle;
	
	public void sendTo(Player p) {
		if (!isCompatible(p));
		Object handle = Reflection.getHandle(p);
		Object playerConnection = getField(makeField(handle.getClass(), "playerConnection"), handle);
		Method sendPacket = makeMethod(getNmsClass("PlayerConnection"), "sendPacket", getNmsClass("Packet"));
		callMethod(sendPacket, playerConnection, getHandle());
	}
	
	public abstract Class<?> getPacketClass();
	
	public boolean isCompatible(Player p) {
		return true;
	}
}