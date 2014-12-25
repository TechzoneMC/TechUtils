package techcable.minecraft.techutils;

import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

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
			return null;
		}
		return player.getName();
	}
	
	public static UUID getUUID(String name) {
		PlayerRecord player = UUID_PROVIDER.doLookup(name);
		if (player == null) {
			return null;
		}
		return player.getUuid();
	}
}
