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
package net.techcable.techutils.scoreboard;

import com.google.common.collect.ImmutableSet;
import lombok.*;
import net.techcable.techutils.entity.TechPlayer;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A no-flicker scoreboard manager that properly respects existing scoreboards
 *
 * <p>
 * Credits:
 * - DarkSaraphram (fireblast709) -- https://bukkit.org/threads/update-a-scoreboard-every-second-without-flashing.288265/
 * - Wolvereness -- http://sbnc.khobbits.co.uk/log/logs/old/bukkitdev_%5B2014-06-09%5D.htm
 * </p>
 */
public class BukkitTechBoard extends TechBoard {

    private Objective o;
    private Objective buffer;
    private Objective t;

    @Getter
    private final TechPlayer player;
    public BukkitTechBoard(TechPlayer player) {
        this.player = player;
        if (player.getEntity().getScoreboard() == null) player.getEntity().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    @Override
    public void display0() {
        Scoreboard board = getPlayer().getEntity().getScoreboard();
        if (board == null) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
            getPlayer().getEntity().setScoreboard(board);
        }
        if (o == null) {
            this.o = board.registerNewObjective(getPlayer().getPlugin().getName() + "-primary", "dummy");
            this.buffer = board.registerNewObjective(getPlayer().getPlugin().getName() + "-buffer", "dummy");
        }
        this.o.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    @Override
    public void hide0() {
        Scoreboard board = getPlayer().getEntity().getScoreboard();
        if (board == null) return;
        this.o.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private final ConcurrentLinkedQueue<ScoreboardChange> changes = new ConcurrentLinkedQueue<>();

    @Override
    protected void setScore(final String name, final int score) {
        changes.add(new ScoreboardChange() {
            @Override
            public void execute(Objective o) {
                o.getScore(new FakeOfflinePlayer(name)).setScore(score);
            }
        });
    }

    @Override
    protected void removeScore(final String name) {
        changes.add(new ScoreboardChange() {
            @Override
            public void execute(Objective o) {
                o.getScoreboard().resetScores(new FakeOfflinePlayer(name));
            }
        });
    }

    @Override
    public void cleanup() {
        super.cleanup();
        o.unregister();
        t.unregister();
    }

    @Override
    protected void flush() {
        if (changes.isEmpty()) return;
        ImmutableSet.Builder<ScoreboardChange> changesBuilder = ImmutableSet.builder();
        ScoreboardChange pending;
        while ((pending = this.changes.poll()) != null) {
            changesBuilder.add(pending);
        }
        ImmutableSet<ScoreboardChange> changes = changesBuilder.build();
        for (ScoreboardChange change : changes) {
            change.execute(buffer);
        }
        swapBuffer();
        for (ScoreboardChange change : changes) {
            change.execute(buffer);
        }
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

    public static interface ScoreboardChange {
        public void execute(Objective o);
    }
}