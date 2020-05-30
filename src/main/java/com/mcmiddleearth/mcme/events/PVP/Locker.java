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

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.mcme.events.Main;
import com.mcmiddleearth.mcme.events.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Donovan <dallen@dallen.xyz>
 */
public class Locker implements CommandExecutor, TabCompleter, Listener{
    
    private static volatile boolean locked = true;
    
    private static String Message = "PvP-server Locked";
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if(cs.hasPermission(Permissions.LOCKER.getPermissionNode())){
            if(args.length > 0){
                if(args[0].equalsIgnoreCase("kickall")){
                    for(Player p : Bukkit.getOnlinePlayers()){
                        if(!p.hasPermission(Permissions.LOCKER.getPermissionNode())){
                            //p.kickPlayer("PvP manager kicked all players");
                            p.sendMessage("A PvP manager kicked all players");
                            sendPlayerToMain(p);
                        }
                    }
                    cs.sendMessage("Kicked all!");
                }
                else if(args[0].equalsIgnoreCase("lock")){
                    if(args.length > 1){
                        Message = "";
                        for(int i = 1; i < args.length; i++){
                            Message = Message.concat(args[i] + " ");
                        }
                    }
                    if(locked){
                        cs.sendMessage("Server Unlocked!");
                        locked = false;
                    }
                    else{
                        cs.sendMessage("Server Locked!");
                        locked = true;
                        Message = "Server Locked!";
                        for(Player p : Bukkit.getOnlinePlayers()){
                            if(!p.hasPermission(Permissions.LOCKER.getPermissionNode())){
                                //p.kickPlayer("Server locked");
                                p.sendMessage(Message);
                                sendPlayerToMain(p);
                            }
                        }
                    }
                }
            }
        } else {
            cs.sendMessage(ChatColor.RED + "You don't have the permission to lock the server!");
        }
        return true;
    }
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        List<String> arguments = new ArrayList<>();
        List<String> Flist = new ArrayList<>();
        Player p = (Player) cs;
        if (
        cmd.getName().equalsIgnoreCase("locker") && ((p.hasPermission(Permissions.PVP_MANAGER.getPermissionNode())) || (p.
        hasPermission(Permissions.PVP_ADMIN.getPermissionNode())))){
            arguments.add("lock");
            arguments.add("kickall");
        }
        if (args.length == 1) {
            for (String s : arguments) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    Flist.add(s);
                }
            }
            return Flist;
        } else {
            return null;
        }
    }


    @EventHandler (priority = EventPriority.HIGHEST)
    public void onServerListPing(ServerListPingEvent e){
        if(locked){
            e.setMotd(e.getMotd() + "\n" + ChatColor.BLUE + Message);
            e.setMaxPlayers(0);
        }
    }
    
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(Bukkit.getOnlinePlayers().isEmpty()
                || Bukkit.getOnlinePlayers().stream().allMatch(player -> player.equals(event.getPlayer()))) {
            locked = true;
        }
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent e){
///Logger.getGlobal().info("Player login: "  +locked+ " "+e.getPlayer().hasPermission(Permissions.LOCKER.getPermissionNode()));
//Logger.getGlobal().info("Player allowed: "+(locked && !e.getPlayer().hasPermission(Permissions.LOCKER.getPermissionNode())));
        if(locked && !e.getPlayer().hasPermission(Permissions.LOCKER.getPermissionNode())){
///Logger.getGlobal().info("Player kick! ");
            e.getPlayer().sendMessage(Message);
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendPlayerToMain(e.getPlayer());
                }
            }.runTaskLater(Main.getPlugin(),1);
            //e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.BLUE + Message);
        }
    }
    
    
    private void sendPlayerToMain(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(player.getName());
        out.writeUTF("world");
        player.sendPluginMessage(Main.getPlugin(), "BungeeCord", out.toByteArray());
    }
}
