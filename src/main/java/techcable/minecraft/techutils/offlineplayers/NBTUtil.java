package techcable.minecraft.techutils.offlineplayers;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spacehq.opennbt.tag.builtin.ByteTag;
import org.spacehq.opennbt.tag.builtin.CompoundTag;
import org.spacehq.opennbt.tag.builtin.DoubleTag;
import org.spacehq.opennbt.tag.builtin.IntTag;
import org.spacehq.opennbt.tag.builtin.ListTag;
import org.spacehq.opennbt.tag.builtin.ShortTag;
import org.spacehq.opennbt.tag.builtin.Tag;

public class NBTUtil {
    private NBTUtil() {}
    /**
     * Reads An ItemStack from nbt
     * Doesn't support enchantments yet
     */
    //NBT Files Actually Use "magic values" believe it or not
    @SuppressWarnings("deprecation")
    public static ItemStack readStack(CompoundTag tag) {
        try {
            short itemId = tag.<ShortTag>get("id").getValue();
            if (itemId < 0) return null; //Slot is Empty
            short damage = tag.contains("Damage") ? tag.<ShortTag>get("Damage").getValue() : 0;
            byte count = tag.<ByteTag>get("Count").getValue();
            return new ItemStack(itemId, count, damage);
        } catch (ClassCastException | NullPointerException ex) {
            throw new IllegalArgumentException("Not A Valid ItemStack!", ex);
        }
    }
	
	public static CompoundTag writeStack(ItemStack stack, byte slot) {
		CompoundTag tag = writeStack(stack);
		tag.put(new ByteTag("Slot", slot));
		return tag;
	}
	
	public static CompoundTag writeStack(ItemStack stack) {
		if (stack == null) return null;
		short itemId = (short) stack.getTypeId();
		if (stack.getAmount() <= 0) itemId = -1;
		byte count = (byte) stack.getAmount();
		short damage = stack.getDurability();
		CompoundTag tag = new CompoundTag("");
		if (itemId > -1) {
			tag.put(new ByteTag("Count", count));
			tag.put(new ShortTag("Damage", damage));
		}
		tag.put(new ShortTag("id", itemId));
		return tag;
	}
    
    public static void readMainInventory(AdvancedOfflinePlayer out, ListTag nbt) {
        ItemStack[] armor = new ItemStack[4];
        for (Tag rawTag : nbt) {
            if (rawTag instanceof CompoundTag) {
                CompoundTag tag = (CompoundTag) rawTag;
                if (!tag.contains("Slot")) throw new IllegalArgumentException("No Slot Number!");
                byte slot = tag.<ByteTag>get("Slot").getValue();
                ItemStack stack = readStack(tag);
                if (slot >= 0 && slot <= 35) {
                	if (OfflinePlayers.isDebug()) {
                		OfflinePlayers.info("Read " + stack.getAmount() + " " + stack.getType().toString() + " in slot " + slot);
                	}
                	out.setItem(slot, stack);
                } else if (slot >= 100 && slot <= 103) {
                	if (OfflinePlayers.isDebug()) {
                		OfflinePlayers.info("Read " + stack.getAmount() + " " + stack.getType().toString() + " in armor slot " + (slot - 100));
                	}
                	armor[slot - 100] = stack;
                } 
            }
        }
        out.setArmor(armor);
    }
	public static ListTag writeMainInventory(AdvancedOfflinePlayer in, String tagName) {
		//Using traditional for loop so i can get the slot num too
		ListTag tag = new ListTag(tagName, CompoundTag.class);
		ItemStack[] items = in.getItems();
		for (int slot = 0; slot < items.length; slot++) {
			if (items[slot] != null) tag.add(writeStack(items[slot], (byte)slot));
		}
		ItemStack[] armor = in.getArmor();
		for (int armorSlot = 0; armorSlot < armor.length; armorSlot++) {
			if (OfflinePlayers.isDebug()) {
				OfflinePlayers.info("Write " + armor[armorSlot].getAmount() + " " + armor[armorSlot].getType().toString() + " in armor slot " + armorSlot);
			}
			if (armor[armorSlot] != null) tag.add(writeStack(armor[armorSlot], (byte) (armorSlot + 100)));
		}
		return tag;
	}
	
