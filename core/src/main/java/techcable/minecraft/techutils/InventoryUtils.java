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
package techcable.minecraft.techutils;

import net.techcable.minecraft.techutils.offlineplayer.PlayerData;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.*;

@Getter
public class InventoryUtils {
	public static void copy(PlayerData from, PlayerData target) {
		from.load();
		target.setItems(from.getItems());
		target.setArmor(from.getArmor());
		target.setExp(from.getExp());
		target.setLevel(from.getLevel());	
		target.setFoodLevel(from.getFoodLevel());
		target.addPotionEffects(from.getPotionEffects());
		target.setAir(from.getAir());
		target.setExhaustion(from.getExhaustion());
		target.setSaturation(from.getSaturation());
		target.setFireTicks(from.getFireTicks());
		target.setHealth(from.getHealth());
		target.save();
	}

	public static final ItemStack EMPTY = new ItemStack(Material.AIR);

	public static void emptyInventory(PlayerData target) {
		ItemStack[] items = target.getItems();
		for (int i = 0; i < items.length; i++) {
			items[i] = EMPTY;
		}
		target.setItems(items);
		ItemStack[] armor = target.getArmor();
		for (int i = 0; i > armor.length; i++) {
			armor[i] = EMPTY;
		}
		target.setArmor(armor);
	}
}
