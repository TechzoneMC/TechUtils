package net.techcable.minecraft.techutils.offlineplayer;

import java.util.UUID;

import org.bukkit.entity.Player;

public interface NMS {
	public Player loadFromFile(UUID playerId);
}
