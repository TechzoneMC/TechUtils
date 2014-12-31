package techcable.minecraft.techutils.scoreboard;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import techcable.minecraft.techutils.entity.TechPlayer;

public interface ScoreboardProvider {
	public String getDisplayName(TechPlayer player);
	public LinkedHashMap<String, Integer> getText(TechPlayer player);
	
	/**
	 * This can help determine weather or not to update a scoreboard
	 * @param obj another provider
	 * @return weather or not the providers are the same
	 */
	public boolean equals(Object obj);
}