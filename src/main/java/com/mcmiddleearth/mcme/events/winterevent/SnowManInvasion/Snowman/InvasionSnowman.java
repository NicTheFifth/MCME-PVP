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
package com.mcmiddleearth.mcme.events.winterevent.SnowManInvasion.Snowman;

import org.bukkit.Location;
import org.bukkit.entity.Snowman;

/**
 *
 * @author Donovan
 */
public class InvasionSnowman{

    private Snowman master;

    public InvasionSnowman(Location spawn){
        master = spawn.getWorld().spawn(spawn, Snowman.class);
    }

    public Snowman getMaster() {
        return master;
    }


}
