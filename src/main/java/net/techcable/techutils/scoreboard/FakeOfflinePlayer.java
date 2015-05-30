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

import com.google.common.collect.Maps;
import lombok.*;
import net.techcable.techutils.uuid.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@EqualsAndHashCode(of = {"name"})
public class FakeOfflinePlayer implements OfflinePlayer {
    private final String name;

    @Override
    public boolean isOnline() {
        if (Bukkit.getOfflinePlayer(getUniqueId()) != null) {
            return Bukkit.getOfflinePlayer(getUniqueId()).isOnline();
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    private UUID id;
    @Override
    public UUID getUniqueId() {
        if (id == null) {
            id = UUIDUtils.getId(name);
            if (id == null) {
                id = UUID.randomUUID();
            }
        }
        return id;
    }

    @Override
    public boolean isBanned() {
        return Bukkit.getOfflinePlayer(getUniqueId()) == null ? false : Bukkit.getOfflinePlayer(getUniqueId()).isBanned();
    }

    @Override
    public void setBanned(boolean b) {
        if (Bukkit.getOfflinePlayer(getUniqueId()) != null) {
            Bukkit.getOfflinePlayer(getUniqueId()).setBanned(false);
        }
    }

    @Override
    public boolean isWhitelisted() {
        return Bukkit.getOfflinePlayer(getUniqueId()) == null ? true : Bukkit.getOfflinePlayer(getUniqueId()).isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean b) {
        if (Bukkit.getOfflinePlayer(getUniqueId()) != null) {
            Bukkit.getOfflinePlayer(getUniqueId()).setWhitelisted(b);
        }
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(getUniqueId());
    }

    @Override
    public long getFirstPlayed() {
        if (Bukkit.getOfflinePlayer(getUniqueId()) != null) {
            return Bukkit.getOfflinePlayer(getUniqueId()).getFirstPlayed();
        }
        return 0;
    }

    @Override
    public long getLastPlayed() {
        if (Bukkit.getOfflinePlayer(getUniqueId()) != null) {
            return Bukkit.getOfflinePlayer(getUniqueId()).getLastPlayed();
        }
        return 0;
    }

    @Override
    public boolean hasPlayedBefore() {
        if (Bukkit.getOfflinePlayer(getUniqueId()) != null) {
            return Bukkit.getOfflinePlayer(getUniqueId()).hasPlayedBefore();
        }
        return false;
    }

    @Override
    public Location getBedSpawnLocation() {
        if (Bukkit.getOfflinePlayer(getUniqueId()) != null) {
            return Bukkit.getOfflinePlayer(getUniqueId()).getBedSpawnLocation();
        }
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", name);
        if (id != null) map.put("id", id.toString());
        return map;
    }

    public static FakeOfflinePlayer deserialize(Map<String, Object> map) {
        String name = (String) map.get("name");
        FakeOfflinePlayer player = new FakeOfflinePlayer(name);
        if (map.containsKey("id")) player.id = UUID.fromString((String) map.get("id"));
        return player;
    }

    @Override
    public boolean isOp() {
        if (Bukkit.getOfflinePlayer(getUniqueId()) != null) {
            return Bukkit.getOfflinePlayer(getUniqueId()).isOp();
        }
        return false;
    }

    @Override
    public void setOp(boolean b) {
        if (Bukkit.getOfflinePlayer(getUniqueId()) != null) {
            Bukkit.getOfflinePlayer(getUniqueId()).setOp(b);
        }
    }
}
