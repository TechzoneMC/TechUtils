/**
 * The MIT License
 * Copyright (c) 2014-2015 Techcable
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
package net.techcable.minecraft.techutils.inventory;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@AllArgsConstructor
@Getter
class PlayerPlayerData implements PlayerData {
	private Player player;

	@Override
	public ItemStack[] getArmor() {
		return getInventory().getArmorContents();
	}

	@Override
	public ItemStack getHelmet() {
		return getInventory().getHelmet();
	}

	@Override
	public ItemStack getChestplate() {
		return getInventory().getChestplate();
	}

	@Override
	public ItemStack getLeggings() {
		return getInventory().getLeggings();
	}

	@Override
	public ItemStack getBoots() {
		return getInventory().getBoots();
	}

	@Override
	public void setArmor(ItemStack[] armor) {
		getInventory().setArmorContents(armor);
	}

	@Override
	public void setHelmet(ItemStack helmet) {
		getInventory().setHelmet(helmet);
	}

	@Override
	public void setChestplate(ItemStack chestplate) {
		getInventory().setChestplate(chestplate);
	}

	@Override
	public void setLeggings(ItemStack leggings) {
		getInventory().setLeggings(leggings);
	}

	@Override
	public void setBoots(ItemStack boots) {
		getInventory().setBoots(boots);
	}

	@Override
	public float getExp() {
		return getPlayer().getExp();
	}

	@Override
	public void setExp(float exp) {
		getPlayer().setExp(exp);
	}

	@Override
	public int getLevel() {
		return getPlayer().getLevel();
	}

	@Override
	public void setLevel(int level) {
		getPlayer().setLevel(level);
	}

	@Override
	public float getHealth() {
		return (float) getPlayer().getHealth();
	}

	@Override
	public void setHealth(float health) {
		getPlayer().setHealth(health);
	}

	@Override
	public int getFoodLevel() {
		return getPlayer().getFoodLevel();
	}

	@Override
	public void setFoodLevel(int foodLevel) {
		getPlayer().setFoodLevel(foodLevel);
	}

	@Override
	public float getSaturation() {
		return getPlayer().getSaturation();
	}

	@Override
	public void setSaturation(float saturation) {
		getPlayer().setSaturation(saturation);
	}

	@Override
	public float getExhaustion() {
		return getPlayer().getExhaustion();
	}

	@Override
	public void setExhaustion(float exhaustion) {
		getPlayer().setExhaustion(exhaustion);
	}

	@Override
	public ItemStack[] getEnderchest() {
		return getPlayer().getEnderChest().getContents();
	}

	@Override
	public void setEnderchest(ItemStack[] enderchest) {
		getPlayer().getEnderChest().setContents(enderchest);
	}

	@Override
	public void setEnderchestItem(int slot, ItemStack item) {
		getPlayer().getEnderChest().setItem(slot, item);
	}

	@Override
	public ItemStack getEnderchestItem(int slot) {
		return getPlayer().getEnderChest().getItem(slot);
	}

	@Override
	public ItemStack[] getItems() {
		return getInventory().getContents();
	}

	@Override
	public void setItems(ItemStack[] items) {
		getInventory().setContents(items);
	}

	@Override
	public ItemStack getItem(int slot) {
		return getInventory().getItem(slot);
	}

	@Override
	public void setItem(int slot, ItemStack item) {
		getInventory().setItem(slot, item);
	}

	@Override
	public int getFireTicks() {
		return getPlayer().getFireTicks();
	}

	@Override
	public void setFireTicks(int ticks) {
		getPlayer().setFireTicks(ticks);
	}

	@Override
	public int getAir() {
		return getPlayer().getRemainingAir();
	}

	@Override
	public void setAir(int air) {
		getPlayer().setRemainingAir(air);
	}

	@Override
	public World getWorld() {
		return getLocation().getWorld();
	}

	@Override
	public Location getLocation() {
		return getPlayer().getLocation();
	}

	@Override
	public void load() {}

	@Override
	public void save() {
		getPlayer().saveData();
	}

	@Override
	public void addPotionEffect(PotionEffect effect) {
		getPlayer().addPotionEffect(effect);
	}

	@Override
	public void addPotionEffects(Collection<PotionEffect> effects) {
		getPlayer().addPotionEffects(effects);
	}

	@Override
	public Collection<PotionEffect> getPotionEffects() {
		return getPlayer().getActivePotionEffects();
	}

	@Override
	public void removePotionEffect(PotionEffectType type) {
		getPlayer().removePotionEffect(type);
	}
	
	public PlayerInventory getInventory() {
		return getPlayer().getInventory();
	}
}