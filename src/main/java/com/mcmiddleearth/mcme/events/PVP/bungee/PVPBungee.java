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

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Fraspace5
 */
public final class PVPBungee extends Plugin {

    @Getter
    private static final HashMap<UUID, List<String>> data = new HashMap<>();

    @Getter
    private static PVPBungee instance;

    @Override
    public void onEnable() {
        getLogger().info("PVPBungee plugin " + this.getDescription().getVersion() + "v enabled!");
        instance = this;
        getProxy().getPluginManager().registerListener(this, new ListenerChat());
        try {
            loadAll();
        } catch (FileNotFoundException e) {

        } catch (IOException ex) {
            Logger.getLogger(PVPBungee.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void onDisable() {
        getLogger().info("PVPBungee plugin " + this.getDescription().getVersion() + "v disabled!");
    }

    private void loadAll() throws FileNotFoundException, IOException {
        data.clear();
        File file = new File(getDataFolder().getAbsolutePath() + "/players.json");

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        if (!file.exists()) {
            file.createNewFile();
        } else if (file.length() > 0) {
            Scanner myReader = new Scanner(file);
            StringBuilder text = new StringBuilder();
            while (myReader.hasNextLine()) {
                text.append(myReader.nextLine());

            }
            myReader.close();
            
            JSONObject json = new JSONObject(text.toString());
            JSONArray array = json.getJSONArray("players");

            for (int i = 0; i < array.length(); i++) {

                JSONObject player = array.getJSONObject(i);

                data.put(UUID.fromString(player.getString("uuid")), listUnserialize(player.getString("games")));
            }

        }

    }

    private List<String> listUnserialize(String s) {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, s.split(";"));

        return list;
    }

}
