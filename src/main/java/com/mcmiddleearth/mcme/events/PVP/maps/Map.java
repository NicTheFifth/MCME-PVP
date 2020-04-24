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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mcmiddleearth.mcme.events.PVP.Gamemode.BasePluginGamemode;
import com.mcmiddleearth.mcme.events.PVP.Gamemode.BasePluginGamemode.GameState;
import com.mcmiddleearth.mcme.events.Util.EventLocation;
import com.mcmiddleearth.mcme.events.PVP.Gamemode.Gamemode;
import com.mcmiddleearth.mcme.events.PVP.Handlers.ChatHandler;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

/**
 *
 * @author Donovan <dallen@dallen.xyz>
 */
public class Map {
    @Getter @Setter
    private int Max;

    @Getter @Setter @JsonIgnore
    private int Curr;

    @Getter @Setter
    private EventLocation LobbySign;
    
    @Getter @Setter @JsonIgnore
    private Gamemode gm;
    
    @Getter @Setter
    private String gmType;
    
    @Getter @Setter
    private EventLocation Spawn;
    
    @Getter @Setter
    private String name;
    
    @Getter @Setter
    private String title;
    
    @Getter @Setter
    private HashMap<String, EventLocation> ImportantPoints = new HashMap<>();
    
    @Setter
    private ArrayList<EventLocation> spawnPoints = new ArrayList<>();
    
    @Getter @Setter
    private ArrayList<EventLocation> regionPoints = new ArrayList<>();
    
    @JsonIgnore @Getter
    private Region region;
    
    @Getter @Setter
    private String resourcePackURL;

    public static HashMap<String, Map> maps = new HashMap<>();
    
    public Map(){}
    
    public Map(Location spawn){
        this.Spawn = new EventLocation(spawn);
    }
    
    public Map(Location spawn, String name){
        this.Spawn = new EventLocation(spawn);
        this.name = name;
        this.title = name;
    }
    
    public void bindSign(SignChangeEvent sign){
        this.LobbySign = new EventLocation(sign.getBlock().getLocation());
        sign.setLine(0, ChatColor.AQUA + "" + ChatColor.BOLD + title);
        if(gmType.equalsIgnoreCase("Team Deathmatch")){
            sign.setLine(1, ChatColor.BLUE + "" + ChatColor.BOLD + "TDM");
        }else{
            sign.setLine(1, ChatColor.BLUE + "" + ChatColor.BOLD + gmType);
        }
        sign.setLine(2, ChatColor.GREEN + "" + ChatColor.BOLD + "" + Curr+"/"+Max);
    }
    
    public void rebindSign(Location signLoc){
        this.LobbySign = new EventLocation(signLoc.getBlock().getLocation());
        Sign sign = (Sign) signLoc.getBlock().getState();
        sign.setLine(0, ChatColor.AQUA + "" + ChatColor.BOLD + title);
        if(gmType.equalsIgnoreCase("Team Deathmatch")){
            sign.setLine(1, ChatColor.BLUE + "" + ChatColor.BOLD + "TDM");
        }else{
            sign.setLine(1, ChatColor.BLUE + "" + ChatColor.BOLD + gmType);
        }
        sign.setLine(2, ChatColor.GREEN + "" + ChatColor.BOLD + "" + Curr+"/"+Max);
    }
    
    public boolean playerJoin(Player p){
        if(Max <= Curr){
            return false;
        }
        
        gm.getPlayers().add(p);
        if(gm.getState() == GameState.IDLE){
            Curr++;
            
            for(Player pl : Bukkit.getOnlinePlayers()){
                pl.sendMessage(ChatColor.GREEN + p.getName() + " Joined!");
            }
            
            try{
                Sign s = (Sign) LobbySign.toBukkitLoc().getBlock().getState();
                s.setLine(2, ChatColor.GREEN + "" + ChatColor.BOLD + "" + Curr+"/"+Max);
                s.update(true, true);
                LobbySign.toBukkitLoc().getBlock().getState().update();
            }catch(NullPointerException e){
                System.err.println("Couldn't find game signs!");
            }catch(ClassCastException e){
                System.err.println("Signs aren't working! Ignoring!");
            }
        }    
        else if(gm.getState() == GameState.RUNNING && gm.midgamePlayerJoin(p)){}
        else{
            p.sendMessage(ChatColor.YELLOW + "Can't join " + gmType + " midgame!");
        }
        return true;
    }
    
