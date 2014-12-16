package techcable.minecraft.techutils;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;

import techcable.minecraft.techutils.entity.TechPlayer;
import techcable.minecraft.techutils.offlineplayers.AdvancedOfflinePlayer;
import techcable.minecraft.techutils.offlineplayers.NBTAdvancedOfflinePlayer.PlayerNotFoundException;
import techcable.minecraft.techutils.offlineplayers.OfflinePlayers;

import lombok.Getter;

public class TechUtils {
	@Getter
	private static boolean debug;
	@Getter
	private static Logger log = Logger.getLogger("TechUtils");
	private TechUtils() {}

	public static AdvancedOfflinePlayer getAdvancedOfflinePlayer(OfflinePlayer player) {
		try {
			return OfflinePlayers.getAdvancedOfflinePlayer(player);
		} catch (PlayerNotFoundException ex) {
			return null;
		} 
	}
	
	public static void setDebug(boolean debug) {
		TechUtils.debug = debug;
	}
	
	
	public static TechPlayer getTechPlayer(OfflinePlayer player) {
		return getTechPlayer(player.getUniqueId());
	}
	public static TechPlayer getTechPlayer(UUID id) {
		return TechPlayer.getTechPlayer(id);
	}
}
