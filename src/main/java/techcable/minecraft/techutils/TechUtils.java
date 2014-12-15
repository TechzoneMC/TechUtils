package techcable.minecraft.techutils;

import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;

import techcable.minecraft.offlineplayers.AdvancedOfflinePlayer;
import techcable.minecraft.offlineplayers.NBTAdvancedOfflinePlayer.PlayerNotFoundException;
import techcable.minecraft.offlineplayers.OfflinePlayers;

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
		OfflinePlayers.setDebug(debug);
	}
}
