/*
 * Copyright (C) 2020 MCME (Fraspace5)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.mcme.events.PVP.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Fraspace5
 */
public class ListenerChat implements Listener {

    @EventHandler
    public void chatMessage(ChatEvent event) {
        ProxiedPlayer pl = (ProxiedPlayer) event.getReceiver();
        String[] message = event.getMessage().split(" ");
        if (event.isCommand() && event.getSender() instanceof ProxiedPlayer) {
            if (message[0].equalsIgnoreCase("/pvp")) {
                if (message.length == 2 && message[1].equalsIgnoreCase("confirm")) {

                    if (PVPBungee.getData().get(pl.getUniqueId())) {
                        pl.sendMessage(new ComponentBuilder("You'll no longer receive pvp alerts ").color(ChatColor.AQUA).create());
                        PVPBungee.getData().remove(pl.getUniqueId());
                        PVPBungee.getData().put(pl.getUniqueId(), false);
                    } else {
                        pl.sendMessage(new ComponentBuilder("From now you will be informed about new pvp games ").color(ChatColor.AQUA).create());
                        PVPBungee.getData().remove(pl.getUniqueId());
                        PVPBungee.getData().put(pl.getUniqueId(), true);
                    }


                } else {

                    pl.sendMessage(new ComponentBuilder("Not enough arguments ").color(ChatColor.RED).create());

                }

            }


        }


    }


    @EventHandler
    public void onPostLogin(PostLoginEvent event) {

        ProxiedPlayer pl = event.getPlayer();

        if (!PVPBungee.getData().containsKey(pl.getUniqueId())) {
            PVPBungee.getData().put(pl.getUniqueId(), true);

        }

    }


    @EventHandler
    public void onMessageSent(PluginMessageEvent event) {
        ProxiedPlayer pl = (ProxiedPlayer) event.getReceiver();

        if (!PVPBungee.getData().get(pl.getUniqueId())) {
            event.setCancelled(true);
        }


    }

}
