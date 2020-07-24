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
package com.mcmiddleearth.mcme.events.Handlers;

import com.mcmiddleearth.mcme.events.Gamemode.BasePluginGamemode.GameState;
import com.mcmiddleearth.mcme.events.maps.Map;

import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Donovan <dallen@dallen.xyz>
 */
public class CommandBlockHandler implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if(cs instanceof BlockCommandSender && args.length > 1){
            if(args[0].equalsIgnoreCase("cmdBlock")){                      //cmd     0      1       2           3           4
                if(args[1].equalsIgnoreCase("prefix") && args.length >= 4){//pvp cmdblock prefix <player> <prefix word> <prefix color>
                    if(args[3].equalsIgnoreCase("clear")){
                        ChatHandler.getPlayerPrefixes().remove(args[2]);
                    } 
                    else{
                        if(args.length == 5){
                            if(ChatColor.valueOf(args[4]) != null){
                                ChatHandler.getPlayerPrefixes().put(args[2], ChatColor.valueOf(args[4]) + args[3]);
                            }else{
                                cs.sendMessage("not a valid bukkit chat color");
                            }
                        }else{
                            ChatHandler.getPlayerPrefixes().put(args[2], args[3]);
                        }
                    }
                }
                else if(args[1].equalsIgnoreCase("endGame") && args.length >= 3){
                    if(args[2].equalsIgnoreCase("all")){
                        for(Map m : Map.maps.values()){
                            if(m.getGm().getState() == GameState.RUNNING){
                                m.getGm().End(m);
                            }
                        }
                    }
                    else if(Map.maps.containsKey(args[2]) && Map.maps.get(args[2]).getGm().getState() == GameState.RUNNING){
                        Map.maps.get(args[2]).getGm().End(Map.maps.get(args[2]));
                        if(args.length == 4){
                            if(args[3].equalsIgnoreCase("next")){//wip
                                Location sign = Map.maps.get(args[2]).getLobbySign().toBukkitLoc();
                                Map.maps.get(args[2]).setLobbySign(null);
                                Map.maps.get((String) Map.maps.keySet().toArray()[new Random().nextInt(Map.maps.keySet().size()-1)]).rebindSign(sign);
                            }
                        }
                    }
                    else{
                        cs.sendMessage("game cannot be ended");
                    }
                }
            }
        }
        return true;
    }
}
