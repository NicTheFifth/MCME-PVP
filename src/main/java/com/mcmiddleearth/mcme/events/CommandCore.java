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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan
 */
public class CommandCore implements TabExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(sender instanceof Player){
            if(cmd.getName().equalsIgnoreCase("world")){
                Player p = (Player) sender;
                if(args.length>1){
                    if(args[0].equalsIgnoreCase("save")&&p.hasPermission("event.saveworld")){
                        WorldSave.saveWorld(p.getWorld(), args[1], p);
                    }else if(args[0].equalsIgnoreCase("load")&&p.hasPermission("event.loadworld")){
                        WorldSave.loadWorld(p.getWorld(), args[1]);
                    }
                    return true;
                }else if(args.length>0){
                    if(args[0].equalsIgnoreCase("list")){
                        p.sendMessage("Loaded Worlds:");
                        for(World w : Bukkit.getWorlds()){
                            p.sendMessage(w.getName());
                        }
                        if(p.hasPermission("event.loadworld")){
                            p.sendMessage("World Backups");
                            for(File f: PVPPlugin.getPlugin().getDataFolder().listFiles()){
                                p.sendMessage(f.getName());
                            }
                        }
                        return true;
                    }
                }
            }else if(cmd.getName().equalsIgnoreCase("worldjump")){
                World spawn = Bukkit.getWorld(PVPPlugin.getPlugin().getSpawnWorld());
                Player p = (Player) sender;
                if(args.length>0&&p.hasPermission("event.jump")){
                    if(!p.getWorld().equals(spawn)&&!args[0].equalsIgnoreCase(spawn.getName())){
                        p.sendMessage("you must be in spawn to jump");
                        p.sendMessage("use /worldjump "+ spawn.getName());
                        return true;
                    }
                    World w = Bukkit.getWorld(args[0]);
                    if(w == null){
                        p.sendMessage(args[0] + " is not a loaded world");
                    } else {
                        p.sendMessage("Welcome to " + ChatColor.BLUE + w.getName());
                            Inventory i = p.getInventory();
                            i.clear();
                            p.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
                            p.getInventory().setContents(i.getContents());
                            if(w.equals(spawn)){
                                p.setGameMode(GameMode.ADVENTURE);
                            }
                        p.teleport(w.getSpawnLocation());
                    }
                    return true;
                }
            }else if(cmd.getName().equalsIgnoreCase("plugup")){
                if(sender.isOp()){
                    //new WebHook().update(sender);
                    return true;
                }else{
                    return false;
                }
            }
        }else if(cmd.getName().equalsIgnoreCase("plugup")){
            if(sender.isOp()){
                //new WebHook().update(sender);
                return true;
            }else{
                return false;
            }
        }else{
            sender.sendMessage("No console");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("worldjump")){
            List<String> rtn = new ArrayList<>();
            for(World w : Bukkit.getWorlds()){
                rtn.add(w.getName());
            }
            return rtn;
        }else if(cmd.getName().equalsIgnoreCase("world")){
            if(args.length == 0){
                return Arrays.asList(new String[] {"save", "load", "list"});
            }else{
                if(args[0].equalsIgnoreCase("save") || args[0].equalsIgnoreCase("load")){
                    List<String> rtn = new ArrayList<>();
                    for(World w : Bukkit.getWorlds()){
                        rtn.add(w.getName());
                    }
                    return rtn;
                }
            }
        }
        return null;
    }
    
}
