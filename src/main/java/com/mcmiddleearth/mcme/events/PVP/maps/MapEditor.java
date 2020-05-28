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
package com.mcmiddleearth.mcme.events.PVP.maps;

import com.mcmiddleearth.mcme.events.PVP.Gamemode.FreeForAll;
import com.mcmiddleearth.mcme.events.PVP.Gamemode.Infected;
import com.mcmiddleearth.mcme.events.Util.EventLocation;
import com.mcmiddleearth.mcme.events.PVP.Gamemode.KingOfTheHill;
import com.mcmiddleearth.mcme.events.PVP.Gamemode.OneInTheQuiver;
import com.mcmiddleearth.mcme.events.PVP.Gamemode.Ringbearer;
import com.mcmiddleearth.mcme.events.PVP.Gamemode.TeamConquest;
import com.mcmiddleearth.mcme.events.PVP.Gamemode.TeamDeathmatch;

import com.mcmiddleearth.mcme.events.PVP.Gamemode.TeamSlayer;
import com.mcmiddleearth.mcme.events.PVP.PVPCore;
import com.mcmiddleearth.mcme.events.Permissions;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.Region;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author Donovan <dallen@dallen.xyz>
 */
public class MapEditor implements CommandExecutor, Listener{
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if(cs instanceof Player){
            Player p = (Player) cs;
            if(cmnd.getName().equalsIgnoreCase("pvp") && args.length > 0){
                if(args[0].equalsIgnoreCase("lobby")){
                    p.sendMessage(ChatColor.GREEN + "Sending Signs");
                    for(Map m : Map.maps.values()){
                        ItemStack sign = new ItemStack(Material.OAK_SIGN);
                        ItemMeta im = sign.getItemMeta();
                        im.setDisplayName(m.getName());
                        String gamemode = "none";
                        if(m.getGm() != null){
                            gamemode = m.getGmType();
                        }
                        im.setLore(Arrays.asList(new String[] {m.getTitle(), gamemode, String.valueOf(m.getMax())}));
                        sign.setItemMeta(im);
                        p.getInventory().addItem(sign);
                    }
                }
                else if(args[0].equalsIgnoreCase("map")){
                    if(args.length > 2){
                        if(Map.maps.containsKey(args[1])){
                            Map m = Map.maps.get(args[1]);
                            if(args[2].equalsIgnoreCase("spawn")){
                                m.setSpawn(new EventLocation(p.getLocation()));
                                p.sendMessage(ChatColor.GREEN + "Map spawn set");
                            }
                            else if(args[2].equalsIgnoreCase("poi") && args.length > 3){
                                if(m.getGm() == null){
                                    p.sendMessage(ChatColor.YELLOW + "WARNING: no gamemode has been set yet!");
                                }
                                else if(m.getGm() instanceof FreeForAll || m.getGm() instanceof OneInTheQuiver){
                                    p.sendMessage(ChatColor.GREEN + String.valueOf(m.getImportantPoints().size()) + " spawn points configured!");
                                }
                                else if(!m.getGm().getNeededPoints().contains(args[3])){
                                    p.sendMessage(ChatColor.YELLOW + "WARNING: That is not a poi for this maps gamemode");
                                }
                                m.getImportantPoints().put(args[3], new EventLocation(p.getLocation().add(0, -1, 0)));
                                EventLocation el = new EventLocation(p.getLocation().add(0, -1, 0));
                                p.sendMessage(ChatColor.YELLOW + args[3] + " set to (" +  el.getX() + ", " + el.getY() + ", " + el.getZ() + ")");
                            }
                            else if(args[2].equalsIgnoreCase("setMax") && args.length > 3){
                                m.setMax(Integer.parseInt(args[3]));
                                p.sendMessage(ChatColor.GREEN + "Max players set to " + args[3]);
                            }
                            else if(args[2].equalsIgnoreCase("setTitle") && args.length > 3){
                                m.setTitle(args[3]);
                                p.sendMessage(ChatColor.GREEN + "map Title set to " + args[3]);
                            }
                            else if(args[2].equalsIgnoreCase("setGamemode") && args.length > 3){
                                boolean real = false;
                                if(args[3].equalsIgnoreCase("Freeforall")){
                                    m.setGm(new FreeForAll());
                                    m.setGmType("Free For All");
                                    real = true;
                                }
                                else if(args[3].equalsIgnoreCase("Infected")){
                                    m.setGm(new Infected());
                                    m.setGmType("Infected");
                                    real = true;
                                }
                                else if(args[3].equalsIgnoreCase("oneinthequiver")){
                                    m.setGm(new OneInTheQuiver());
                                    m.setGmType("One In The Quiver");
                                    real = true;
                                }
                                else if(args[3].equalsIgnoreCase("Ringbearer")){
                                    m.setGm(new Ringbearer());
                                    m.setGmType("Ringbearer");
                                    real = true;
                                }
                                else if(args[3].equalsIgnoreCase("TeamConquest")){
                                    m.setGm(new TeamConquest());
                                    m.setGmType("Team Conquest");
                                    real = true;
                                }
                                else if(args[3].equalsIgnoreCase("kingofthehill")){
                                    m.setGm(new KingOfTheHill());
                                    m.setGmType("King Of The Hill");
                                    real = true;
                                }
                                else if(args[3].equalsIgnoreCase("TeamDeathmatch")){
                                    m.setGm(new TeamDeathmatch());
                                    m.setGmType("Team Deathmatch");
                                    real = true;
                                }
                                else if(args[3].equalsIgnoreCase("TeamSlayer")){
                                    m.setGm(new TeamSlayer());
                                    m.setGmType("Team Slayer");
                                    real = true;
                                }/*else if(args[3].equalsIgnoreCase("Siege")){
                                    m.setGm(new Siege());
                                    m.setGmType("Siege");
                                    p.sendMessage(ChatColor.RED + "Warning, Seige is a very large gamemode that does not fit regular gamemode standards");
                                    real = true;
                                }*/
                                if(real){
                                    p.sendMessage(ChatColor.GREEN + "Gamemode set to " + args[3]);
                                    if(!m.getImportantPoints().keySet().containsAll(m.getGm().getNeededPoints())){
                                        p.sendMessage(ChatColor.YELLOW + "WARNING: The following points are not set: [");
                                        for(String s : m.getGm().getNeededPoints()){
                                            if(!m.getImportantPoints().containsKey(s)){
                                                p.sendMessage(ChatColor.YELLOW + " - " + s);
                                            }
                                        }
                                        p.sendMessage("]");
                                    }
                                }else{
                                    p.sendMessage(ChatColor.RED+ "No such gamemode " + args[3]);
                                }
                            }
                            else if(args[2].equalsIgnoreCase("setarea")){
                                BukkitPlayer bukkitP = new BukkitPlayer(p);//PVPCore.getWorldEditPlugin(), PVPCore.getWorldEditPlugin().getServerInterface(), p);
                                LocalSession session = PVPCore.getWorldEditPlugin().getWorldEdit().getSessionManager().get(bukkitP);
                                
                                try{
                                    Region r = session.getSelection(new BukkitWorld(p.getWorld()));
                                    if(r.getHeight() < 250){
                                        p.sendMessage(ChatColor.RED + "I think you forgot to do //expand vert!");
                                    }
                                    else{
                                        List<BlockVector2> wePoints = r.polygonize(1000);
                                        ArrayList<EventLocation> bPoints = new ArrayList<>();
                                        
                                        for(BlockVector2 point : wePoints){
                                            bPoints.add(new EventLocation(new Location(p.getWorld(), point.getX(), 1, point.getZ())));
                                        }
                                        
                                        m.setRegionPoints(bPoints);
                                        m.initializeRegion();
                                        p.sendMessage(ChatColor.YELLOW + "Area set!");
                                    }
                                }
                                catch(IncompleteRegionException e){
                                    p.sendMessage(ChatColor.RED + "You don't have a region selected!");
                                }
                            }
                            else if(args[2].equalsIgnoreCase("setrp")){
                                String rp = args[3].toLowerCase();
                                
                                switch(rp){
                                    case "eriador":
                                        m.setResourcePackURL("http://www.mcmiddleearth.com/content/Eriador.zip");
                                        break;
                                    case "rohan":
                                        m.setResourcePackURL("http://www.mcmiddleearth.com/content/Rohan.zip");
                                        break;
                                    case "lothlorien":
                                        m.setResourcePackURL("http://www.mcmiddleearth.com/content/Lothlorien.zip");
                                        break;
                                    case "gondor":
                                        m.setResourcePackURL("http://www.mcmiddleearth.com/content/Gondor.zip");
                                        break;
                                    case "dwarven":
                                        m.setResourcePackURL("http://www.mcmiddleearth.com/content/Moria.zip");
                                        break;
                                    case "moria":
                                        m.setResourcePackURL("http://www.mcmiddleearth.com/content/Moria.zip");
                                        break;
                                    case "mordor":
                                        m.setResourcePackURL("http://www.mcmiddleearth.com/content/Mordor.zip");
                                        break;
                                    default:
                                        p.sendMessage(ChatColor.RED + "RP: " + rp + " is not a valid resource pack!");
                                        p.sendMessage(ChatColor.RED + "Valid packs are:");
                                        p.sendMessage(ChatColor.GRAY + "    Eriador");
                                        p.sendMessage(ChatColor.GRAY + "    Lothlorien");
                                        p.sendMessage(ChatColor.GRAY + "    Rohan");
                                        p.sendMessage(ChatColor.GRAY + "    Gondor");
                                        p.sendMessage(ChatColor.GRAY + "    Moria");
                                        p.sendMessage(ChatColor.GRAY + "    Mordor");
                                        return true;
                                }
                                p.sendMessage(ChatColor.YELLOW + "Resource pack set to " + rp);
                            }
                        }
                        else{
                            if(args[2].equalsIgnoreCase("spawn")){
                                p.sendMessage(ChatColor.GREEN + "Creating new map");
                                Map.maps.put(args[1], new Map(p.getLocation(), args[1]));
                                System.out.println(args[1]);
                            }
                        }
                    }
                    else{
                        if(args.length > 1){
                            if(args[1].equalsIgnoreCase("list")){
                                p.sendMessage(ChatColor.YELLOW + "Maps: [");
                                for(Map m : Map.maps.values()){
                                    p.sendMessage(ChatColor.YELLOW + m.getName() + " - " + m.getTitle() + ", " + m.getGmType());
                                }
                                p.sendMessage(ChatColor.YELLOW + "]");
                            }
                        }
                        }
                    }
                }else if(args[0].equalsIgnoreCase("pastemap")){
                    if(p.hasPermission(Permissions.PVP_ADMIN.getPermissionNode())){
                        
                        if(args.length >= 7){
                            Location startLoc = new Location(p.getWorld(), Double.valueOf(args[1]), Double.valueOf(args[2]), Double.valueOf(args[3])); 
                            Location endLoc = new Location(p.getWorld(), Double.valueOf(args[4]), Double.valueOf(args[5]), Double.valueOf(args[6]));
                        }
                        else{
                            p.sendMessage(ChatColor.RED + "Incorrect syntax! See DSE's post on pasting maps.");
                        }                                                           
                        
                    }
                    else{
                        p.sendMessage(ChatColor.RED + "Only staff can paste maps!");
                    }
                }
            }
        return true;
    }
    
}
