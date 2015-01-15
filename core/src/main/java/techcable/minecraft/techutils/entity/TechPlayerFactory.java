package techcable.minecraft.techutils.entity;

import java.util.UUID;

import techcable.minecraft.techutils.TechPlugin;

public interface TechPlayerFactory<T extends TechPlayer> {
    public static TechPlayerFactory<TechPlayer> DEFAULT = new TechPlayerFactory<TechPlayer>() {
        @Override
        public TechPlayer createPlayer(UUID player, TechPlugin<TechPlayer> plugin) {
            return new TechPlayer(player, plugin);
        }
    };
    public T createPlayer(UUID player, TechPlugin<T> plugin);
}