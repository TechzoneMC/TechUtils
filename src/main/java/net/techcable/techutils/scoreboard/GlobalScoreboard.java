package net.techcable.techutils.scoreboard;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import lombok.*;
import net.techcable.techutils.TechPlugin;
import net.techcable.techutils.entity.TechPlayer;

import java.util.Collection;

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