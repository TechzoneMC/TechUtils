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
package net.techcable.techutils.scheduler;

import lombok.*;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.scheduler.BukkitScheduler;

import com.avaje.ebean.EbeanServer;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TickUtils {

    private static final Class<? extends BukkitScheduler> schedulerClass = Bukkit.getScheduler().getClass();

    private static volatile long currentTick;

    public static long getCurrentTick() {
        return currentTick;
    }

    private static void tick() {
        Preconditions.checkState(Bukkit.isPrimaryThread(), "Not on main thread");
        currentTick++;
        for (Runnable tickListener : tickListeners) {
            try {
                tickListener.run();
            } catch (Exception ignored) {
            }
        }
    }

    private static final Set<Runnable> tickListeners = new HashSet<>();

    private static void injectTicker() {
        Bukkit.getScheduler().runTaskTimer(new FakePlugin(), new Runnable() {

            @Override
            public void run() {
                tick();
            }
        }, 0, 1);
    }

    private static boolean setup;

    @Synchronized
    public static void addTickListener(Runnable tickListener) {
        tickListeners.add(tickListener);
        if (setup) return;
        injectTicker();
        setup = true;
    }

    @Getter
    private static final Executor mainThreadExecutor = new Executor() {

        @Override
        public void execute(Runnable command) {
            TechScheduler.scheduleSyncTask(command, 0);
        }
    };

    public static class FakePlugin implements Plugin {

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return null;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            return false;
        }

        @Override
        public File getDataFolder() {
            return null;
        }

        @Override
        public PluginDescriptionFile getDescription() {
            return new PluginDescriptionFile("TickUtils", "1.1.0", TickUtils.class.getName());
        }

        @Override
        public FileConfiguration getConfig() {
            return null;
        }

        @Override
        public InputStream getResource(String filename) {
            return null;
        }

        @Override
        public void saveConfig() {

        }

        @Override
        public void saveDefaultConfig() {

        }

        @Override
        public void saveResource(String resourcePath, boolean replace) {

        }

        @Override
        public void reloadConfig() {

        }

        @Override
        public PluginLoader getPluginLoader() {
            return null;
        }

        @Override
        public Server getServer() {
            return Bukkit.getServer();
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void onDisable() {

        }

        @Override
        public void onLoad() {

        }

        @Override
        public void onEnable() {

        }

        @Override
        public boolean isNaggable() {
            return false;
        }

        @Override
        public void setNaggable(boolean canNag) {

        }

        @Override
        public EbeanServer getDatabase() {
            return null;
        }

        @Override
        public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
            return null;
        }

        @Override
        public Logger getLogger() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    }
}
