package techcable.minecraft.techutils;

import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.turt2live.uuid.CachingServiceProvider;
import com.turt2live.uuid.PlayerRecord;
import com.turt2live.uuid.ServiceProvider;
import com.turt2live.uuid.turt2live.v2.ApiV2Service;

public class UUIDUtils {
	static {
		UUID_PROVIDER = new CachingServiceProvider(new ApiV2Service());
	}
	private UUIDUtils() {}
	private static final ServiceProvider UUID_PROVIDER;
	public static String getName(UUID uuid) {
		PlayerRecord player = UUID_PROVIDER.doLookup(uuid);
		if (player == null) {
			return Bukkit.getOfflinePlayer(uuid).getName();
		}
		return player.getName();
	}
	
	@SuppressWarnings("deprecation")
	public static UUID getUUID(String name) {
		PlayerRecord player = UUID_PROVIDER.doLookup(name);
		if (player == null) {
			return Bukkit.getOfflinePlayer(name).getUniqueId();
		}
		if (Bukkit.getOnlineMode()) {
			return player.getUuid();
		} else {
			return player.getOfflineUuid();
		}
	}
}
