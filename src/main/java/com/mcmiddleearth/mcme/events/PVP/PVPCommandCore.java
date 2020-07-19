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

import com.google.common.base.Joiner;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.mcme.events.Main;
import com.mcmiddleearth.mcme.events.PVP.Gamemode.BasePluginGamemode.GameState;
import com.mcmiddleearth.mcme.events.PVP.Handlers.BukkitTeamHandler;
import com.mcmiddleearth.mcme.events.PVP.Handlers.ChatHandler;
import com.mcmiddleearth.mcme.events.PVP.maps.Map;
import com.mcmiddleearth.mcme.events.Permissions;
import com.mcmiddleearth.mcme.events.PVP.command.PVPCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
 * @author Donovan <dallen@dallen.xyz>
 */
public class PVPCommandCore implements TabCompleter, CommandExecutor{
    
    protected static Map queuedGame = null;
    
    protected static Map runningGame = null;
    
    protected int parameter;

    protected static Main main;

    private CommandDispatcher<Player> commandDispatcher;

    public PVPCommandCore(){
        main = Main.getPlugin();
        this.commandDispatcher = new PVPCommand(main);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            try {
                if (args.length > 0) {
                    commandDispatcher.execute(commandDispatcher.parse(String.format("%s %s", label, Joiner.on(" ").join(args)), (Player) sender));
                } else {
                    commandDispatcher.execute(commandDispatcher.parse(label, (Player) sender));
                }
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            try {
                CompletableFuture<Suggestions> completionSuggestions = commandDispatcher.getCompletionSuggestions(commandDispatcher.parse(getInput(command, args), (Player) sender));
                return completionSuggestions.get().getList().stream().map(Suggestion::getText).collect(Collectors.toList());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }
    private String getInput(Command command, String[] args) {
        StringBuilder input = new StringBuilder(command.getName());
        for (String arg : args) {
            input.append(CommandDispatcher.ARGUMENT_SEPARATOR).append(arg);
        }
        return input.toString();
    }

    public static void toggleVoxel(boolean onlyDisable){
        try{
            if(Bukkit.getPluginManager().getPlugin("VoxelSniper").isEnabled()){
                Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("VoxelSniper"));
            }
            else if(!onlyDisable){
                Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin("VoxelSniper"));
            }
        }
        catch(NullPointerException e){
            System.err.println("VoxelSniper isn't loaded! Ignoring!");
        }
    }
	
