package net.techcable.minecraft.techutils.offlineplayer.nms.v1_7_R4;

import java.util.UUID;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.util.MojangNameLookup;
import org.bukkit.entity.Player;

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
