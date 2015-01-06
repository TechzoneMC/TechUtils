package techcable.minecraft.techutils;

import java.util.UUID;
import java.util.logging.Logger;

import net.techcable.minecraft.techutils.offlineplayer.OfflinePlayers;
import net.techcable.minecraft.techutils.offlineplayer.PlayerData;

import org.bukkit.OfflinePlayer;

import techcable.minecraft.techutils.entity.TechPlayer;
import lombok.Getter;

public class TechUtils {
	@Getter
	private static boolean debug;
	@Getter
	private static Logger log = Logger.getLogger("TechUtils");
	private TechUtils() {}

	public static PlayerData getPlayerData(UUID player) {
		return OfflinePlayers.getData(player);
	}
	
	public static PlayerData getPlayerData(OfflinePlayer player) {
		return getPlayerData(player.getUniqueId());
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
