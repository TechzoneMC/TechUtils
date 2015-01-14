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
