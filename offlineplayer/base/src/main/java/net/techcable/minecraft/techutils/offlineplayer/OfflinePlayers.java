package net.techcable.minecraft.techutils.offlineplayer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.common.base.Throwables;

public class OfflinePlayers {
	
	public static boolean isSupported() {
		try {
			getNMS();
			return true;
		} catch (UnsupportedOperationException ex) {
			return false;
		}
	}
	
	public static PlayerData getData(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if (player == null) {
			player = loadPlayer(uuid);
		}
		return new PlayerPlayerData(player);
	}
	
	public static PlayerData getData(OfflinePlayer player) {
		if (player instanceof Player) {
			return new PlayerPlayerData((Player)player);
		} else {
			return getData(player.getUniqueId());
		}
	}
	
	protected static Player loadPlayer(UUID id) {
		if (!isSupported()) throw new UnsupportedOperationException("Not supported");
		Player offline = getNMS().loadFromFile(id);
		offline.loadData();
		return offline;
	}
	
	private static NMS nms;
    private static NMS getNMS() {
    	if (nms == null) {
    		try {
        		String version = getVersion();
        		Class<?> rawClass = Class.forName("net.techcable.minecraft.techutils.offlineplayer.nms." + version + ".NMS");
        		Class<? extends NMS> nmsClass = rawClass.asSubclass(NMS.class);
        		Constructor<? extends NMS> constructor = nmsClass.getConstructor();
        		return constructor.newInstance();
        	} catch (ClassNotFoundException ex) {
        		throw new UnsupportedOperationException("Unsupported nms version", ex);
        	} catch (InvocationTargetException ex) {
        		throw Throwables.propagate(ex.getTargetException());
        	} catch (Exception ex) {
        		throw Throwables.propagate(ex);
        	}
    	}
    	return nms;
    }
    
    private static String getVersion() {
    	String packageName = Bukkit.getServer().getClass().getPackage().getName();
    	return packageName.substring(packageName.lastIndexOf(".") + 1);
    }
}
