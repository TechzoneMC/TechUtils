package techcable.minecraft.techutils;

import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import techcable.minecraft.uuidfetcher.UUIDFetcher;

public class UUIDUtils {
	private UUIDUtils() {}
	
	public static String getName(UUID uuid) {
		try {
			return UUIDFetcher.getName(uuid);
		} catch (Exception ex) {
			if (TechUtils.isDebug()) {
				TechUtils.getLog().log(Level.WARNING, "Unable to Fetch Name through UUIDFetcher", ex);
			}
			return Bukkit.getOfflinePlayer(uuid).getName();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static UUID getUUID(String name) {
		try {
			return UUIDFetcher.getUUID(name);
		} catch (Exception ex) {
			if (TechUtils.isDebug()) {
				TechUtils.getLog().log(Level.WARNING, "Unable to Fetch uuid through UUIDFetcher", ex);
			}
			return Bukkit.getOfflinePlayer(name).getUniqueId();
		}
	}
}
