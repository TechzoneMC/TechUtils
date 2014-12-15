package techcable.minecraft.techutils.offlineplayers;

import java.util.Collection;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

/**
 * An abstract player that reads data from its data file
 * Methods work the same as the player class
 */
public interface AdvancedOfflinePlayer extends OfflinePlayer {
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
    public List<PotionEffect> getPotionEffects();
    public void removePotionEffect(PotionEffectType type);
}