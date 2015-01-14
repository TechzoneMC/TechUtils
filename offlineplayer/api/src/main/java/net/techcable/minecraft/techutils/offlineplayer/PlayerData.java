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
package net.techcable.minecraft.techutils.offlineplayer;

import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public interface PlayerData {
	   //Armor Getting and Setting Methods
    public ItemStack[] getArmor();
    public ItemStack getHelmet();
    public ItemStack getChestplate();
    public ItemStack getLeggings();
    public ItemStack getBoots();
    public void setArmor(ItemStack[] armor);
    public void setHelmet(ItemStack helmet);
    public void setChestplate(ItemStack chestplate);
    public void setLeggings(ItemStack leggings);
    public void setBoots(ItemStack boots);
    
    //Exp Methods
    public float getExp();
    public void setExp(float exp);
    public int getLevel();
    public void setLevel(int level);
    
    //Food And Health Methods
    public float getHealth();
    public void setHealth(float health);
    public int getFoodLevel();
    public void setFoodLevel(int foodLevel);
    public float getSaturation();
    public void setSaturation(float saturation);
    public float getExhaustion();
    public void setExhaustion(float exhaustion);
    
    //EnderChest
    public ItemStack[] getEnderchest();
    public void setEnderchest(ItemStack[] enderchest);
    public void setEnderchestItem(int slot, ItemStack item);
    public ItemStack getEnderchestItem(int slot);
    
    //Main Inventory
    public ItemStack[] getItems();
    public void setItems(ItemStack[] items);
    public ItemStack getItem(int slot);
    public void setItem(int slot, ItemStack item);
    
    //Fire and Air (Sounds Like Avatar)
    public int getFireTicks();
    public void setFireTicks(int ticks);
    public int getAir();
    public void setAir(int air);
    
    //Location
    public World getWorld();
    public Location getLocation();
    
    //Abstract IO Methods
    public void load();
    public void save();
    
    //PotionEffects
    public void addPotionEffect(PotionEffect effect);
    public void addPotionEffects(Collection<PotionEffect> effects);
    public Collection<PotionEffect> getPotionEffects();
    public void removePotionEffect(PotionEffectType type);
}