	public static ItemStack[] readInventory(ListTag nbt, int expectedSize) {
		ItemStack[] inventory = new ItemStack[expectedSize];
		for (Tag rawTag : nbt) {
			if (rawTag instanceof CompoundTag) {
				CompoundTag tag = (CompoundTag) rawTag;
				if (!tag.contains("Slot")) throw new IllegalArgumentException("No Slot Number!");
				byte slot = tag.<ByteTag>get("Slot").getValue();
				//inventory.length is 1 based slot is 0 based so test for =>
				if (slot >= inventory.length) inventory = Arrays.copyOf(inventory, slot + 1);
				ItemStack stack = readStack(tag);
				inventory[slot] = stack;
			}
		}
		return inventory;
	}
	
	public static ItemStack[] readInventory(ListTag nbt) {
		return readInventory(nbt, 0);
	}
	
	public static ListTag writeInventory(ItemStack[] stack, String tagName) {
		ListTag tag = new ListTag(tagName, CompoundTag.class);
		for (int slot = 0; slot < stack.length; slot++) {
			if (stack[slot] != null) tag.add(writeStack(stack[slot], (byte) slot));
		}
		return tag;
	}
	
        public static File getPlayerFile(OfflinePlayer player) {
     	        return getPlayerFile(player, getPlayerWorld(player));
	}
	public static World getPlayerWorld(OfflinePlayer player) {
		for (World world : Bukkit.getWorlds()) {
			if (getPlayerFile(player, world).exists()) return world;
		}
		return null;
	}
	
	public static File getPlayerFile(OfflinePlayer player, World world) {
		File playerFolder = new File(world.getWorldFolder(), "playerdata");
    		File playerFile = new File(playerFolder, player.getUniqueId().toString() + ".dat");
    		return playerFile;
	}
	
	public static Location readLocation(CompoundTag tag, OfflinePlayer player) {
		ListTag pos = tag.get("Pos");
		double x = pos.<DoubleTag>get(0).getValue();
		double y = pos.<DoubleTag>get(1).getValue();
		double z = pos.<DoubleTag>get(2).getValue();
		World world = getPlayerWorld(player);
		return new Location(world, x, y, z);
	}
	
	/**
	* Write a location
	* doesn't support changing worlds
	*/
	public static ListTag writeLocation(Location loc) {
		ListTag pos = new ListTag("Pos", DoubleTag.class);
		pos.add(new DoubleTag("", loc.getX()));
		pos.add(new DoubleTag("", loc.getY()));
		pos.add(new DoubleTag("", loc.getZ()));
		return pos;
	}
	
	@SuppressWarnings("deprecation")
	public static PotionEffect readPotionEffect(CompoundTag tag) {
		int id = tag.<ByteTag>get("Id").getValue();
		PotionEffectType type = PotionEffectType.getById(id);
		int amplifier = tag.<ByteTag>get("Amplifier").getValue();
		int duration = tag.<IntTag>get("Duration").getValue();
		boolean ambient = tag.<ByteTag>get("Ambient").getValue() == 1 ? true : false;
		PotionEffect effect = new PotionEffect(type, duration, amplifier, ambient);
		return effect;
	}
	
	@SuppressWarnings("deprecation")
	public static CompoundTag writePotionEffect(PotionEffect effect) {
		CompoundTag tag = new CompoundTag("");
		int id = effect.getType().getId();
		int amplifier = effect.getAmplifier();
		int duration = effect.getDuration();
		boolean ambient = effect.isAmbient();
		tag.put(new ByteTag("Id", (byte)id));
		tag.put(new ByteTag("Amplifier", (byte)amplifier));
		tag.put(new IntTag("Duration", duration));
		tag.put(new ByteTag("Ambient", (byte)(ambient ? 1 : 0)));
		return tag;
	}
	
	public static PotionEffect[] readEffects(ListTag effectList) {
	    if (effectList == null || effectList.size() == 0) return new PotionEffect[0];
		PotionEffect[] effects = new PotionEffect[effectList.size()];
		for (int i = 0; i < effectList.size(); i++) {
			if (effectList.get(i) instanceof CompoundTag) {
				effects[i] = readPotionEffect((CompoundTag)effectList.get(i));
			}
		}
		return effects;
	}
	
	public static ListTag writeEffects(String name, PotionEffect[] effects) {
		ListTag effectList = new ListTag(name, CompoundTag.class);
		for (PotionEffect effect : effects) {
			effectList.add(writePotionEffect(effect));
		}
		return effectList;
	}
}
