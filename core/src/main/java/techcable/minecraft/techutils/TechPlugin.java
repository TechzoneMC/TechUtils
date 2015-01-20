package techcable.minecraft.techutils;

import java.util.UUID;

import net.techcable.minecraft.techutils.offlineplayer.OfflinePlayers;
import net.techcable.minecraft.techutils.offlineplayer.PlayerData;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import techcable.minecraft.techutils.entity.TechPlayer;
import techcable.minecraft.techutils.entity.TechPlayerFactory;
import techcable.minecraft.techutils.utils.EasyCache;

public abstract class TechPlugin<T extends TechPlayer> extends JavaPlugin {
    public abstract TechPlayerFactory<T> getPlayerFactory();
    private EasyCache<UUID, T> playerCache = EasyCache.makeCache(new EasyCache.Loader<UUID, T>() {
       @Override
       public T load(UUID key) {
           return getPlayerFactory().createPlayer(key, TechPlugin.this);
       }
    });
    public T getPlayer(OfflinePlayer player) {
        return getPlayer(player.getUniqueId());
    }
    public T getPlayer(UUID id) {
        return playerCache.get(id);
    }
    public static PlayerData getPlayerData(UUID player) {
        return OfflinePlayers.getData(player);
    }
    public PlayerData getPlayerData(OfflinePlayer player) {
        return OfflinePlayers.getData(player);
    }
    public String getMetadataBase() {
        return "techutils." + getName() + ".";
    }
}