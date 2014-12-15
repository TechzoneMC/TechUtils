package techcable.minecraft.techutils.entity;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import techcable.minecraft.offlineplayers.AdvancedOfflinePlayer;
import techcable.minecraft.techutils.EasyCache;
import techcable.minecraft.techutils.TechUtils;
import techcable.minecraft.techutils.UUIDUtils;
import techcable.minecraft.techutils.VelocityUtils;

import lombok.*;

@Getter
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public class TechPlayer {
	private final UUID uuid;
	private AdvancedOfflinePlayer advancedOfflinePlayer;
	@Getter(AccessLevel.NONE)
	private boolean advancedOfflinePlayerOnline;
	
	public String getName() {
		return UUIDUtils.getName(getUuid());
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(getUuid());
	}
	
	public AdvancedOfflinePlayer getAdvancedOfflinePlayer() {
		if (advancedOfflinePlayer == null || advancedOfflinePlayerOnline != isOnline()) {
			advancedOfflinePlayer = TechUtils.getAdvancedOfflinePlayer(getOfflinePlayer());
			advancedOfflinePlayerOnline = isOnline();
		}
		return advancedOfflinePlayer;
	}
	
	public Player getPlayer() {
		if (!isOnline()) throw new RuntimeException("not online");
		return Bukkit.getPlayer(getUuid());
	}
	
	public boolean isOnline() {
		return getOfflinePlayer().isOnline();
	}
	
	public void knockback(double power) {
		if (!isOnline()) return;
		Player player = getPlayer();
		player.setVelocity(VelocityUtils.knockback(getPlayer().getVelocity(), power));
	}
	
	private static EasyCache<UUID, TechPlayer> techPlayerCache = new EasyCache<>(new EasyCache.Loader<UUID, TechPlayer>() {

		@Override
		public TechPlayer load(UUID key) {
			return new TechPlayer(key);
		}
	
	});
	public static TechPlayer getTechPlayer(UUID id) {
		return techPlayerCache.get(id);
	}
}
