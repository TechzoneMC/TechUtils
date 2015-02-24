package net.techcable.techutils.compat.worldguard;

import org.bukkit.Location;

public interface RegionManager {
	public ApplicableRegionSet getApplicableRegionSet(Location loc);
}
