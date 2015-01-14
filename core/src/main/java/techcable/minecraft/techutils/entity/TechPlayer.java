/**
 * The MIT License
 * Copyright (c) 2015 ${owner}
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package techcable.minecraft.techutils.entity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import net.techcable.minecraft.techutils.offlineplayer.PlayerData;

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
import techcable.minecraft.techutils.scoreboard.ScoreboardProvider;
import techcable.minecraft.techutils.scoreboard.TechScoreboard;
import techcable.minecraft.techutils.utils.EasyCache;
import lombok.*;

@Getter
@RequiredArgsConstructor(access=AccessLevel.PROTECTED)
public class TechPlayer {
	private final UUID uuid;
	private PlayerData playerData;
	private boolean playerDataOnline;
	
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
	
	public PlayerData getPlayerData() {
		if (playerData == null || playerDataOnline != isOnline()) {
			playerData = TechUtils.getPlayerData(getOfflinePlayer());
			playerDataOnline = isOnline();
		}
		return playerData;
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
		return Bukkit.getPlayer(getUuid()) != null;
	}
	
	public void copyTo(TechPlayer target) {
		if (target.isOnline()) copyTo(target.getPlayer());
		else copyTo(target.getPlayerData());
	}
	
	public void copyTo(Player target) {
		copyTo(TechUtils.getPlayerData(target));
		target.updateInventory();
	}
	
	public void copyTo(PlayerData target) {
		InventoryUtils.copy(getPlayerData(), target);
	}
	
	public void copyFrom(TechPlayer source) {
		copyFrom(source.getPlayerData());
	}
	
	public void copyFrom(Player source) {
		copyFrom(TechUtils.getPlayerData(source));
		if (isOnline()) getPlayer().updateInventory();
	}
	
	public void copyFrom(PlayerData source) {
		InventoryUtils.copy(source, getPlayerData());
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
		return getPlayerData().getArmor();
	}

	public ItemStack getHelmet() {
		return getPlayerData().getHelmet();
	}

	public ItemStack getChestplate() {
		return getPlayerData().getChestplate();
	}

	public ItemStack getLeggings() {
		return getPlayerData().getLeggings();
	}

	public ItemStack getBoots() {
		return getPlayerData().getBoots();
	}

	public void setArmor(ItemStack[] armor) {
		getPlayerData().setArmor(armor);
	}

	public void setHelmet(ItemStack helmet) {
		getPlayerData().setHelmet(helmet);
	}

	public void setChestplate(ItemStack chestplate) {
		getPlayerData().setChestplate(chestplate);
	}

	public void setLeggings(ItemStack leggings) {
		getPlayerData().setLeggings(leggings);
	}

	public void setBoots(ItemStack boots) {
		getPlayerData().setBoots(boots);
	}

	public float getExp() {
		return getPlayerData().getExp();
	}

	public void setExp(float exp) {
		getPlayerData().setExp(exp);
	}

	public int getLevel() {
		return getPlayerData().getLevel();
	}

	public void setLevel(int level) {
		getPlayerData().setLevel(level);
	}

	public float getHealth() {
		return getPlayerData().getHealth();
	}

	public void setHealth(float health) {
		getPlayerData().setHealth(health);
	}

	public int getFoodLevel() {
		return getPlayerData().getFoodLevel();
	}

	public void setFoodLevel(int foodLevel) {
		getPlayerData().setFoodLevel(foodLevel);
	}

	public float getSaturation() {
		return getPlayerData().getSaturation();
	}

	public void setSaturation(float saturation) {
		getPlayerData().setSaturation(saturation);
	}

	public float getExhaustion() {
		return getPlayerData().getExhaustion();
	}

	public void setExhaustion(float exhaustion) {
		getPlayerData().setExhaustion(exhaustion);
	}

	public ItemStack[] getEnderchest() {
		return getPlayerData().getEnderchest();
	}

	public void setEnderchest(ItemStack[] enderchest) {
		getPlayerData().setEnderchest(enderchest);
	}

	public void setEnderchestItem(int slot, ItemStack item) {
		getPlayerData().setEnderchestItem(slot, item);
	}

	public ItemStack getEnderchestItem(int slot) {
		return getPlayerData().getEnderchestItem(slot);
	}

	public ItemStack[] getItems() {
		return getPlayerData().getItems();
	}

	public void setItems(ItemStack[] items) {
		getPlayerData().setItems(items);
	}

	public ItemStack getItem(int slot) {
		return getPlayerData().getItem(slot);
	}

	public void setItem(int slot, ItemStack item) {
		getPlayerData().setItem(slot, item);
	}

	public int getFireTicks() {
		return getPlayerData().getFireTicks();
	}

	public void setFireTicks(int ticks) {
		getPlayerData().setFireTicks(ticks);
	}

	public int getAir() {
		return getPlayerData().getAir();
	}

	public void setAir(int air) {
		getPlayerData().setAir(air);
	}

	public World getWorld() {
		return getPlayerData().getWorld();
	}

	public Location getLocation() {
		return getPlayerData().getLocation();
	}

	public void addPotionEffect(PotionEffect effect) {
		getPlayerData().addPotionEffect(effect);
	}

	public void addPotionEffects(Collection<PotionEffect> effects) {
		getPlayerData().addPotionEffects(effects);
	}

	public Collection<PotionEffect> getPotionEffects() {
		return getPlayerData().getPotionEffects();
	}

	public void removePotionEffect(PotionEffectType type) {
		getPlayerData().removePotionEffect(type);
	}
	
	public void save() {
		getPlayerData().save();
	}
	public void load() {
		getPlayerData().load();
	}
}
