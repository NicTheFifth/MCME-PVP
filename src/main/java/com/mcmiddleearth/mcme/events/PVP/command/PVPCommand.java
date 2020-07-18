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
package com.mcmiddleearth.mcme.events.PVP.command;

import com.google.common.collect.Lists;
import com.mcmiddleearth.mcme.events.Main;
import com.mcmiddleearth.mcme.events.PVP.maps.Map;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;


public class PVPCommand extends CommandDispatcher<Player>{

    private final Main main;
    private static HashMap<String, Map> maps;
    private static Set<String> mapNames;
    public PVPCommand(Main main) {
        this.main = main;
        this.maps = Map.maps;
        this.mapNames = new HashSet<>(Lists.newArrayList());
        for (String i : maps.keySet()) {
            this.mapNames.add(i);
        }
        register(LiteralArgumentBuilder.<Player>literal("pvp")
            .then(LiteralArgumentBuilder.<Player>literal("map")
                .then(LiteralArgumentBuilder.<Player>literal("list").executes(c -> {
                    doCommand("mapList", c.getSource());
                    return 1;} ))
                .then(RequiredArgumentBuilder.<Player, String>argument("map", new CommandNewMapArgument(mapNames))
                    .then(LiteralArgumentBuilder.<Player>literal("spawn").executes(c -> {
                        doCommand("mapCreate", c.getArgument("map", String.class), c.getSource());
                        return 1;} )))
                .then(RequiredArgumentBuilder.<Player, String>argument("map", new CommandMapArgument(mapNames))
                    .then(LiteralArgumentBuilder.<Player>literal("poi")
                            .then(RequiredArgumentBuilder.<Player,String>argument("poi", new CommandStringArgument()).executes(c -> {
                        doCommand("mapPointCreate", c.getArgument("map", String.class), c.getArgument("poi", String.class), c.getSource());
                        return 1;} )))
                    .then(LiteralArgumentBuilder.<Player>literal("setmax")
                        .then(RequiredArgumentBuilder.<Player, String>argument("number", new CommandIntVariableArgument()).executes(c -> {
                                doCommand("setMax", c.getArgument("number", String.class), c.getSource());
                                return 1;} )))
                    .then(LiteralArgumentBuilder.<Player>literal("settitle")
                        .then(RequiredArgumentBuilder.<Player, String>argument("title", new CommandStringVariableArgument()).executes(c -> {
                            doCommand("setTitle", c.getArgument("title", String.class), c.getSource());
                            return 1;} )))
                    .then(LiteralArgumentBuilder.<Player>literal("setgamemode")
                            .then(RequiredArgumentBuilder.<Player, String>argument("gamemode", new CommandStringArgument("infected", "teamslayer", "teamdeathmatch", "ringbearer", "oneinthequiver", "teamconquest")).executes(c -> {
                                doCommand("setGamemode", c.getArgument("gamemode", String.class), c.getSource());
                                return 1;} )))
                    .then(LiteralArgumentBuilder.<Player>literal("setarea").executes(c -> {
                        doCommand("setArea", c.getSource());
                        return 1;} ))
                    .then(LiteralArgumentBuilder.<Player>literal("setrp")
                        .then(RequiredArgumentBuilder.<Player, String>argument("rp", new CommandStringArgument("eriador", "rohan", "lothlorien", "gondor", "dwarven", "moria", "mordor")).executes(c -> {
                            doCommand("setRP", c.getArgument("rp", String.class), c.getSource());
                            return 1;} )))))
            .then(LiteralArgumentBuilder.<Player>literal("game")
                .then(LiteralArgumentBuilder.<Player>literal("quickstart")
                    .then(RequiredArgumentBuilder.<Player, String>argument( "map", new CommandMapArgument(mapNames)).executes(c -> {
                        doCommand("createGame", c.getArgument("map", String.class), c.getSource());
                        return 1;} )
                        .then(RequiredArgumentBuilder.<Player,String>argument("variable", new CommandIntVariableArgument()).executes(c -> {
                            doCommand("createVarGame", c.getArgument("map", String.class), c.getArgument("variable", String.class), c.getSource());
                            return 1;} )
                            .then(LiteralArgumentBuilder.<Player>literal("test").executes(c -> {
                                doCommand("createVarTest", c.getArgument("map", String.class), c.getArgument("variable", String.class), c.getSource());
                                return 1;} )))
                        .then(LiteralArgumentBuilder.<Player>literal("test").executes(c -> {
                            doCommand("createTest", c.getArgument("map", String.class), c.getSource());
                            return 1;} ))))
                .then(LiteralArgumentBuilder.<Player>literal("start").executes(c -> {
                    doCommand("startGame", c.getSource());
                    return 1;} ))
                .then(LiteralArgumentBuilder.<Player>literal("end").executes(c -> {
                    doCommand("endGame", c.getSource());
                    return 1;} ))
                .then(LiteralArgumentBuilder.<Player>literal("getgames").executes(c -> {
                    doCommand("getGames", c.getSource());
                    return 1;} )))
            .then(LiteralArgumentBuilder.<Player>literal("join").executes(c -> {
                doCommand("join", c.getSource());
                return 1;} ))
            .then(LiteralArgumentBuilder.<Player>literal("kick")
                .then(RequiredArgumentBuilder.<Player, String>argument("player", new CommandPlayerArgument(main.getServer())).executes(c -> {
                    doCommand("kickPlayer", c.getArgument("player", String.class), c.getSource());
                    return 1;} )))
            .then(LiteralArgumentBuilder.<Player>literal("rules")
                    .then(RequiredArgumentBuilder.<Player, String>argument("gamemode", new CommandStringArgument("infected", "teamslayer", "teamdeathmatch", "ringbearer", "oneinthequiver", "teamconquest")).executes(c -> {
                        doCommand("rules", c.getArgument("gamemode", String.class), c.getSource());
                        return 1;} )))
            .then(LiteralArgumentBuilder.<Player>literal("pipe").executes(c -> {
                doCommand("pipe", c.getSource());
                return 1;} ))
            .then(LiteralArgumentBuilder.<Player>literal("stats").executes(c -> {
                doCommand("stats", c.getSource());
                return 1;} )
                .then(LiteralArgumentBuilder.<Player>literal("clear").executes(c -> {
                    doCommand("statsClear", c.getSource());
                    return 1;} )).executes(c -> {
                        doCommand("stats", c.getSource());
                        return 1;}))
            .then(LiteralArgumentBuilder.<Player>literal("removegame")
                .then(RequiredArgumentBuilder.<Player, String>argument("map", new CommandMapArgument(mapNames)).executes(c -> {
                    doCommand("deleteMap", c.getArgument("map", String.class), c.getSource());
                    return 1;} )))
            .then(LiteralArgumentBuilder.<Player>literal("togglevoxel").executes(c -> {
                doCommand("toggleVoxel", c.getSource());
                return 1;} ))
            .then(LiteralArgumentBuilder.<Player>literal("lobby").executes(c -> {
                doCommand("lobby", c.getSource());
                return 1;} ))
        );
    }