    public void playerLeave(Player p){
        ChatHandler.getPlayerPrefixes().remove(p.getName());
        p.setDisplayName(p.getName());
        for(Player pl : gm.getPlayers()){
            pl.sendMessage(p.getName() + " left");
        }
        if(gm instanceof BasePluginGamemode){
            ((BasePluginGamemode) gm).playerLeave(p);
        }else{
            gm.getPlayers().remove(p);
        }
        if(!p.getGameMode().equals(GameMode.SPECTATOR)){
            Curr--;
        }
        
        try{
            Sign s = (Sign) LobbySign.toBukkitLoc().getBlock().getState();
            s.setLine(2, ChatColor.GREEN + "" + ChatColor.BOLD + "" + Curr+"/"+Max);
            s.update(true, true);
            LobbySign.toBukkitLoc().getBlock().getState().update();
            p.getInventory().clear();
            if(Bukkit.getScoreboardManager().getMainScoreboard().getTeam("players").hasPlayer(p)){
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("players").removePlayer(p);
            }
        }
        catch(NullPointerException e){
            System.out.println("Couldn't find game signs!");
        }
        catch(ClassCastException e){
            System.err.println("Signs aren't working! Ignoring!");
        }
    }
    
    public void playerLeaveAll(){
        for(Player p : gm.getPlayers()){
            ChatHandler.getPlayerPrefixes().remove(p.getName());
            p.setDisplayName(p.getName());
            for(Player pl : gm.getPlayers()){
//                pl.sendMessage(p.getName() + " left");
            }
            if(!p.getGameMode().equals(GameMode.SPECTATOR)){
                Curr--;
            }
            
            try{
                Sign s = (Sign) LobbySign.toBukkitLoc().getBlock().getState();
                s.setLine(2, ChatColor.GREEN + "" + ChatColor.BOLD + Curr+"/"+Max);
                s.update(true, true);
                LobbySign.toBukkitLoc().getBlock().getState().update();
            }
            catch(NullPointerException e){
                System.err.println("Couldn't find game sign!");
            }
            catch(ClassCastException e){
                System.err.println("Signs aren't working! Ignoring!");
            }
            
            p.getInventory().clear();
            
        }
        gm.getPlayers().clear();
    }
    
    public void bindGamemode(){
        try {
            Class<?> clazz = Class.forName("com.mcmiddleearth.mcme.events.PVP.Gamemode." + gmType.replace(" ", ""));
            Constructor<?> ctor = clazz.getConstructor();
            gm = (Gamemode) ctor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | 
                SecurityException | InstantiationException | 
                IllegalAccessException | IllegalArgumentException | 
                InvocationTargetException ex) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void initializeRegion(){
        ArrayList<BlockVector2> wePoints = new ArrayList<>();
        World world = Bukkit.getWorld("world");
        
        for(EventLocation e : regionPoints){
            BlockVector2 point = BlockVector2.at(e.getX(), e.getZ());
            wePoints.add(point);
        }
        
        region = new Polygonal2DRegion(new BukkitWorld(world), wePoints, 0, 1000);
        
    }
    
    public static Map findMap(String title, String gamemode){
        for(Map m : maps.values()){
            if(m.getGmType().equalsIgnoreCase(gamemode) && m.getTitle().equalsIgnoreCase(title)){
                return m;
            }
        }
        return null;
    }
    
//    @JsonIgnore
//    public void setRp(String rp){
//        this.rp = rp;
//    }
    
}
