package techcable.minecraft.techutils.offlineplayers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import techcable.minecraft.techutils.TechUtils;
import techcable.minecraft.techutils.offlineplayers.NBTAdvancedOfflinePlayer.PlayerNotFoundException;
import techcable.minecraft.techutils.offlineplayers.wrappers.OnlineAdvancedOfflinePlayer;

import lombok.*;

@Getter
public class OfflinePlayers {
	private OfflinePlayers() {}
	public static AdvancedOfflinePlayer getAdvancedOfflinePlayer(OfflinePlayer player) throws PlayerNotFoundException {
		if (player.isOnline()) {
			return new OnlineAdvancedOfflinePlayer(player.getPlayer());
		} else {
			return new NBTAdvancedOfflinePlayer(player);
		}
	}
	
	public static boolean isDebug() {
		return TechUtils.isDebug();
	}
	
	public static void info(String info) {
		Bukkit.getLogger().info("[OfflinePlayers] " + info);
	}
}