	private boolean pvpGameQuickstart(CommandSender sender, String map, String[] args) {
            if(sender.hasPermission(Permissions.CREATE.getPermissionNode())){
                if(Map.maps.containsKey(map)){
                    Map m = Map.maps.get(map);

                    if(runningGame != null){
                        sender.sendMessage(ChatColor.RED + "Can't start!");
                        sender.sendMessage(ChatColor.GRAY + runningGame.getGmType() + " on " + runningGame.getTitle() + " is running!");
                        sender.sendMessage(ChatColor.GRAY + "You need to end the current game first, with " + ChatColor.GREEN + "/pvp game end" + ChatColor.GRAY + ".");
                    }
                    else if(queuedGame != null && queuedGame != m){
                        sender.sendMessage(ChatColor.RED + "Can't queue!");
                        sender.sendMessage(ChatColor.GRAY + queuedGame.getGmType() + " on " + queuedGame.getTitle() + " is in the queue!");
                        sender.sendMessage(ChatColor.GRAY + "You need to cancel the queued game first, with " + ChatColor.GREEN + "/pvp game end" + ChatColor.GRAY + ".");
                    }
                    else if(!m.getGm().requiresParameter().equals("none")){
                       try{
                            int newParam = Integer.parseInt(args[3]);
                            if(newParam < 1 ) {
                               sender.sendMessage(ChatColor.GRAY + "Parameter is not allowed to be this value.");
                            } else if(queuedGame == null) {
                                parameter = newParam;
                                sender.sendMessage("Map: " + m.getTitle() + ", Gamemode: " + m.getGmType());
                                /*for(Player p : Bukkit.getOnlinePlayers()){

                                    p.sendMessage(ChatColor.GRAY + p.getName() + " has started a game");
                                    p.sendMessage(ChatColor.GRAY + "Map: " + ChatColor.GREEN + m.getTitle() + ChatColor.GRAY + ", Gamemode: " + ChatColor.GREEN + m.getGmType());
                                    p.sendMessage(ChatColor.GRAY + "Use " + ChatColor.GREEN + "/pvp join" + ChatColor.GRAY + " to join the game");
                                    p.sendMessage(ChatColor.GRAY + "There are only " + m.getMax() + " slots left");
                                    p.sendMessage(ChatColor.GREEN + "Do /pvp rules " + removeSpaces(m.getGmType()) + " if you don't know how this gamemode works!");

                                }*/
                                queuedGame = m;
                            } else if(queuedGame == m && newParam != parameter) {
                                    sender.sendMessage(ChatColor.GRAY + "Parameter changed from " + ChatColor.GREEN + parameter + ChatColor.GRAY + " to " + ChatColor.GREEN + newParam);
                                    parameter = newParam;
                            }

                        }
                        catch(ArrayIndexOutOfBoundsException ex) {
                            sender.sendMessage(ChatColor.RED + m.getGmType() + " needs you to enter " + m.getGm().requiresParameter() + "!");
                        }
                       catch(NumberFormatException ex) {
                               sender.sendMessage(ChatColor.RED + "The parameter value must be an integer");
                       }
                    } else{
                        parameter = 0;
                        sender.sendMessage("Map: " + m.getTitle() + ", Gamemode: " + m.getGmType());
                        /*ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("PlayerList");
                        out.writeUTF("ALL");
                        Player player = (Player) sender;
                        player.sendPluginMessage(Main.getPlugin(), "BungeeCord", out.toByteArray());
                        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(Main.getPlugin(), "BungeeCord", 
                                new PluginMessageListener() {
                                    @Override
                                    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
                                        if (!channel.equals("BungeeCord")) {
                                          return;
                                        }
                                        ByteArrayDataInput in = ByteStreams.newDataInput(message);
                                        String subchannel = in.readUTF();
                                        if (subchannel.equals("PlayerList")) {
                                            String server = in.readUTF(); 
                                            String[] playerList = in.readUTF().split(", ");
                                            for(String playerName: playerList) {
                                                
                                            }
                                            Bukkit.getServer().getMessenger().unregisterIncomingPluginChannel(Main.getPlugin(), "BungeeCord", this);
                                        }
                                    }
                                });                      
                            for(Player pl : Bukkit.getOnlinePlayers()){

                                pl.sendMessage(ChatColor.GRAY + sender.getName() + " has started a game");
                                pl.sendMessage(ChatColor.GRAY + "Map: " + ChatColor.GREEN + m.getTitle() + ChatColor.GRAY + ", Gamemode: " + ChatColor.GREEN + m.getGmType());
                                pl.sendMessage(ChatColor.GRAY + "Use " + ChatColor.GREEN + "/pvp join" + ChatColor.GRAY + " to join the game");
                                pl.sendMessage(ChatColor.GRAY + "There are only " + m.getMax() + " slots left");
                                pl.sendMessage(ChatColor.GREEN + "Do /pvp rules " + removeSpaces(m.getGmType()) + " if you don't know how this gamemode works!");

                            }*/
                        queuedGame = m;
                    }

                }
                else{
                    sender.sendMessage("No such map!");
                }
            }
            return true;
	}
	
