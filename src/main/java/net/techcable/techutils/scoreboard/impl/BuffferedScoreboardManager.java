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
package net.techcable.techutils.scoreboard.impl;

import net.techcable.techutils.scoreboard.IScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

/**
 * All credit to:
 * http://bukkit.org/threads/scoreboards-without-flashing-on-refresh-class.356078/#post-3094679
 * and Wolvereness
 */
public class BuffferedScoreboardManager implements IScoreboardManager {

    private ScoreboardManager manager;
    private Scoreboard board;
    private Team team;
    private Objective o;
    private Objective buffer;
    private Objective t;

    public BuffferedScoreboardManager(Plugin plugin) {
        // Setting up all the scoreboard variables
        this.manager = plugin.getServer().getScoreboardManager();
        this.board = manager.getNewScoreboard();
        this.team = board.registerNewTeam("team");
        // Two objectives for buffer & normal
        this.o = board.registerNewObjective("test", "dummy");
        this.buffer = board.registerNewObjective("buffer", "dummy");

        // Setting up the scoreboard display stuff
        this.o.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.o.setDisplayName("SCOREBOARD!!!");
    }

    public void setScore(String name, int score) {
        // First you update the buffer
        buffer.getScore(name).setScore(score);
        // Then you tell the scoreboard to use the buffer
        // and swap the variables for our convenience
        swapBuffer();
        // And update the -what used to be objective- buffer
        buffer.getScore(name).setScore(score);
    }

    public void swapBuffer() {
        // Simply change the slot, the scoreboard will now
        // push all updating packets to the player
        // Thus wasting not a single ms on executing this at
        // a later time
        buffer.setDisplaySlot(o.getDisplaySlot());
        buffer.setDisplayName(o.getDisplayName());
        // Simply changing references for naming convenience
        t = o;
        o = buffer;
        buffer = t;
    }
    // Adding the player to the scoreboard
    public void addPlayer(Player p) {
        team.addPlayer(p);
        p.setScoreboard(board);
    }

    // Removing the player from the scoreboard
    public void removePlayer(Player p) {
        team.removePlayer(p);
        p.setScoreboard(manager.getNewScoreboard());
    }
}