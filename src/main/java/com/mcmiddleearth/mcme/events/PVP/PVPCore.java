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

import com.mcmiddleearth.mcme.events.PVP.maps.MapEditor;
import com.mcmiddleearth.mcme.events.PVP.maps.Map;
import com.mcmiddleearth.mcme.events.Event;
import com.mcmiddleearth.mcme.events.Main;
import com.mcmiddleearth.mcme.events.PVP.Gamemode.anticheat.AntiCheatListeners;
import com.mcmiddleearth.mcme.events.PVP.Handlers.AllGameHandlers;
import com.mcmiddleearth.mcme.events.PVP.Handlers.BukkitTeamHandler;
import com.mcmiddleearth.mcme.events.PVP.Handlers.ChatHandler;
import com.mcmiddleearth.mcme.events.PVP.Handlers.GearHandler.GearEvents;
import com.mcmiddleearth.mcme.events.PVP.Handlers.JoinLeaveHandler;
import com.mcmiddleearth.mcme.events.PVP.Handlers.WeatherHandler;
import com.mcmiddleearth.mcme.events.Util.CLog;
import com.mcmiddleearth.mcme.events.Util.DBmanager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author Donovan <dallen@dallen.xyz>
 */
public class PVPCore implements Event{
    
    @Getter
    private static File saveLoc = new File(Main.getPluginDirectory() + Main.getFileSep() + "PVP");
    
    @Getter
    private static Location Spawn;
    
    @Getter
    private static int countdownTime = 5;
    
    
    @Override
    public void onEnable(){
        File loc = new File(saveLoc + Main.getFileSep() + "Maps");
        HashMap<String, Object> maps = new HashMap<>();
        try{
            maps = DBmanager.loadAllObj(Map.class, loc);
        }
        catch(Exception ex){
        }
        if(maps == null){
            maps = new HashMap<>();
        }
        for(Entry<String, Object> e : maps.entrySet()){
            try{
                Map m = (Map) e.getValue();
                m.setCurr(0);
                if(m.getGmType() != null){
                    m.bindGamemode();
                }
                if(m.getRegionPoints().size() > 0){
                    m.initializeRegion();
                }
                
                Map.maps.put(e.getKey(), m);
                
                
            }
            catch(Exception ex){
                System.out.println("Error loading map " + e.getKey());
                
            }
        }
        CLog.println(maps);
        Main.getPlugin().getCommand("pvp").setExecutor(new PVPCommandCore());
        Main.getPlugin().getCommand("locker").setExecutor(new Locker());
        Main.getPlugin().getCommand("t").setExecutor(new TeamChat());
        PluginManager pm = Main.getServerInstance().getPluginManager();
        pm.registerEvents(new MapEditor(), Main.getPlugin());
//        pm.registerEvents(new PlayerStat.StatLitener(), Main.getPlugin());
        pm.registerEvents(new Lobby.SignClickListener(), Main.getPlugin());
        pm.registerEvents(new ChatHandler(), Main.getPlugin());
        pm.registerEvents(new Locker(), Main.getPlugin());
        pm.registerEvents(new JoinLeaveHandler(), Main.getPlugin());
        pm.registerEvents(new AllGameHandlers(), Main.getPlugin());
        pm.registerEvents(new PlayerStat.StatListener(), Main.getPlugin());
        pm.registerEvents(new GearEvents(), Main.getPlugin());
        pm.registerEvents(new AntiCheatListeners(), Main.getPlugin());
        pm.registerEvents(new WeatherHandler(), Main.getPlugin());
        BukkitTeamHandler.configureBukkitTeams();
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){

            @Override
            public void run() {
                Spawn = new Location(Bukkit.getWorld("world"), 344.47, 39, 521.58, 0.3F, -24.15F);
                        
                }
            }, 20);
        
    }
    
    @Override
    public void onDisable(){
        for(String mn : Map.maps.keySet()){
            Map m = Map.maps.get(mn);
            m.setCurr(0);
            DBmanager.saveObj(m, new File(saveLoc + Main.getFileSep() + "Maps"), mn);
        }
    }
    
    public static WorldEditPlugin getWorldEditPlugin(){
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        
        if(p == null){
            return null;
        }
        return (WorldEditPlugin) p;
    }
}