	private boolean pvpGameEnd(CommandSender sender) {
            if(sender.hasPermission(Permissions.CREATE.getPermissionNode())){
                if(runningGame != null){

                    for(Player pl : Bukkit.getOnlinePlayers()){
                        pl.sendMessage(ChatColor.GRAY + "The game was ended by a staff!");
                    }
                    runningGame.getGm().End(runningGame);
                }
                else if(queuedGame != null){
                    queuedGame.getGm().getPlayers().clear();
                    queuedGame = null;
                    for(Player pl : Bukkit.getOnlinePlayers()){
                        ChatHandler.getPlayerColors().put(pl.getName(), ChatColor.WHITE);
                        pl.setPlayerListName(ChatColor.WHITE + pl.getName());
                        pl.setDisplayName(ChatColor.WHITE + pl.getName());
                        BukkitTeamHandler.removeFromBukkitTeam(pl);
                        pl.sendMessage(ChatColor.GRAY + "The queued game was canceled! You'll need to rejoin when another game is queued.");
                    }
                    ChatHandler.getPlayerPrefixes().clear();
                } else {
                    sender.sendMessage(ChatColor.GRAY + "There is no game to end!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to end games!");
            }
            return true;
	}
	
	private boolean pvpGameGetGames(CommandSender cs) {
		cs.sendMessage("Getting maps");
        if(queuedGame != null || runningGame != null){
            
            if(queuedGame != null){
                cs.sendMessage(queuedGame.getName() + " is queued");
            }
            if(runningGame != null){
                cs.sendMessage(runningGame.getName() + " is running");
            }
            
        }
        else{
            cs.sendMessage("No games are currently queued or running!");
        }
        return true;
	}
	
	private boolean pvpJoin(Player p) {
		Map m = null;
        
        if(queuedGame != null){
            m = queuedGame;
        }
        else if(runningGame != null){
            m = runningGame;
        }
        else{
            p.sendMessage(ChatColor.RED + "There is no queued or running game!");
            return true;
        }
       
        if(!m.getGm().getPlayers().contains(p) && m.getGm().getState() != GameState.COUNTDOWN){
            if(m.playerJoin(p)){
                    
                if(m.getGm().getState() == GameState.IDLE){
                    p.setPlayerListName(ChatColor.GREEN + p.getName());
                    p.setDisplayName(ChatColor.GREEN + p.getName());
                    ChatHandler.getPlayerColors().put(p.getName(), ChatColor.GREEN);
                    ChatHandler.getPlayerPrefixes().put(p.getName(), ChatColor.GREEN + "Participant");
                    BukkitTeamHandler.addToBukkitTeam(p, ChatColor.GREEN);
                    /*try{
                        p.setResourcePack(m.getResourcePackURL());
                    }
                    catch(NullPointerException e){
                        p.sendMessage(ChatColor.RED + "No resource pack was set for this map!");
                    }*/
                }
                   
            }
            else{
                p.sendMessage("Failed to Join Map");
            }
        }
        else if(m.getGm().getState() == GameState.COUNTDOWN){
            p.sendMessage(ChatColor.RED + "Do " + ChatColor.GREEN + "/pvp join" + ChatColor.RED + " again once the countdown is done!");
        }
        else{
            p.sendMessage("You are already part of a game");
            //if(p.getName().equalsIgnoreCase("Despot666")){
            //    p.kickPlayer("<3 -Dallen");
            //}
        }
        p.setGameMode(GameMode.CREATIVE);
        p.setGameMode(GameMode.SURVIVAL);
        return true;
	}
	
	private boolean pvpRemoveGame(Player p, String map) {
            Map.maps.remove(map);
            File f = new File(PVPCore.getSaveLoc() + Main.getFileSep() + "Maps" + Main.getFileSep() + map);
            f.delete();
            p.sendMessage(ChatColor.RED + "Deleted " + map);
            return true;
	}
        
	private boolean pvpKick(Player p, String kickedPlayerName) {
        Player kickedPlayer = Bukkit.getPlayer(kickedPlayerName);
        if(kickedPlayer==null) {
            p.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }
        Map m;

        if(queuedGame != null){
            m = queuedGame;
        } else if(runningGame != null){
            m = runningGame;
        } else{
            p.sendMessage(ChatColor.RED + "There is no queued or running game!");
            return true;
        }

        if(!m.getGm().getPlayers().contains(kickedPlayer)){
            p.sendMessage(ChatColor.RED+"Player is not in the current game.");
            return true;
        } else {
            //JoinLeaveHandler.handlePlayerQuit(kickedPlayer);
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ConnectOther");
            out.writeUTF(kickedPlayerName);
            out.writeUTF("world");
            p.sendPluginMessage(Main.getPlugin(), "BungeeCord", out.toByteArray());
            p.sendMessage(ChatColor.GREEN+"Kicked "+kickedPlayerName+" from the PvP server!");
        }
        return true;
    }

    public static Map getQueuedGame() {
        return queuedGame;
    }

    public static Map getRunningGame() {
        return runningGame;
    }

    public static void setQueuedGame(Map queuedGame) {
        PVPCommandCore.queuedGame = queuedGame;
    }

    public static void setRunningGame(Map runningGame) {
        PVPCommandCore.runningGame = runningGame;
    }
}

                
