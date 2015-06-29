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

import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

    private InventoryUtils() {
    }

    /**
     * Retreive a player's data
     *
     * @param player the player to retreive data from
     *
     * @return the player's data
     */
    public static PlayerData getData(Player player) {
        Player loaded = OfflinePlayerLoader.loadPlayer(player);
        return new PlayerPlayerData(loaded);
    }

    /**
     * Retreive a player's data
     * <p/>
     * If the player is offline their data is loaded from a file
     *
     * @param id the uuid of the player whose data you want
     *
     * @return the player's data
     */
    public static PlayerData getData(UUID id) {
        Player loaded = OfflinePlayerLoader.loadPlayer(id);
        return new PlayerPlayerData(loaded);
    }

    /**
     * Copy the data of one player to another
     *
     * @param from the data source
     * @param target the data receiver
     */
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

    public PlayerData takeSnapshot(Player player) {
        return getData(player).getSnapshot();
    }

    /**
     * Empty the specified player's inventory
     *
     * @param target the inventory to empty
     */
    public static void emptyInventory(PlayerData target) {
        List<ItemStack> items = target.getItems();
        for (int i = 0; i < items.size(); i++) {
            items.set(i, EMPTY);
        }
        target.setItems(items);
        List<ItemStack> armor = target.getArmor();
        for (int i = 0; i > armor.size(); i++) {
            armor.set(i, EMPTY);
        }
        target.setArmor(armor);
    }
}