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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mcmiddleearth.mcme.events.PVPPlugin;
import com.mcmiddleearth.mcme.events.Gamemode.BasePluginGamemode;
import com.mcmiddleearth.mcme.events.Gamemode.BasePluginGamemode.GameState;
import com.mcmiddleearth.mcme.events.Gamemode.anticheat.AntiCheatListeners;
import com.mcmiddleearth.mcme.events.command.PVPCommand;
import com.mcmiddleearth.mcme.events.maps.Map;
import com.mcmiddleearth.mcme.events.PVP.PlayerStat;
import com.mcmiddleearth.mcme.events.PVP.Team;
import com.mcmiddleearth.mcme.events.Util.DBmanager;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan <dallen@dallen.xyz>
 */
public class JoinLeaveHandler implements Listener{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        final Player p = e.getPlayer();
        PlayerStat.loadStat(p);
        
        if(PVPCommand.getRunningGame() != null){
            e.setJoinMessage(ChatColor.GRAY + e.getPlayer().getName() + " has joined as a spectator! \n To join the game type /pvp join.");
        }
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(PVPPlugin.getPlugin(), new Runnable(){
            @Override
            public void run(){
                
                if(!p.isDead()){
                    p.setHealth(20);
                }
                
                p.setTotalExperience(0);
                p.setExp(0);
                p.setGameMode(GameMode.ADVENTURE);
                p.getInventory().clear();
                p.setPlayerListName(ChatColor.WHITE + p.getName());
                p.setDisplayName(ChatColor.WHITE + p.getName());
                p.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR),
                    new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
                try {
                    System.out.println(DBmanager.getJSonParser().writeValueAsString(PlayerStat.getPlayerStats()));
                } catch (JsonProcessingException ex) {
                    Logger.getLogger(JoinLeaveHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ArrayList<String> nullPointerColors = new ArrayList<>();
                for(String playerName : ChatHandler.getPlayerColors().keySet()){
                    try{
                        Bukkit.getPlayer(playerName).setPlayerListName(ChatHandler.getPlayerColors().get(playerName) + playerName);
                        Bukkit.getPlayer(playerName).setDisplayName(ChatHandler.getPlayerColors().get(playerName) + playerName);
                    }
                    catch(NullPointerException e){
                        nullPointerColors.add(playerName);
                    }
                }
                
                for(String s : nullPointerColors){
                    ChatHandler.getPlayerColors().remove(s);
                }
                
                if(PVPCommand.getRunningGame() == null && PVPCommand.getNextGame() == null){
                    p.teleport(PVPPlugin.getSpawn());
                    //p.setResourcePack("http://www.mcmiddleearth.com/content/Eriador.zip");
                    ChatHandler.getPlayerColors().put(p.getName(), org.bukkit.ChatColor.WHITE);
                }else{
                    Map m = null;
                    if(PVPCommand.getNextGame() != null){
                        m = PVPCommand.getNextGame();
                    }
                    else if(PVPCommand.getRunningGame() != null){
                        m = PVPCommand.getRunningGame();
                    }
                    else{
                        return;
                    }
                          
                    if(m.getGm().getState() != GameState.IDLE){
                        p.teleport(m.getSpawn().toBukkitLoc().add(0, 2, 0));
                        
                        /*try{
                            p.setResourcePack(m.getResourcePackURL());
                        }
                        catch(NullPointerException e){
                            p.sendMessage(org.bukkit.ChatColor.RED + "No resource pack was set for this map!");
                        }*/
                        
                        p.setScoreboard(BasePluginGamemode.getScoreboard());
                        p.sendMessage(ChatColor.GREEN + "Current Game: " + ChatColor.BLUE + m.getGmType() + ChatColor.GREEN + " on " + ChatColor.RED + m.getTitle());
                          
                        if(m.getGm().isMidgameJoin()){
                            p.sendMessage(ChatColor.YELLOW + "Use /pvp join to join the game!");
                        }else{
                            p.sendMessage(ChatColor.YELLOW + "Sorry, you can't join this game midgame");
                            p.sendMessage(ChatColor.YELLOW + "You can join the next game, though!");
                        }
                        Team.getSpectator().add(p);
                    }
                    else{
                        p.teleport(PVPPlugin.getSpawn());
                        //p.setResourcePack("http://www.mcmiddleearth.com/content/Eriador.zip");
                        p.sendMessage(ChatColor.GREEN + "Upcoming Game: " + ChatColor.BLUE + m.getGmType() + ChatColor.GREEN + " on " + ChatColor.RED + m.getTitle());
                        p.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.GREEN + "/pvp join" + ChatColor.YELLOW + " to join!");
                        p.sendMessage(ChatColor.GREEN + "Do /pvp rules " + PVPCommand.removeSpaces(PVPCommand.getNextGame().getGmType()) + " if you don't know how this gamemode works!");
                    }
                }
            }
        }, 20);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        e.setQuitMessage(handlePlayerQuit(e.getPlayer()));
    }
    
    public static String handlePlayerQuit(Player player) {
        String quitMessage = "";
        if(PVPCommand.getNextGame() != null){
            PVPCommand.getNextGame().getGm().getPlayers().remove(player);
        }
        else if(PVPCommand.getRunningGame() != null){
            PVPCommand.getRunningGame().getGm().getPlayers().remove(player);
        }
        
        if(PlayerStat.getPlayerStats().get(player.getName()) != null){
            PlayerStat.getPlayerStats().get(player.getName()).saveStat();
            PlayerStat.getPlayerStats().remove(player.getName());
        }
        //Thompson.getInst().farwell(player);
        
        if(PVPCommand.getRunningGame() != null){
            quitMessage = ChatHandler.getPlayerColors().get(player.getName()) + player.getName() + ChatColor.GRAY + " left the fight!";
        }
        
        AntiCheatListeners.getLastInteract().remove(player.getName());
        
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR),
            new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
        
        Team.removeFromTeam(player);
        ChatHandler.getPlayerColors().remove(player.getName());
        ChatHandler.getPlayerPrefixes().remove(player.getName());
        return quitMessage;
    }
}
