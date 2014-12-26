package techcable.minecraft.techutils.offlineplayers;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spacehq.opennbt.NBTIO;
import org.spacehq.opennbt.tag.builtin.CompoundTag;
import org.spacehq.opennbt.tag.builtin.DoubleTag;
import org.spacehq.opennbt.tag.builtin.FloatTag;
import org.spacehq.opennbt.tag.builtin.IntTag;
import org.spacehq.opennbt.tag.builtin.ListTag;
import org.spacehq.opennbt.tag.builtin.ShortTag;

import com.google.common.collect.Lists;

import lombok.*;

@Getter
@Setter
public class NBTAdvancedOfflinePlayer extends AbstractAdvancedOfflinePlayer {
	private File datFile;
	public NBTAdvancedOfflinePlayer(OfflinePlayer player) throws PlayerNotFoundException {
		super(player);
		datFile = NBTUtil.getPlayerFile(player);
		if (datFile == null || !datFile.exists()) throw new PlayerNotFoundException(player.getName());
		//Init Arrays
		armor = new ItemStack[4];
		items = new ItemStack[36];
		enderchest = new ItemStack[27];
		effects = new HashMap<>();
	}

	private ItemStack[] armor, items, enderchest;
	private float exp, health, saturation, exhaustion;
	private int level, foodLevel, fireTicks, air;
	private Map<PotionEffectType, PotionEffect> effects;
	/**
	* Doesn't support world changing!
	*/
	private Location location;

	@Override
	public void load() {
		CompoundTag nbt = readTag();
		NBTUtil.readMainInventory(this, nbt.<ListTag>get("Inventory"));
		setEnderchest(NBTUtil.readInventory(nbt.<ListTag>get("EnderItems"), 27));
		setExp(nbt.<FloatTag>get("XpP").getValue());
		//If we don't have the float health use the int one
		if (!nbt.contains("HealF")) setHealth((float) nbt.<IntTag>get("Health").getValue());
		else setHealth(nbt.<FloatTag>get("HealF").getValue());
		setSaturation(nbt.<FloatTag>get("foodSaturationLevel").getValue());
		setExhaustion(nbt.<FloatTag>get("foodExhaustionLevel").getValue());
		setLevel(nbt.<IntTag>get("XpLevel").getValue());
		setFoodLevel(nbt.<IntTag>get("foodLevel").getValue());
		setFireTicks(nbt.<ShortTag>get("Fire").getValue());
		setAir(nbt.<ShortTag>get("Air").getValue());
		setLocation(NBTUtil.readLocation(nbt, getBacking()));
		setEffects(toMap(NBTUtil.readEffects(nbt.<ListTag>get("ActiveEffects"))));
	}

	@Override
	public void save() {
		CompoundTag nbt = readTag();
		nbt.put(NBTUtil.writeMainInventory(this, "Inventory"));
		nbt.put(NBTUtil.writeInventory(getEnderchest(), "EnderItems"));
		nbt.put(new FloatTag("XpP", getExp()));
		nbt.put(new FloatTag("HealF", getHealth()));
		nbt.put(new FloatTag("foodSaturationLevel", getSaturation()));
		nbt.put(new FloatTag("foodExhaustionLevel", getExhaustion()));
		nbt.put(new IntTag("XpLevel", getLevel()));
		nbt.put(new IntTag("foodLevel", getFoodLevel()));
		nbt.put(new ShortTag("Fire", (short)getFireTicks()));
		nbt.put(new ShortTag("Air", (short)getAir()));
		nbt.put(NBTUtil.writeEffects("ActiveEffects", toArray(effects)));
		writeTag(nbt);
	}
	
	public CompoundTag readTag() {
		CompoundTag nbt;
		try {
			nbt = NBTIO.readFile(getDatFile());
			return nbt;
		} catch (IOException ex) {
			//Maybe not compressed ?
			try {
				nbt = NBTIO.readFile(getDatFile(), false);
				return nbt;
			} catch (IOException ex2) {
				throw new RuntimeException("Couldn't read " + getDatFile().getName(), ex2);
			}
		}
	}
	
	public void writeTag(CompoundTag tag) {
		try {
			NBTIO.writeFile(tag, getDatFile(), true);
		} catch (IOException ex) {
			throw new RuntimeException("Couldn't write " + getDatFile().getName());
		}
	}
	
	//Efficiency Overrides
	@Override
	protected void setArmor(int index, ItemStack armor) {
		getArmor()[index] = armor;
	}
	
	@Override
	public void setEnderchestItem(int slot, ItemStack item) {
		getEnderchest()[slot] = item;
	}
	
	@Override
	public void setItem(int slot, ItemStack item) {
		getItems()[slot] = item;
	}
	

	
	public static class PlayerNotFoundException extends Exception {

		private static final long serialVersionUID = -1264365592287143462L;

		public PlayerNotFoundException() {
			super();
		}

		public PlayerNotFoundException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		public PlayerNotFoundException(String arg0) {
			super(arg0);
		}

		public PlayerNotFoundException(Throwable arg0) {
			super(arg0);
		}
		
	}



	@Override
	public void addPotionEffect(PotionEffect effect) {
		effects.put(effect.getType(), effect);
	}

	@Override
	public void removePotionEffect(PotionEffectType type) {
		effects.remove(type);
	}
	

	@Override
	public List<PotionEffect> getPotionEffects() {
		return toList(effects);
	}
	
	private static Map<PotionEffectType, PotionEffect> toMap(PotionEffect[] effects) {
		Map<PotionEffectType, PotionEffect> map = new HashMap<>();
		for (PotionEffect effect : effects) {
			map.put(effect.getType(), effect);
		}
		return map;
	}
	
	private static PotionEffect[] toArray(Map<PotionEffectType, PotionEffect> map) {
		return map.values().toArray(new PotionEffect[map.size()]);
	}
	
	private static List<PotionEffect> toList(Map<PotionEffectType, PotionEffect> map) {
		return Lists.newArrayList(map.values());
	}

}