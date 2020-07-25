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
package com.mcmiddleearth.mcme.pvp.maps;

import com.mcmiddleearth.mcme.pvp.PVPPlugin;
import com.mcmiddleearth.mcme.pvp.command.PVPCommand;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Donovan <dallen@dallen.xyz>
 */
public class MapEditor{
    public static void MapCreator(String map, Player p){
        p.sendMessage(ChatColor.GREEN + "Creating new map");
        Map.maps.put(map, new Map(p.getLocation(), map));
        System.out.println(map);
        Map m = Map.maps.get(map);
        p.sendMessage(map + " spawn is: " + m.getSpawn().getX() + " " + m.getSpawn().getY() + " " + m.getSpawn().getZ());
        p.sendMessage("good version");
        sendMapMessage(map, m, p);
        PVPCommand.reloadMaplist();
    }

    public static void MapNameEdit(String map, String name, Player p){
        Map m = Map.maps.get(map);
        m.setName(name);
        Map.maps.put(name, m);
        Map.maps.remove(map);
        sendMapMessage(name, m, p);
        PVPCommand.reloadMaplist();
    }

    public static void MapTitleEdit(String map, String title, Player p){
        Map m = com.mcmiddleearth.mcme.pvp.maps.Map.maps.get(map);
        m.setTitle(title);
        sendMapMessage(map, m, p);
    }

    public static void MapGamemodeSet(String map, String gamemode, Player p){
        Map m = Map.maps.get(map);
        m.setGmType(gamemode);
        m.bindGamemode();
        sendMapMessage(map, m, p);
    }

    public static void MapMaxSet(String map, String amount, Player p){
        Map m = com.mcmiddleearth.mcme.pvp.maps.Map.maps.get(map);
        m.setMax(Integer.parseInt(amount));
        sendMapMessage(map, m, p);
    }
    public static void MapRPSet(String map, String rp, Player p){
        Map m = Map.maps.get(map);
        switch(rp) {
            case "eriador":
                m.setResourcePackURL("http://www.mcmiddleearth.com/content/Eriador.zip");
                break;
            case "rohan":
                m.setResourcePackURL("http://www.mcmiddleearth.com/content/Rohan.zip");
                break;
            case "lothlorien":
                m.setResourcePackURL("http://www.mcmiddleearth.com/content/Lothlorien.zip");
                break;
            case "gondor":
                m.setResourcePackURL("http://www.mcmiddleearth.com/content/Gondor.zip");
                break;
            case "moria":
                m.setResourcePackURL("http://www.mcmiddleearth.com/content/Moria.zip");
                break;
            case "mordor":
                m.setResourcePackURL("http://www.mcmiddleearth.com/content/Mordor.zip");
                break;
        }
        sendMapMessage(map, m, p);
    }

    public static void sendMapMessage (String map, Map m, Player p){
        FancyMessage message = new FancyMessage(MessageType.INFO, PVPPlugin.getMessageUtil());
        message.addFancy("Map name: " + m.getName() + "\n",
                "/mapeditor "+(map)+" name "+ map,
                "Click to edit. Don't change the leading '/mapEditor <map> name'.");
        message.addFancy("Map title: " + m.getTitle() + "\n",
                "/mapeditor "+(map)+" title "+ m.getTitle(),
                "Click to edit. Don't change the leading '/mapEditor <map> title'.");
        message.addFancy("Map gamemode: " + m.getGmType() + "\n",
                "/mapeditor "+(map)+" gm "+ m.getGmType(),
                "Click to edit. Don't change the leading '/mapEditor <map> gm'.");
        message.addFancy("Map max players: " + m.getMax() + "\n",
                "/mapeditor "+(map)+" max "+ m.getMax(),
                "Click to edit. Don't change the leading '/mapEditor <map> max'.");
        message.addFancy("Map rp: " + m.getResourcePackURL() + "\n",
                "/mapeditor "+(map)+" rp ",
                "Click to edit. Don't change the leading '/mapEditor <map> rp'.");
        message.send(p);
    }
}
