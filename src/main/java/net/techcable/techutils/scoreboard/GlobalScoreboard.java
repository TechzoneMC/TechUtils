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

import lombok.*;

import java.util.Collection;

import net.techcable.techutils.TechPlugin;
import net.techcable.techutils.entity.TechPlayer;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * A scoreboard that relays its changes to all scoreboards
 */
@RequiredArgsConstructor
public class GlobalScoreboard extends TechBoard {

    private final TechPlugin plugin;

    public Collection<TechBoard> getAllBoards() {
        return Collections2.transform(plugin.getOnlinePlayers(), new Function<TechPlayer, TechBoard>() {

            @Override
            public TechBoard apply(TechPlayer player) {
                return player.getScoreboard();
            }
        });
    }

    @Override
    public void display0() {
        for (TechBoard board : getAllBoards()) {
            board.aquireLock();
            resynchonyze(board); // Sends packets
            try {
                board.display0();
            } finally {
                board.releaseLock();
            }
        }
    }

    @Override
    public void hide0() {
        for (TechBoard board : getAllBoards()) {
            board.aquireLock();
            resynchonyze(board); // Sends packets
            try {
                board.display0();
            } finally {
                board.releaseLock();
            }
        }
    }

    @Override
    protected void setScore(String name, int score) {
        for (TechBoard board : getAllBoards()) {
            board.aquireLock();
            try {
                board.setScore(name, score);
            } finally {
                board.releaseLock();
            }
        }
    }

    @Override
    protected void flush() {
        for (TechBoard board : getAllBoards()) {
            board.aquireLock();
            resynchonyze(board);
            try {
                board.flush();
            } finally {
                board.releaseLock();
            }
        }
    }

    @Override
    protected void removeScore(String name) {
        for (TechBoard board : getAllBoards()) {
            board.aquireLock();
            resynchonyze(board); // Sends packets
            try {
                board.flush();
            } finally {
                board.releaseLock();
            }
        }
    }

    private void resynchonyze(TechBoard board) { // Only needs to be called when packets are sent
        if (this.getElements().equals(board.getElements())) return;
        board.clear();
        for (ScoreboardElement element : getElements()) {
            board.addElement(element.getName(), element.getScore());
        }
    }
}