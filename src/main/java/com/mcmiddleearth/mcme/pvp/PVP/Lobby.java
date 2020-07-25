/*
 * This file is part of MCME-pvp.
 * 
 * MCME-pvp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MCME-pvp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MCME-pvp.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package com.mcmiddleearth.mcme.pvp.PVP;

import com.mcmiddleearth.mcme.pvp.maps.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Donovan <dallen@dallen.xyz>
 */
public class Lobby {
    private static String world;
    
    public Lobby(){}
    
    public static void LoadLobby(){
        for(Map m : Map.maps.values()){
            m.rebindSign(m.getLobbySign().toBukkitLoc());
        }
    }
    
    public static class SignClickListener implements Listener{
        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent e){
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                if(e.getClickedBlock().getState() instanceof Sign){
                    Sign s = (Sign) e.getClickedBlock().getState();
                    String title = s.getLine(0).replace(ChatColor.AQUA + "" + ChatColor.BOLD, "");
                    String gamemode = s.getLine(1).replace(ChatColor.BLUE + "" + ChatColor.BOLD, "");
                    if(gamemode.equalsIgnoreCase("TDM")){
                        gamemode = "Team Deathmatch";
                    }
                    Map m = Map.findMap(title, gamemode);
                    if(m == null){
                        return;
                    }
                    if(!m.getGm().getPlayers().contains(e.getPlayer())){
                        if(m.playerJoin(e.getPlayer())){
                            e.getPlayer().sendMessage(ChatColor.YELLOW + "Joining Map...");
                            Bukkit.broadcastMessage(ChatColor.YELLOW + e.getPlayer().getName() + " Joined " + m.getTitle() + " playing " + m.getGmType());
                        }else{
                            e.getPlayer().sendMessage(ChatColor.RED + "Failed to Join Map");
                        }
                    }else{
                        e.getPlayer().sendMessage(ChatColor.RED + "You are already part of a game");
                    }
                }
            }
        }
        
        @EventHandler
        public void onSignChange(SignChangeEvent e){
            if(e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()){
                ItemMeta im = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
                if(im.hasLore()){
                    if(Map.findMap(im.getLore().get(0), im.getLore().get(1)) != null){
                        final Map m = Map.findMap(im.getLore().get(0), im.getLore().get(1));
                        m.bindSign(e);
                        world = e.getBlock().getLocation().getWorld().getName();
                    }
                }
            }
        }
    }

    public static String getWorld() {
        return world;
    }

    public static void setWorld(String world) {
        Lobby.world = world;
    }
}
