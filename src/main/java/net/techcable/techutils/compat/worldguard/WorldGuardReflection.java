package net.techcable.techutils.compat.worldguard;

import static net.techcable.techutils.Reflection.*;
import net.techcable.techutils.Reflection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class WorldGuardReflection {
	
	public static Class<? extends Plugin> PLUGIN_CLASS = Reflection.getClass("com.sk89q.worldguard.bukkit.WorldGuardPlugin", Plugin.class);
	public static Class<?> REGION_MANAGER_CLASS = Reflection.getClass("com.sk89q.worldguard.protection.managers.RegionManager");
	public static Class<?> DEFAULT_FLAG_CLASS = Reflection.getClass("com.sk89q.worldguard.protection.flags.DefaultFlag");
	public static Class<?> APPLICABLE_REGION_SET_CLASS = Reflection.getClass("import com.sk89q.worldguard.protection.ApplicableRegionSet");
	public static Class<?> STATE_FLAG_CLASS = Reflection.getClass("com.sk89q.worldguard.protection.flags.StateFlag"); 
	
	
	public static Plugin getPlugin() {
		return Bukkit.getPluginManager().getPlugin("WorldGuard");
	}
	
	public static RegionManager getRegionManager(World world) {
		final Object regionHandle = callMethod(makeMethod(PLUGIN_CLASS, "getRegionManager", World.class), getPlugin(), world);
		return new RegionManager() {

			@Override
			public ApplicableRegionSet getApplicableRegionSet(final Location loc) {
				
				final Object regionSetHandle = callMethod(makeMethod(REGION_MANAGER_CLASS, "getApplicableRegions", Location.class), regionHandle, loc);
				
				return new ApplicableRegionSet() {
					
					@Override
					public boolean hasPvp() {
						return callMethod(makeMethod(APPLICABLE_REGION_SET_CLASS, "allows", STATE_FLAG_CLASS), regionSetHandle, PVP_FLAG);
					}
				};
			}
		};
	}
	
	public static boolean isSupported() {
		if (PLUGIN_CLASS != null);
		Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if (plugin != null && PLUGIN_CLASS.isInstance(plugin)) return true;
		else return false;
	}
	
	//Flags
	private static Object PVP_FLAG = callMethod(makeMethod(DEFAULT_FLAG_CLASS, "valueOf", String.class), null, "PVP");
}