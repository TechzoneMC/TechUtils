package techcable.minecraft.techutils.offlineplayers.wrappers;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;

import techcable.minecraft.techutils.offlineplayers.AbstractAdvancedOfflinePlayer;

public class OnlineAdvancedOfflinePlayer extends AbstractAdvancedOfflinePlayer {
    public OnlineAdvancedOfflinePlayer(Player backing) {
        super(backing);
    }
    
    @Override
    public Player getBacking() {
        return (Player) super.getBacking();
    }
    
    
    public ItemStack[] getArmor() {
        return getBacking().getInventory().getArmorContents();
    }
    
    public void setArmor(ItemStack[] armor) {
        getBacking().getInventory().setArmorContents(armor);
    }
    
    public float getExp() {
        return getBacking().getExp();
    }
    public void setExp(float exp) {
        getBacking().setExp(exp);
    }
    public int getLevel() {
        return getBacking().getLevel();
    }
    public void setLevel(int level) {
        getBacking().setLevel(level);
    }
    
    //Food And Health Methods
    public float getHealth() {
        return (float) getBacking().getHealth();
    }
    public void setHealth(float health) {
        getBacking().setHealth(health);
    }
    public int getFoodLevel() {
        return getBacking().getFoodLevel();
    }
    public void setFoodLevel(int foodLevel) {
        getBacking().setFoodLevel(foodLevel);
    }
    public float getSaturation() {
        return getBacking().getSaturation();
    }
    public void setSaturation(float saturation) {
        getBacking().setSaturation(saturation);
    }
    public float getExhaustion() {
        return getBacking().getExhaustion();
    }
    public void setExhaustion(float exhaustion) {
        getBacking().setExhaustion(exhaustion);
    }
    
    //EnderChest
    public ItemStack[] getEnderchest() {
        return getBacking().getEnderChest().getContents();
    }
    public void setEnderchest(ItemStack[] enderchest) {
        getBacking().getEnderChest().setContents(enderchest);
    }
    public void setEnderchestItem(int slot, ItemStack item) {
        getBacking().getEnderChest().setItem(slot, item);
    }
    public ItemStack getEnderchestItem(int slot) {
        return getBacking().getEnderChest().getItem(slot);
    }
    
    //Main Inventory
    public ItemStack[] getItems() {
        return getBacking().getInventory().getContents();
    }
    public void setItems(ItemStack[] items) {
        getBacking().getInventory().setContents(items);
    }
    public ItemStack getItem(int slot) {
        return getBacking().getInventory().getItem(slot);
    }
    public void setItem(int slot, ItemStack item) {
        getBacking().getInventory().setItem(slot, item);
    }
    
    //Fire and Air (Sounds Like Avatar)
    public int getFireTicks() {
        return getBacking().getFireTicks();
    }
    public void setFireTicks(int ticks) {
        getBacking().setFireTicks(ticks);
    }
    public int getAir() {
        return getBacking().getRemainingAir();
    }
    public void setAir(int air) {
        getBacking().setRemainingAir(air);
    }
    
    public Location getLocation() {
        return getBacking().getLocation();
    }
    
    public void load() {
    	//If i loaded it would override existing data
    }
    public void save() {
    	getBacking().updateInventory();
    	getBacking().saveData();
    }
    //Potion Effects
	@Override
	public void addPotionEffect(PotionEffect effect) {
		getBacking().addPotionEffect(effect);
	}

	@Override
	public List<PotionEffect> getPotionEffects() {
		return Lists.newArrayList(getBacking().getActivePotionEffects());
	}

	@Override
	public void removePotionEffect(PotionEffectType type) {
		getBacking().removePotionEffect(type);
	}
}