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
