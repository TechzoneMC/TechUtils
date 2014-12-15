package techcable.minecraft.techutils.offlineplayers;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import lombok.*;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public abstract class AbstractAdvancedOfflinePlayer implements AdvancedOfflinePlayer {
	@Delegate
	private final OfflinePlayer player;
	
	public OfflinePlayer getBacking() {
		return player;
	}
	
	@Override
	public ItemStack getHelmet() {
		return getArmor()[3];
	}

	@Override
	public ItemStack getChestplate() {
		return getArmor()[2];
	}

	@Override
	public ItemStack getLeggings() {
		return getArmor()[1];
	}

	@Override
	public ItemStack getBoots() {
		return getArmor()[0];
	}

	@Override
	public void setHelmet(ItemStack helmet) {
		setArmor(3, helmet);
	}

	@Override
	public void setChestplate(ItemStack chestplate) {
		setArmor(2, chestplate);
	}

	@Override
	public void setLeggings(ItemStack leggings) {
		setArmor(1, leggings);
	}

	@Override
	public void setBoots(ItemStack boots) {
		setArmor(0, boots);
	}
	/**
	 * Used Internally 
	 * 
	 * Implementation can and should override this to be more efficient as it resets the entire array
	 * It does this because implementations can choose weather getArmor() returns the backing data or a copy
	 * 
	 * @param index the array index to set the slot
	 * @param armor the armor to set
	 */
	protected void setArmor(int index, ItemStack armor) {
		ItemStack[] currentArmor = getArmor();
		currentArmor[index] = armor;
		setArmor(currentArmor);
	}
	/**
	 * Implementation can and should override this to be more efficient as it resets the entire array
	 * It does this because implementations can choose weather getEnderchest() returns the backing data or a copy
	 * 
	 */
	@Override
	public void setEnderchestItem(int slot, ItemStack item) {
		ItemStack[] oldEnderchest = getEnderchest();
		oldEnderchest[slot] = item;
		setEnderchest(oldEnderchest);
	}

	@Override
	public ItemStack getEnderchestItem(int slot) {
		return getEnderchest()[slot];
	}

	@Override
	public ItemStack getItem(int slot) {
		return getItems()[slot];
	}
	/**
	 * Implementation can and should override this to be more efficient as it resets the entire array
	 * It does this because implementations can choose weather getItems() returns the backing data or a copy
	 *  
	 */
	@Override
	public void setItem(int slot, ItemStack item) {
		ItemStack[] items = getItems();
		items[slot] = item;
		setItems(items);
	}

	@Override
	public World getWorld() {
		return getLocation().getWorld();
	}
	
    public void addPotionEffects(Collection<PotionEffect> effects) {
    	for (PotionEffect effect : effects) {
    		addPotionEffect(effect);
    	}
    }
}
