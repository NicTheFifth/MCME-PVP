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
package com.mcmiddleearth.mcme.events.winterevent.SnowManInvasion;

import com.mcmiddleearth.mcme.events.winterevent.SnowManInvasion.Snowman.InvasionSnowman;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Donovan
 */
public class SpawnPoint {
    Location center;
    int Radius;
    
    public SpawnPoint(Location l, int r){
        center = l;
        Radius = r;
    }
    
    public ArrayList<InvasionSnowman> spawn(){
        Random r = new Random();
        double number = r.nextInt(5) + 1;
        ArrayList<InvasionSnowman> rtn = new ArrayList<>();
        for(int i = 0; i < number; i++){
            double angle = Math.random()*Math.PI*2;
            double x = Math.cos(angle)*r.nextInt(Radius);
            double z = Math.sin(angle)*r.nextInt(Radius);
            InvasionSnowman is = new InvasionSnowman(new Location(center.getWorld(), center.getX() + x, center.getY() + 1,center.getZ() + z));
            rtn.add(is);
        }
        return rtn;
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public int getRadius() {
        return Radius;
    }

    public void setRadius(int radius) {
        Radius = radius;
    }
}
