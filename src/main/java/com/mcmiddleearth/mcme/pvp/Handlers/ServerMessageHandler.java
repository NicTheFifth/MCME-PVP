package com.mcmiddleearth.mcme.pvp.Handlers;

import com.mcmiddleearth.mcme.pvp.command.PVPCommand;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerMessageHandler implements Listener{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(ServerListPingEvent e){
        if(PVPCommand.isLocked()){
            e.setMotd(e.getMotd() + "\n" + ChatColor.BLUE + PVPCommand.getMessage());
            e.setMaxPlayers(0);
        }
    }
}
