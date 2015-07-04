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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mcmiddleearth.mcme.events.Main;
import com.mcmiddleearth.mcme.events.Util.EventLocation;
import com.mcmiddleearth.mcme.events.PVP.Gamemode.Gamemode;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
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
    private HashMap<String, EventLocation> ImportantPoints = new HashMap<>();
    
    @Getter @Setter
    private ArrayList<EventLocation> spawnPoints = new ArrayList<>();

    public static HashMap<String, Map> maps = new HashMap<>();
    
    public Map(){}
    
    public Map(Location spawn){
        this.Spawn = new EventLocation(spawn);
    }
    
    public Map(Location spawn, String name){
        this.Spawn = new EventLocation(spawn);
        this.name = name;
    }
    
    public void bindSign(SignChangeEvent sign){
        this.LobbySign = new EventLocation(sign.getBlock().getLocation());
        sign.setLine(0, name);
        sign.setLine(1, gmType);
        sign.setLine(2, Curr+"/"+Max);
    }
    
    public boolean playerJoin(Player p){
        if(Max <= Curr){
            return false;
        }
        p.teleport(Spawn.toBukkitLoc());
        gm.getPlayers().add(p);
        Curr++;
        PVPCore.getPlaying().put(p.getName(), name);
        Sign s = (Sign) LobbySign.toBukkitLoc().getBlock().getState();
        s.setLine(2, Curr+"/"+Max);
        s.update(true, true);
        LobbySign.toBukkitLoc().getBlock().getState().update();
        if(Max == Curr){
            gm.Start(this);
        }
        return true;
    }
    
    public void playerLeave(Player p){
        for(Player pl : gm.getPlayers()){
            pl.sendMessage(p.getName() + " left");
        }
        gm.getPlayers().remove(p);
        Curr--;
        PVPCore.getPlaying().remove(p.getName());
        Sign s = (Sign) LobbySign.toBukkitLoc().getBlock().getState();
        s.setLine(2, Curr+"/"+Max);
        s.update(true, true);
        LobbySign.toBukkitLoc().getBlock().getState().update();
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
}