/**
 * The MIT License
 * Copyright (c) 2014-2015 Techcable
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
		GameProfile profile = new GameProfile(playerId, getName(playerId));
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
    private static String getName(UUID id) {
        if (!Bukkit.getOnlineMode()) {
            return Bukkit.getOfflinePlayer(id).getName();
        } else {
            return MojangNameLookup.lookupName(id);
        }
    }
}
