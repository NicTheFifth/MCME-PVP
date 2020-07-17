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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.mcmiddleearth.mcme.events.PVP.Handlers.ChatHandler;
import com.mcmiddleearth.mcme.events.PVP.PVPCommandCore;
import com.mcmiddleearth.mcme.events.PVP.PVPCore;
import com.mcmiddleearth.mcme.events.PVP.command.PVPCommand;
import com.mcmiddleearth.mcme.events.Util.CLog;
import com.mcmiddleearth.mcme.events.summerevent.SummerCommands;
import com.mcmiddleearth.mcme.events.summerevent.SummerCore;
import com.mcmiddleearth.mcme.events.winterevent.SnowManInvasion.EventHandles.SignListener;
import com.mcmiddleearth.mcme.events.winterevent.SnowManInvasion.EventHandles.SnowballHandle;
import com.mcmiddleearth.mcme.events.winterevent.SnowballFight.listeners.SnowballListener;
import com.mcmiddleearth.mcme.events.winterevent.WinterCommands;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Donovan
 */
public class Main extends JavaPlugin{
    
    private static boolean debug = true;
    private static SummerCore summerCore = new SummerCore();
    private static PVPCore PVPCore;
    private static Main plugin;
    private String spawnWorld;
    private static String FileSep = System.getProperty("file.separator");
    private ArrayList<String> noHunger = new ArrayList<String>();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Server serverInstance;
    private static File pluginDirectory;
    private static File playerDirectory;
    private static boolean blockprotect = false;
    private static Integer minutes_broadcast;

    @Override
    public void onEnable(){
        plugin = this;
        this.saveDefaultConfig();
        this.reloadConfig();
        if(this.getConfig().contains("worlds")){
            for(String s : this.getConfig().getStringList("worlds")){
                Bukkit.getServer().getWorlds().add(Bukkit.getServer().createWorld(new WorldCreator(s)));
            }
        }
        if(this.getConfig().contains("noHunger")){
            noHunger.addAll(this.getConfig().getStringList("noHunger"));
        }
        try {
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.serverInstance = getServer();
        this.pluginDirectory = getDataFolder();
        CLog.println(pluginDirectory.getPath());
        if (!pluginDirectory.exists()){
            pluginDirectory.mkdir();
        }
        this.playerDirectory = new File(pluginDirectory + System.getProperty("file.separator") + "players");
        if (!playerDirectory.exists()){
            playerDirectory.mkdir();
        }
//        Thompson t = new Thompson(this);
        this.getCommand("WorldJump").setExecutor(new CommandCore());
        this.getCommand("World").setExecutor(new CommandCore());
        this.getCommand("PlugUp").setExecutor(new CommandCore());
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new ListenerCore(), this);
        boolean PVP = this.getConfig().getBoolean("PVP.Enabled");
        minutes_broadcast = this.getConfig().getInt("PVP.Broadcast_minutes");
        if(PVP){
            PVPCore = new PVPCore();
            PVPCore.onEnable();
        }
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderAPI.registerPlaceholderHook("mcmePvP", new ChatHandler());
        } else {
            Logger.getGlobal().warning("PlaceholderAPI not enabled");
        }
        boolean Winter = this.getConfig().getBoolean("WinterEvent.Enabled");
        boolean Summer = this.getConfig().getBoolean("SummerEvent.Enabled");
        if(Summer){
            summerCore.onEnable();
        }
        if(Winter && Summer){
            this.getCommand("winter").setExecutor(new WinterCommands());
            this.getCommand("summer").setExecutor(new SummerCommands());
            registerHandles(true, pm);
            registerHandles(false, pm);
        }else{
            if(Winter){
                this.getCommand("winter").setExecutor(new WinterCommands());
                this.getCommand("event").setExecutor(new WinterCommands());
                registerHandles(false, pm);
            }else if(Summer){
                this.getCommand("summer").setExecutor(new SummerCommands());
                this.getCommand("event").setExecutor(new SummerCommands());
                registerHandles(true, pm);
            }
        }
    }

    @Override
    public void onDisable(){
        boolean Summer = this.getConfig().getBoolean("SummerEvent.Enabled");
        if(Summer){
            summerCore.onDisable();
        }
        PVPCore.onDisable();
    }
    
    private void registerHandles(boolean summer, PluginManager pm){
        if(summer){
            pm.registerEvents(new SnowballListener(), this);
        }else{
            pm.registerEvents(new SnowballListener(), this);
            pm.registerEvents(new SignListener(), this);
            pm.registerEvents(new SnowballHandle(), this);
        }
    }

    public static boolean isDebug() {
        return debug;
    }

    public static SummerCore getSummerCore() {
        return summerCore;
    }

    public static com.mcmiddleearth.mcme.events.PVP.PVPCore getPVPCore() {
        return PVPCore;
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static Integer getMinutes() {
        return minutes_broadcast;
    }

    public String getSpawnWorld() {
        return spawnWorld;
    }

    public static String getFileSep() {
        return FileSep;
    }

    public ArrayList<String> getNoHunger() {
        return noHunger;
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static Server getServerInstance() {
        return serverInstance;
    }

    public static File getPluginDirectory() {
        return pluginDirectory;
    }

    public static File getPlayerDirectory() {
        return playerDirectory;
    }

    public static boolean isBlockprotect() {
        return blockprotect;
    }
}
