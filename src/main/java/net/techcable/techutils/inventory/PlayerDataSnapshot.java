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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.techcable.techutils.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * A 
 * 
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
 * @author Nicholas Schlabach
 */
public class PlayerDataSnapshot implements PlayerData {
    private final ImmutableList<ItemStack> items;
    private final ImmutableList<ItemStack> armor;
    private final float experience;
    private final int xpLevel;
    private final float health;
    private final int food;
    private final float saturation;
    private final float exhaustion;
    private final ImmutableList<ItemStack> enderchest;
    private final int fire;
    private final int air;
    private final Location location;
    private final ImmutableSet<PotionEffect> potions;
    
    public PlayerDataSnapshot(List<ItemStack> items, List<ItemStack> armor, float experience, int xpLevel, float health, int food, float saturation, float exhaustion, List<ItemStack> enderchest, int fire, int air, Location location, Collection<PotionEffect> potions) {
        this.items = clone(items);
        this.armor = clone(armor);
        this.experience = experience;
        this.xpLevel = xpLevel;
        this.health = health;
        this.food = food;
        this.saturation = saturation;
        this.exhaustion = exhaustion;
        this.enderchest = clone(enderchest);
        this.fire = fire;
        this.air = air;
        this.location = location.clone();
        this.potions = ImmutableSet.copyOf(potions);
    }
    
    protected PlayerDataSnapshot(PlayerData old) {
        this(old.getItems(), old.getArmor(), old.getExp(), old.getLevel(), old.getHealth(), old.getFoodLevel(), old.getSaturation(), old.getExhaustion(), old.getEnderchest(), old.getFireTicks(), old.getAir(), old.getLocation(), old.getPotionEffects());
    }
    
    private static ImmutableList<ItemStack> clone(List<ItemStack> originals) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        for (ItemStack original : originals) {
            ItemStack clone = original.clone();
            builder.add(clone);
        }
        return builder.build();
    }

    
    
    //Getters

    @Override
    public List<ItemStack> getArmor() {
        return clone(armor);
    }

    @Override
    public ItemStack getHelmet() {
        return armor.get(3).clone();
    }

    @Override
    public ItemStack getChestplate() {
        return armor.get(2).clone();
    }

    @Override
    public ItemStack getLeggings() {
        return armor.get(1).clone();
    }

    @Override
    public ItemStack getBoots() {
        return armor.get(0).clone();
    }
    @Override
    public float getExp() {
        return experience;
    }

    @Override
    public int getLevel() {
        return xpLevel;
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    public int getFoodLevel() {
        return food;
    }

    @Override
    public float getSaturation() {
        return saturation;
    }

    @Override
    public float getExhaustion() {
        return exhaustion;
    }

    @Override
    public List<ItemStack> getEnderchest() {
        return clone(enderchest);
    }

    @Override
    public ItemStack getEnderchestItem(int slot) {
        return enderchest.get(slot).clone();
    }

    @Override
    public List<ItemStack> getItems() {
        return clone(items);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot).clone();
    }

    @Override
    public int getFireTicks() {
        return fire;
    }

    @Override
    public int getAir() {
        return air;
    }

    @Override
    public World getWorld() {
        return location.getWorld();
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public Collection<PotionEffect> getPotionEffects() {
        return potions;
    }

    @Override
    public PlayerData getSnapshot() {
        return this;
    }
    
    //NO-OPS
    @Override
    public void load() {}
    
    @Override
    public void save() {}
    
    //Unsupported

    @Override
    public void setArmor(List<? extends ItemStack> armor) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setHelmet(ItemStack helmet) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setChestplate(ItemStack chestplate) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setLeggings(ItemStack leggings) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setBoots(ItemStack boots) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setExp(float exp) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setLevel(int level) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setHealth(float health) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setFoodLevel(int foodLevel) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setSaturation(float saturation) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setExhaustion(float exhaustion) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setEnderchest(List<ItemStack> enderchest) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setEnderchestItem(int slot, ItemStack item) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setItems(List<ItemStack> items) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setFireTicks(int ticks) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void setAir(int air) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }
    
    @Override
    public void addPotionEffect(PotionEffect effect) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void addPotionEffects(Collection<PotionEffect> effects) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }

    @Override
    public void removePotionEffect(PotionEffectType type) {
        throw new UnsupportedOperationException("You can't edit immutable objects");
    }
}