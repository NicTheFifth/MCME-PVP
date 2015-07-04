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
package com.mcmiddleearth.mcme.events.PVP;

import com.mcmiddleearth.mcme.events.PVP.Handlers.CommandBlockHandler;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Donovan <dallen@dallen.xyz>
 */
public class PVPCommandCore implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if(cs instanceof Player){
            if(args.length > 1){
                Player p = (Player) cs;
                if(args[0].equalsIgnoreCase("leave") && 
                        PVPCore.getPlaying().keySet().contains((p).getName())){
                    Map m = Map.maps.get(PVPCore.getPlaying().get(p.getName()));
                    m.playerLeave(p);
                    return true;
                }
            }
            return new MapEditor().onCommand(cs, cmnd, label, args);
        }else if(cs instanceof BlockCommandSender){
            return new CommandBlockHandler().onCommand(cs, cmnd, label, args);
        }
        return false;
    }
}