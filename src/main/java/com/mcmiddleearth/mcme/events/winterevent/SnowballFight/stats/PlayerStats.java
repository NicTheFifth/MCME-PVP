/**
 * This file is part of winterEvent, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014 Henry Slawniak <http://mcme.co/>
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
package com.mcmiddleearth.mcme.events.winterevent.SnowballFight.stats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mcmiddleearth.mcme.events.Main;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class PlayerStats {

    private String playerUUID;
    private String playerName;
    private int thrown = 0;
    private int hitOthers = 0;
    private int hitSelf = 0;

    public PlayerStats(Player player) {
        this.playerUUID = player.getUniqueId().toString();
        this.playerName = player.getName();
    }

    public PlayerStats() {

    }

    /**
     * Increment the number of times this player has thrown a snowball.
     *
     * @return The new amount of snowballs this player has thrown.
     */
    public int incrementThrown() {
        thrown += 1;
        return thrown;
    }

    /**
     * Increment the number of times this player has thrown a snowball.
     *
     * @param amount The amount to increment by.
     * @return The new amount of snowballs this player has thrown.
     */
    public int incrementThrown(int amount) {
        thrown += amount;
        return thrown;
    }

    /**
     * Increment the number of times this player has hit someone with a snowball.
     *
     * @return The new amount of times this player has hit others.
     */
    public int incrementHitOthers() {
        hitOthers += 1;
        return hitOthers;
    }

    /**
     * Increment the number of times this player has hit someone with a snowball.
     *
     * @param amount The amount to increment by.
     * @return The new amount of times this player has hit others.
     */
    public int incrementHitOthers(int amount) {
        hitOthers += amount;
        return hitOthers;
    }

    /**
     * Increment the number of times this player has been hit with a snowball.
     *
     * @return The new amount of snowballs this player has been hit by.
     */
    public int incrementHitSelf() {
        hitSelf += 1;
        return hitSelf;
    }

    /**
     * Increment the number of times this player has been hit with a snowball.
     *
     * @param amount The amount to increment by.
     * @return The new amount of snowballs this player has been hit by.
     */
    public int incrementHitSelf(int amount) {
        hitSelf += amount;
        return hitSelf;
    }

    /**
     * Write this statistics to disk.
     */
    public void save() throws IOException {
        Main.getObjectMapper().writeValue(getFileLocation(), this);
    }

    @JsonIgnore
    public File getFileLocation() {
        return new File(Main.getPlayerDirectory(), playerUUID + ".snowball");
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getThrown() {
        return thrown;
    }

    public void setThrown(int thrown) {
        this.thrown = thrown;
    }

    public int getHitOthers() {
        return hitOthers;
    }

    public void setHitOthers(int hitOthers) {
        this.hitOthers = hitOthers;
    }

    public int getHitSelf() {
        return hitSelf;
    }

    public void setHitSelf(int hitSelf) {
        this.hitSelf = hitSelf;
    }
}
