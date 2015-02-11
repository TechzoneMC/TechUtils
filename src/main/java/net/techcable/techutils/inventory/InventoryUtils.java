package net.techcable.techutils.inventory;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
    private InventoryUtils() {}
    
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
     * 
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

    /**
     * Empty the specified player's inventory
     * 
     * @param target the inventory to empty
     */
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