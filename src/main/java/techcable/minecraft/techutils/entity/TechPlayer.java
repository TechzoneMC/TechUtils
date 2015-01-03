package techcable.minecraft.techutils.entity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.base.Preconditions;

import techcable.minecraft.techutils.InventoryUtils;
import techcable.minecraft.techutils.TechUtils;
import techcable.minecraft.techutils.UUIDUtils;
import techcable.minecraft.techutils.VelocityUtils;
import techcable.minecraft.techutils.offlineplayers.AdvancedOfflinePlayer;
import techcable.minecraft.techutils.scoreboard.ScoreboardProvider;
import techcable.minecraft.techutils.scoreboard.TechScoreboard;
import techcable.minecraft.techutils.utils.EasyCache;

import lombok.*;

@Getter
@RequiredArgsConstructor(access=AccessLevel.PROTECTED)
public class TechPlayer {
	private final UUID uuid;
	private AdvancedOfflinePlayer advancedOfflinePlayer;
	
	public String getName() {
		return UUIDUtils.getName(getUuid());
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getMetadata(String key) {
		return (T) Bukkit.getPlayer(getUuid()).getMetadata("techutils." + key).get(0); //Assume we are unique
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(getUuid());
	}
	
	public AdvancedOfflinePlayer getAdvancedOfflinePlayer() {
		if (advancedOfflinePlayer == null || advancedOfflinePlayer.isOnline() != isOnline()) {
			advancedOfflinePlayer = TechUtils.getAdvancedOfflinePlayer(getOfflinePlayer());
		}
		return advancedOfflinePlayer;
	}
	
	public Player getPlayer() {
		if (!isOnline()) throw new RuntimeException("not online");
		return Bukkit.getPlayer(getUuid());
	}
	
	public void setScoreboardProvider(ScoreboardProvider provider) {
		if (getScoreboard() == null && TechScoreboard.isSupported()) {
			TechScoreboard.createScoreboard(this);
		}
		getScoreboard().setProvider(provider);
	}
	public TechScoreboard getScoreboard() {
		if (!TechScoreboard.isSupported()) return null;
		return TechScoreboard.getScoreboard(this);
	}
	
	public boolean isOnline() {
		return getOfflinePlayer().isOnline();
	}
	
	public void copyTo(TechPlayer target) {
		if (target.isOnline()) copyTo(target.getPlayer());
		else copyTo(target.getAdvancedOfflinePlayer());
	}
	
	public void copyTo(Player target) {
		copyTo(TechUtils.getAdvancedOfflinePlayer(target));
		target.updateInventory();
	}
	
	public void copyTo(AdvancedOfflinePlayer target) {
		InventoryUtils.copy(getAdvancedOfflinePlayer(), target);
	}
	
	public void copyFrom(TechPlayer source) {
		if (source.isOnline()) copyFrom(source.getPlayer());
		else copyFrom(source.getAdvancedOfflinePlayer());
	}
	
	public void copyFrom(Player source) {
		copyFrom(TechUtils.getAdvancedOfflinePlayer(source));
		if (isOnline()) getPlayer().updateInventory();
	}
	
	public void copyFrom(AdvancedOfflinePlayer source) {
		InventoryUtils.copy(source, getAdvancedOfflinePlayer());
	}
	
	public void knockback(double power) {
		if (!isOnline()) return;
		Player player = getPlayer();
		player.setVelocity(VelocityUtils.knockback(getPlayer().getVelocity(), power));
	}
	
	//Cache
	
	private static EasyCache<UUID, TechPlayer> techPlayerCache = EasyCache.makeCache(new EasyCache.Loader<UUID, TechPlayer>() {

		@Override
		public TechPlayer load(UUID key) {
			return new TechPlayer(key);
		}
	
	});
	public static TechPlayer getTechPlayer(UUID id) {
		return techPlayerCache.get(id);
	}
	
	//Delegates
	
	
	//Advanced Offline Player
	
	public ItemStack[] getArmor() {
		return getAdvancedOfflinePlayer().getArmor();
	}

	public ItemStack getHelmet() {
		return getAdvancedOfflinePlayer().getHelmet();
	}

	public ItemStack getChestplate() {
		return getAdvancedOfflinePlayer().getChestplate();
	}

	public ItemStack getLeggings() {
		return getAdvancedOfflinePlayer().getLeggings();
	}

	public ItemStack getBoots() {
		return getAdvancedOfflinePlayer().getBoots();
	}

	public void setArmor(ItemStack[] armor) {
		getAdvancedOfflinePlayer().setArmor(armor);
	}

	public void setHelmet(ItemStack helmet) {
		getAdvancedOfflinePlayer().setHelmet(helmet);
	}

	public void setChestplate(ItemStack chestplate) {
		getAdvancedOfflinePlayer().setChestplate(chestplate);
	}

	public void setLeggings(ItemStack leggings) {
		getAdvancedOfflinePlayer().setLeggings(leggings);
	}

	public void setBoots(ItemStack boots) {
		getAdvancedOfflinePlayer().setBoots(boots);
	}

	public float getExp() {
		return getAdvancedOfflinePlayer().getExp();
	}

	public void setExp(float exp) {
		getAdvancedOfflinePlayer().setExp(exp);
	}

	public int getLevel() {
		return getAdvancedOfflinePlayer().getLevel();
	}

	public void setLevel(int level) {
		getAdvancedOfflinePlayer().setLevel(level);
	}

	public float getHealth() {
		return getAdvancedOfflinePlayer().getHealth();
	}

	public void setHealth(float health) {
		getAdvancedOfflinePlayer().setHealth(health);
	}

	public int getFoodLevel() {
		return getAdvancedOfflinePlayer().getFoodLevel();
	}

	public void setFoodLevel(int foodLevel) {
		getAdvancedOfflinePlayer().setFoodLevel(foodLevel);
	}

	public float getSaturation() {
		return getAdvancedOfflinePlayer().getSaturation();
	}

	public void setSaturation(float saturation) {
		getAdvancedOfflinePlayer().setSaturation(saturation);
	}

	public float getExhaustion() {
		return getAdvancedOfflinePlayer().getExhaustion();
	}

	public void setExhaustion(float exhaustion) {
		getAdvancedOfflinePlayer().setExhaustion(exhaustion);
	}

	public ItemStack[] getEnderchest() {
		return getAdvancedOfflinePlayer().getEnderchest();
	}

	public void setEnderchest(ItemStack[] enderchest) {
		getAdvancedOfflinePlayer().setEnderchest(enderchest);
	}

	public void setEnderchestItem(int slot, ItemStack item) {
		getAdvancedOfflinePlayer().setEnderchestItem(slot, item);
	}

	public ItemStack getEnderchestItem(int slot) {
		return getAdvancedOfflinePlayer().getEnderchestItem(slot);
	}

	public ItemStack[] getItems() {
		return getAdvancedOfflinePlayer().getItems();
	}

	public void setItems(ItemStack[] items) {
		getAdvancedOfflinePlayer().setItems(items);
	}

	public ItemStack getItem(int slot) {
		return getAdvancedOfflinePlayer().getItem(slot);
	}

	public void setItem(int slot, ItemStack item) {
		getAdvancedOfflinePlayer().setItem(slot, item);
	}

	public int getFireTicks() {
		return getAdvancedOfflinePlayer().getFireTicks();
	}

	public void setFireTicks(int ticks) {
		getAdvancedOfflinePlayer().setFireTicks(ticks);
	}

	public int getAir() {
		return getAdvancedOfflinePlayer().getAir();
	}

	public void setAir(int air) {
		getAdvancedOfflinePlayer().setAir(air);
	}

	public World getWorld() {
		return getAdvancedOfflinePlayer().getWorld();
	}

	public Location getLocation() {
		return getAdvancedOfflinePlayer().getLocation();
	}

	public void addPotionEffect(PotionEffect effect) {
		getAdvancedOfflinePlayer().addPotionEffect(effect);
	}

	public void addPotionEffects(Collection<PotionEffect> effects) {
		getAdvancedOfflinePlayer().addPotionEffects(effects);
	}

	public List<PotionEffect> getPotionEffects() {
		return getAdvancedOfflinePlayer().getPotionEffects();
	}

	public void removePotionEffect(PotionEffectType type) {
		getAdvancedOfflinePlayer().removePotionEffect(type);
	}
	
	public void save() {
		getAdvancedOfflinePlayer().save();
	}
	public void load() {
		getAdvancedOfflinePlayer().load();
	}
}
