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
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Fraspace5
 */
public final class PVPBungee extends Plugin {

    @Getter
    private static final HashMap<UUID, Boolean> data = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("PVPBungee plugin" + this.getDescription().getVersion() + "v enabled!");
        getProxy().getPluginManager().registerListener(this, new ListenerChat());

        try {
            loadAll();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        backup();

    }

    @Override
    public void onDisable() {
        saveAll();
        getLogger().info("PVPBungee plugin" + this.getDescription().getVersion() + "v disabled!");
    }

    private void saveAll() {

        File file = new File(getClass().getResource("players.json").toString());

        if (!file.exists()) {
            file.mkdir();
        }
        JSONObject json = new JSONObject();
        JSONArray pldata = new JSONArray();

        for (UUID uuid : data.keySet()) {
            pldata.put(getPlayer(uuid, data.get(uuid)));
        }

        json.put("players", pldata);

        try {
            FileWriter s = new FileWriter(file);
            s.write(json.toString(1));
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void loadAll() throws FileNotFoundException {
        data.clear();
        File file = new File(getClass().getResource("players.json").toString());

        if (!file.exists()) {
            file.mkdir();
        } else {
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

                data.put(UUID.fromString(player.getString("uuid")), player.getBoolean("bool"));
            }


        }


    }

    private JSONObject getPlayer(UUID uuid, Boolean bool) {
        JSONObject player = new JSONObject();
        player.put("uuid", uuid.toString());
        player.put("bool", bool);
        return player;
    }

    private void backup() {

        ScheduledTask schedule = getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                saveAll();
            }
        }, 2, 60, TimeUnit.MINUTES);


    }


}
