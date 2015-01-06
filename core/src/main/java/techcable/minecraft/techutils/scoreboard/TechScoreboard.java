package techcable.minecraft.techutils.scoreboard;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.StringUtil;

import com.google.common.base.Preconditions;

import techcable.minecraft.techutils.entity.TechPlayer;

import lombok.*;

@Getter
public class TechScoreboard {
	private final Scoreboard scoreboard;
	private final String internalName;
	private final TechPlayer player;
	private ScoreboardProvider provider;
	private final BufferedObjective objective = new BufferedObjective(getInternalName(), getScoreboard());
	
	public void setProvider(ScoreboardProvider provider) {
		this.provider = provider;
		update();
	}
	
	public void update() {
		if (!getPlayer().isOnline()) destroy();
		if (provider == null) {
			getObjective().setVisible(false);
		} else {
			getObjective().setDisplayName(getProvider().getDisplayName(getPlayer()));
			getObjective().clearText();
			getObjective().addText(getProvider().getText(getPlayer()));
			getObjective().update();
		}
	}
	
	public void destroy() {
		getObjective().setVisible(false);
		scoreboards.remove(getPlayer());
	}
	
	private TechScoreboard(TechPlayer player, String internalName) {
		this.player = player;
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.internalName = internalName;
	}
	
	private static final Map<TechPlayer, TechScoreboard> scoreboards = new HashMap<>();
	
	public static boolean isSupported() {
		return Bukkit.getScoreboardManager() != null;
	}
	public static TechScoreboard createScoreboard(TechPlayer player) {
		cleanMap();
		Preconditions.checkState(player.isOnline(), "This player isn't online");
		Preconditions.checkState(isSupported(), "Scoreboards aren't supported");
		String internalName = RandomStringUtils.random(10);
		TechScoreboard board = new TechScoreboard(player, internalName);
		scoreboards.put(player, board);
		return board;
	}
	public static TechScoreboard getScoreboard(TechPlayer player) {
		cleanMap();
		return scoreboards.get(player);
	}
	public static final long MAX_WAIT = 1000 * 10; //10 seconds
	private static long lastClean = 0;
	public static void cleanMap() {
		if (System.currentTimeMillis() - lastClean > MAX_WAIT) {
			for (TechScoreboard scoreboard : scoreboards.values()) {
				if (!scoreboard.getPlayer().isOnline()) {
					scoreboard.destroy();
				}
			}
			lastClean = System.currentTimeMillis();
		}
	}
	public static void updateAllWithProvider(ScoreboardProvider provider) {
		cleanMap();
		for (TechScoreboard scoreboard : scoreboards.values()) {
			if (scoreboard.getProvider() != null && scoreboard.getProvider().equals(provider)) {
				scoreboard.update();
			}
		}
	}
}