/*
 * This file is part of MCME-Events.
 * 
 * MCME-Events is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MCME-Events is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MCME-Events.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package com.mcmiddleearth.mcme.events.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 *
 * @author Donovan <dallen@dallen.xyz>
 */
public class EventLocation {
    
    private int X;
    
    private int Y;
    
    private int Z;
    
    private String World;
    
    public EventLocation(){}
    
    public EventLocation(Location l){
        this.X = l.getBlockX();
        this.Y = l.getBlockY();
        this.Z = l.getBlockZ();
        this.World = l.getWorld().getName();
    }
    
    public Location toBukkitLoc(){
        return new Location(Bukkit.getWorld(World), X, Y, Z);
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public int getZ() {
        return Z;
    }

    public void setZ(int z) {
        Z = z;
    }

    public String getWorld() {
        return World;
    }

    public void setWorld(String world) {
        World = world;
    }
}
