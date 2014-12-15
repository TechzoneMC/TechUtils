package techcable.minecraft.techutils.entity;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import techcable.minecraft.offlineplayers.AdvancedOfflinePlayer;
import techcable.minecraft.techutils.TechUtils;
import techcable.minecraft.techutils.UUIDUtils;
import techcable.minecraft.techutils.VelocityUtils;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class TechPlayer {
	private final UUID uuid;
	
	public String getName() {
		return UUIDUtils.getName(getUuid());
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(getUuid());
	}
	
	public AdvancedOfflinePlayer getAdvancedOfflinePlayer() {
		return TechUtils.getAdvancedOfflinePlayer(getOfflinePlayer());
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
}
