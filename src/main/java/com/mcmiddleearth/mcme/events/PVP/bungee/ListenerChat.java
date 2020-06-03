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

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.mcme.events.PVP.PVPCommandCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Fraspace5
 */
public class ListenerChat implements Listener {

    @EventHandler
    public void chatMessage(ChatEvent event) {
        ProxiedPlayer pl = (ProxiedPlayer) event.getReceiver();
        String[] message = event.getMessage().split(" ");
        if (event.isCommand() && event.getSender() instanceof ProxiedPlayer) {
            if (message[0].equalsIgnoreCase("/pvp")) {
                if (message.length == 2 && (message[1].equalsIgnoreCase("acknowlege") || message[1].equalsIgnoreCase("ack"))) {
                    if (PVPBungee.getData().containsKey(pl.getUniqueId())) {
                        if (!PVPBungee.getData().get(pl.getUniqueId()).contains(PVPCommandCore.getRunningGame().getName())) {
                            pl.sendMessage(new ComponentBuilder("You'll no longer receive pvp alerts for " + PVPCommandCore.getRunningGame().getName()).color(ChatColor.AQUA).create());

                            List<String> s = PVPBungee.getData().get(pl.getUniqueId());
                            s.add(PVPCommandCore.getRunningGame().getName());

                            PVPBungee.getData().remove(pl.getUniqueId());
                            PVPBungee.getData().put(pl.getUniqueId(), s);
                        }

                    } else {
                        pl.sendMessage(new ComponentBuilder("From now you will be informed about new pvp games ").color(ChatColor.AQUA).create());
                        PVPBungee.getData().put(pl.getUniqueId(), new ArrayList<String>());
                    }

                } else if (message.length == 2 && (message[1].equalsIgnoreCase("unsubscribe") || message[1].equalsIgnoreCase("unsub"))) {

                    if (!PVPBungee.getData().containsKey(pl.getUniqueId())) {
                        pl.sendMessage(new ComponentBuilder("You'll no longer receive pvp alerts ").color(ChatColor.AQUA).create());
                        PVPBungee.getData().put(pl.getUniqueId(), new ArrayList<String>());
                        saveAll();
                    } else {
                        pl.sendMessage(new ComponentBuilder("You have already unsubscribed ").color(ChatColor.RED).create());
                    }


                } else if (message.length == 2 && (message[1].equalsIgnoreCase("subscribe") || message[1].equalsIgnoreCase("sub"))) {
                    if (PVPBungee.getData().containsKey(pl.getUniqueId())) {
                        pl.sendMessage(new ComponentBuilder("From now you will be informed about new pvp games ").color(ChatColor.AQUA).create());
                        PVPBungee.getData().remove(pl.getUniqueId());
                        saveAll();
                    } else {
                        pl.sendMessage(new ComponentBuilder("You have already subscribed ").color(ChatColor.RED).create());

                    }


                } else {
                    pl.sendMessage(new ComponentBuilder("Not enough arguments for this command").color(ChatColor.RED).create());

                }

            }


        }


    }


    @EventHandler
    public void onMessageSent(PluginMessageEvent event) {
        ProxiedPlayer pl = (ProxiedPlayer) event.getReceiver();
        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
        String subchannel = input.readUTF();

        if (subchannel.equalsIgnoreCase("mcme:event") && PVPBungee.getData().containsKey(pl.getUniqueId())) {
            if (PVPBungee.getData().get(pl.getUniqueId()).isEmpty()) {
                event.setCancelled(true);
            } else if (PVPBungee.getData().get(pl.getUniqueId()).contains(PVPCommandCore.getRunningGame().getName())) {
                event.setCancelled(true);
            }

        }


    }

    private void saveAll() {

        File file = new File(getClass().getResource("players.json").toString());

        if (!file.exists()) {
            file.mkdir();
        }

        try {
            JSONObject json = new JSONObject();
            JSONArray pldata = new JSONArray();

            for (UUID uuid : PVPBungee.getData().keySet()) {
                JSONObject player = new JSONObject();
                player.put("uuid", uuid.toString());
                pldata.put(player);
                player.put("games", serialize(PVPBungee.getData().get(uuid)));
            }

            json.put("players", pldata);

            try {
                FileWriter s = new FileWriter(file);
                s.write(json.toString(1));
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String serialize(List<String> list) {
        StringBuilder builder = new StringBuilder();
        list.forEach((game) -> {
            builder.append(game).append(";");
        });
        return builder.toString();
    }

}
