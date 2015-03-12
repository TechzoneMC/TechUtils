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
package net.techcable.techutils.inventory;

import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Represents a player's data
 * <br>
 * May be a immutable snapshot of the state of a player's inventory or just reflect the current state of the player
 * <p>
 * This includes
 * <ul>
 *  <li>Inventory</li>
 *  <li>Armor</li>
 *  <li>Experience</li>
 *  <li>Health</li>
 *  <li>Food</li>
 *  <li>Enderchest</li>
 *  <li>Fire Time</li>
 *  <li>Air Time</li>
 *  <li>Location</li>
 *  <li>Potion Effects</li>
 * </ul>
 */
public interface PlayerData {
    //Armor0 Getting and Setting Methods
    public List<ItemStack> getArmor();
    public ItemStack getHelmet();
    public ItemStack getChestplate();
    public ItemStack getLeggings();
    public ItemStack getBoots();
    public void setArmor(List<? extends ItemStack> armor);
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
    public List<ItemStack> getEnderchest();
    public void setEnderchest(List<ItemStack> enderchest);
    public void setEnderchestItem(int slot, ItemStack item);
    public ItemStack getEnderchestItem(int slot);
    
    //Main Inventory
    public List<ItemStack> getItems();
    public void setItems(List<ItemStack> items);
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
    
    /**
     * Get an immutable snapshot of the current state of this player data
     * <br>
     * If this object is immutable may return itself
     * 
     * @return immutable snapshot of current state
     */
    public PlayerData getSnapshot();
}