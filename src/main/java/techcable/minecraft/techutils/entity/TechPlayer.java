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

import techcable.minecraft.techutils.TechUtils;
import techcable.minecraft.techutils.UUIDUtils;
import techcable.minecraft.techutils.VelocityUtils;
import techcable.minecraft.techutils.offlineplayers.AdvancedOfflinePlayer;
import techcable.minecraft.techutils.utils.EasyCache;

import lombok.*;
import lombok.experimental.Delegate;

@Getter
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public class TechPlayer {
	private final UUID uuid;
	private AdvancedOfflinePlayer advancedOfflinePlayer;
	
	public String getName() {
		return UUIDUtils.getName(getUuid());
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
		 load(); 
		 target.setItems(getItems());
		 target.setArmor(getArmor());
		 target.setExp(getExp());
		 target.setLevel(getLevel());
		 target.setFoodLevel(getFoodLevel());
		 target.addPotionEffects(getPotionEffects());
		 target.setAir(getAir());
		 target.setExhaustion(getExhaustion());
		 target.setSaturation(getSaturation());
		 target.setFireTicks(getFireTicks());
		 target.setHealth(getHealth());
		 target.save();
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
		source.load();
		setItems(source.getItems());
		setArmor(source.getArmor());
		setExp(source.getExp());
		setLevel(source.getLevel());
		setFoodLevel(source.getFoodLevel());
		addPotionEffects(source.getPotionEffects());
		setAir(source.getAir());
		setExhaustion(source.getExhaustion());
		setSaturation(source.getSaturation());
		setFireTicks(source.getFireTicks());
		setHealth(source.getHealth());
		save();
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
		return advancedOfflinePlayer.getArmor();
	}

	public ItemStack getHelmet() {
		return advancedOfflinePlayer.getHelmet();
	}

	public ItemStack getChestplate() {
		return advancedOfflinePlayer.getChestplate();
	}

	public ItemStack getLeggings() {
		return advancedOfflinePlayer.getLeggings();
	}

	public ItemStack getBoots() {
		return advancedOfflinePlayer.getBoots();
	}

	public void setArmor(ItemStack[] armor) {
		advancedOfflinePlayer.setArmor(armor);
	}

	public void setHelmet(ItemStack helmet) {
		advancedOfflinePlayer.setHelmet(helmet);
	}

	public void setChestplate(ItemStack chestplate) {
		advancedOfflinePlayer.setChestplate(chestplate);
	}

	public void setLeggings(ItemStack leggings) {
		advancedOfflinePlayer.setLeggings(leggings);
	}

	public void setBoots(ItemStack boots) {
		advancedOfflinePlayer.setBoots(boots);
	}

	public float getExp() {
		return advancedOfflinePlayer.getExp();
	}

	public void setExp(float exp) {
		advancedOfflinePlayer.setExp(exp);
	}

	public int getLevel() {
		return advancedOfflinePlayer.getLevel();
	}

	public void setLevel(int level) {
		advancedOfflinePlayer.setLevel(level);
	}

	public float getHealth() {
		return advancedOfflinePlayer.getHealth();
	}

	public void setHealth(float health) {
		advancedOfflinePlayer.setHealth(health);
	}

	public int getFoodLevel() {
		return advancedOfflinePlayer.getFoodLevel();
	}

	public void setFoodLevel(int foodLevel) {
		advancedOfflinePlayer.setFoodLevel(foodLevel);
	}

	public float getSaturation() {
		return advancedOfflinePlayer.getSaturation();
	}

	public void setSaturation(float saturation) {
		advancedOfflinePlayer.setSaturation(saturation);
	}

	public float getExhaustion() {
		return advancedOfflinePlayer.getExhaustion();
	}

	public void setExhaustion(float exhaustion) {
		advancedOfflinePlayer.setExhaustion(exhaustion);
	}

	public ItemStack[] getEnderchest() {
		return advancedOfflinePlayer.getEnderchest();
	}

	public void setEnderchest(ItemStack[] enderchest) {
		advancedOfflinePlayer.setEnderchest(enderchest);
	}

	public void setEnderchestItem(int slot, ItemStack item) {
		advancedOfflinePlayer.setEnderchestItem(slot, item);
	}

	public ItemStack getEnderchestItem(int slot) {
		return advancedOfflinePlayer.getEnderchestItem(slot);
	}

	public ItemStack[] getItems() {
		return advancedOfflinePlayer.getItems();
	}

	public void setItems(ItemStack[] items) {
		advancedOfflinePlayer.setItems(items);
	}

	public ItemStack getItem(int slot) {
		return advancedOfflinePlayer.getItem(slot);
	}

	public void setItem(int slot, ItemStack item) {
		advancedOfflinePlayer.setItem(slot, item);
	}

	public int getFireTicks() {
		return advancedOfflinePlayer.getFireTicks();
	}

	public void setFireTicks(int ticks) {
		advancedOfflinePlayer.setFireTicks(ticks);
	}

	public int getAir() {
		return advancedOfflinePlayer.getAir();
	}

	public void setAir(int air) {
		advancedOfflinePlayer.setAir(air);
	}

	public World getWorld() {
		return advancedOfflinePlayer.getWorld();
	}

	public Location getLocation() {
		return advancedOfflinePlayer.getLocation();
	}

	public void addPotionEffect(PotionEffect effect) {
		advancedOfflinePlayer.addPotionEffect(effect);
	}

	public void addPotionEffects(Collection<PotionEffect> effects) {
		advancedOfflinePlayer.addPotionEffects(effects);
	}

	public List<PotionEffect> getPotionEffects() {
		return advancedOfflinePlayer.getPotionEffects();
	}

	public void removePotionEffect(PotionEffectType type) {
		advancedOfflinePlayer.removePotionEffect(type);
	}
	
	public void save() {
		getAdvancedOfflinePlayer().save();
	}
	public void load() {
		getAdvancedOfflinePlayer().load();
	}
}