    private boolean hasPermission(Player source, String permission){
        return source.hasPermission(permission);
    }

    private void doCommand(String action, Player source) {
        Logger.getLogger("logger");
        switch (action) {
            case "mapList":
                Logger.getLogger("logger").log(Level.INFO, "mapList command received");
                break;
            case "setArea":
                Logger.getLogger("logger").log(Level.INFO, "setArea command received");
                break;
            case "startGame":
                Logger.getLogger("logger").log(Level.INFO, "startGame command received");
                break;
            case "endGame":
                Logger.getLogger("logger").log(Level.INFO, "endGame command received");
                break;
            case "getGame":
                Logger.getLogger("logger").log(Level.INFO, "getGame command received");
                break;
            case "join":
                Logger.getLogger("logger").log(Level.INFO, "join command received");
                break;
            case "stats":
                Logger.getLogger("logger").log(Level.INFO, "stats command received");
                break;
            case "statsClear":
                Logger.getLogger("logger").log(Level.INFO, "statsClear command received");
                break;
            case "toggleVoxel":
                Logger.getLogger("logger").log(Level.INFO, "toggleVoxel command received");
                break;
            case "lobby":
                Logger.getLogger("logger").log(Level.INFO, "lobby command received");
                break;
        }
    }
    private void doCommand(String action, String argument, Player source) {
        Logger.getLogger("logger");
        switch(action){
            case "mapCreate":
                Logger.getLogger("logger").log(Level.INFO, "mapCreate received with " + argument);
                break;
            case "setMax":
                Logger.getLogger("logger").log(Level.INFO, "setMax received with " + argument);
                break;
            case "setTitle":
                Logger.getLogger("logger").log(Level.INFO, "setTitle received with " + argument);
                break;
            case "setGamemode":
                Logger.getLogger("logger").log(Level.INFO, "setGamemode received with " + argument);
                break;
            case "createTest":
                Logger.getLogger("logger").log(Level.INFO, "createTest received with " + argument);
                break;
            case "createGame":
                Logger.getLogger("logger").log(Level.INFO, "createGame received with " + argument);
                break;
            case "kickPlayer":
                Logger.getLogger("logger").log(Level.INFO, "kickPlayer received with " + argument);
                break;
            case "rules":
                Logger.getLogger("logger").log(Level.INFO, "rules received with " + argument);
                break;
            case "deleteMap":
                Logger.getLogger("logger").log(Level.INFO, "deleteMap received with " + argument);
                break;
        }
    }
    private void doCommand(String action, String argument1, String argument2, Player source) {
        Logger.getLogger("logger");
        switch(action) {
            case "mapPointCreate":
                Logger.getLogger("logger").log(Level.INFO, "mapPointCreate received with " + argument1 + " and " + argument2);
                break;
            case "createVarTest":
                Logger.getLogger("logger").log(Level.INFO, "createVarTest received with " + argument1 + " and " + argument2);
                break;
            case "createVarGame":
                Logger.getLogger("logger").log(Level.INFO, "createVarGame received with " + argument1 + " and " + argument2);
                break;
        }
    }
}
