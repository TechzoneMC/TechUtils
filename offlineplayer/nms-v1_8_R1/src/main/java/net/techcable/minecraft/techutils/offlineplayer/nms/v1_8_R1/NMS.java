package net.techcable.minecraft.techutils.offlineplayer.nms.v1_8_R1;

import java.util.UUID;

import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.MinecraftServer;
import net.minecraft.server.v1_8_R1.PlayerInteractManager;
import net.minecraft.server.v1_8_R1.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_8_R1.CraftServer;
import org.bukkit.craftbukkit.v1_8_R1.util.MojangNameLookup;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

public class NMS implements net.techcable.minecraft.techutils.offlineplayer.NMS {

	@Override
	public Player loadFromFile(UUID playerId) {
		GameProfile profile = new GameProfile(playerId, MojangNameLookup.lookupName(playerId));
		MinecraftServer server = getHandle(Bukkit.getServer());
		WorldServer world = server.getWorldServer(0);
		PlayerInteractManager interactManager = new PlayerInteractManager(world);
		EntityPlayer player = new EntityPlayer(server, world, profile, interactManager);
		return player.getBukkitEntity();
	}
	
	public static MinecraftServer getHandle(Server server) {
		if (server instanceof CraftServer) {
			return ((CraftServer)server).getServer();
		}
		return null;
	}

}
