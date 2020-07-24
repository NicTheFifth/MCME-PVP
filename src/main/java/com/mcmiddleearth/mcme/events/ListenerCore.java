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
package com.mcmiddleearth.mcme.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 *
 * @author Donovan
 */
public class ListenerCore implements Listener{
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(PVPPlugin.isBlockprotect()){
            if(e.getPlayer().getWorld().getName().equals(PVPPlugin.getPlugin().getSpawnWorld())){
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        if(PVPPlugin.isBlockprotect()){
            if(e.getPlayer().getWorld().getName().equals(PVPPlugin.getPlugin().getSpawnWorld())){
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e){
//        World pw = e.getEntity().getWorld();
//        if(Main.getPlugin().getNoHunger().contains(pw.getName())){
            e.setFoodLevel(20);
//        }
    }
}
