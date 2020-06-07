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
import com.mcmiddleearth.mcme.events.PVP.Gamemode.BasePluginGamemode.GameState;
import com.mcmiddleearth.mcme.events.PVP.Handlers.BukkitTeamHandler;
import com.mcmiddleearth.mcme.events.PVP.Handlers.ChatHandler;
import com.mcmiddleearth.mcme.events.PVP.Handlers.CommandBlockHandler;
import com.mcmiddleearth.mcme.events.PVP.Handlers.GearHandler;
import com.mcmiddleearth.mcme.events.PVP.maps.Map;
import com.mcmiddleearth.mcme.events.PVP.maps.MapEditor;
import com.mcmiddleearth.mcme.events.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Donovan <dallen@dallen.xyz>
 */
public class PVPCommandCore implements CommandExecutor, TabCompleter {
    
    protected static Map queuedGame = null;
    
    protected static Map runningGame = null;
    
    protected int parameter;
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if(cs instanceof Player){
            if(args.length >= 1){
                Player p = (Player) cs;
                
                if(args[0].equalsIgnoreCase("game") && args.length >= 2){
                    if(args[1].equalsIgnoreCase("start")){
                        return pvpGameStart(cs);
                    }
                    else if(args[1].equalsIgnoreCase("quickstart")){
        				return args.length >= 3 ? pvpGameQuickstart(cs, args[2], args) : false;
                    }    
                    else if(args[1].equalsIgnoreCase("end")){
                        return pvpGameEnd(cs);
                    }
                    else if(args[1].equalsIgnoreCase("getgames") && p.hasPermission(Permissions.PVP_MANAGER.getPermissionNode())){
                        return pvpGameGetGames(cs);
                    }    
                } 
                else if(args[0].equalsIgnoreCase("join")){
                	return pvpJoin(p);
                }
                else if(args[0].equalsIgnoreCase("kick") && p.hasPermission(Permissions.PVP_MANAGER.getPermissionNode())){
                	return pvpKick(p, args[1]);
                }
                else if(args[0].equalsIgnoreCase("pipe")){
                    GearHandler.giveCustomItem(p, GearHandler.CustomItem.PIPE);
                }
                else if(args[0].equalsIgnoreCase("stat") || args[0].equalsIgnoreCase("stats") || args[0].equalsIgnoreCase("statistics")){
                    if(args.length == 1) {
                    	return pvpStat(p, args.length);
                    }
                    else if(args[1].equalsIgnoreCase("clear") && (p.hasPermission(Permissions.PVP_ADMIN.getPermissionNode()))){
                        return pvpStatClear();
                    }   
                }
                else if(args[0].equalsIgnoreCase("rules")){
                    if(args.length < 2) {
                    	p.sendMessage(ChatColor.RED + "Format: /pvp rules <gamemode>");
                        p.sendMessage(ChatColor.GRAY + "Gamemodes are: FreeForAll, Infected, OneInTheQuiver, Ringbearer, TeamConquest, TeamDeathmatch, and TeamSlayer");
                    }
                    else {
                    	return pvpRules(p, args[1]);
                    }
                    return true;
                }
                else if(args[0].equalsIgnoreCase("removegame") && (p.hasPermission(Permissions.PVP_ADMIN.getPermissionNode()))){
                    if(args.length < 2) {
                    	p.sendMessage(ChatColor.RED + "Format: /pvp removegame <map>");
                    	return true;
                    }
                    else {
                    	return pvpRemoveGame(p, args[1]);
                    }
                }
                if(cs.hasPermission(Permissions.PVP_ADMIN.getPermissionNode())) { 
                    return new MapEditor().onCommand(cs, cmnd, label, args);
                } else {
                    cs.sendMessage(ChatColor.RED + "You don't have the permission to edit maps!");
                }
            
            }   
            else if(args.length>0 && args[0].equalsIgnoreCase("togglevoxel") && cs.hasPermission(Permissions.PVP_ADMIN.getPermissionNode())){
                toggleVoxel(false);
            } else {
                cs.sendMessage(ChatColor.RED + "You are at the PvP server already !");
            }
            return true;
        }
        else if(cs instanceof BlockCommandSender){
            return new CommandBlockHandler().onCommand(cs, cmnd, label, args);
        }
        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        List<String> arguments = new ArrayList<>();
        List<String> Flist = new ArrayList<>();
        Player p = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("pvp")) {
            if (args.length == 1) {
                arguments.add("join");
                arguments.add("rules");
                arguments.add("pipe");
                arguments.add("stats");
                if(sender.hasPermission(Permissions.PVP_MANAGER.getPermissionNode())) {
                    arguments.add("map");
                    arguments.add("game");
                    arguments.add("kick");
                    if(sender.hasPermission(Permissions.PVP_ADMIN.getPermissionNode())) {
                        arguments.add("removegame");
                        arguments.add("togglevoxel");
                        arguments.add("lobby");
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("map")) {
                    if(sender.hasPermission(Permissions.PVP_MANAGER.getPermissionNode())) {
                        arguments.add("list");
                        if (sender.hasPermission(Permissions.PVP_ADMIN.getPermissionNode())) {
                            arguments.add("<map-name>");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("game")) {
                    if(sender.hasPermission(Permissions.PVP_MANAGER.getPermissionNode())) {
                        arguments.add("quickstart");
                        arguments.add("start");
                        arguments.add("end");
                        arguments.add("getgames");
                    }
                } else if (args[0].equalsIgnoreCase("rules")) {
                    arguments.add("infected");
                    arguments.add("oneinthequiver");
                    arguments.add("ringbearer");
                    arguments.add("teamslayer");
                    arguments.add("teamdeathmatch");
                    arguments.add("teamconquest");
                } else if (args[0].equalsIgnoreCase("stats")) {
                    if(sender.hasPermission(Permissions.PVP_ADMIN.getPermissionNode())) {
                        arguments.add("clear");
                    }
                } else if (args[0].equalsIgnoreCase("removegame")) {
                    if (sender.hasPermission(Permissions.PVP_ADMIN.getPermissionNode())) {
                        arguments.add("<map-name>");
                    }
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("map")) {
                    if(p.hasPermission(Permissions.PVP_MANAGER.getPermissionNode())) {
                        arguments.add("spawn");
                        arguments.add("poi");
                        arguments.add("setMax");
                        arguments.add("setTitle");
                        arguments.add("setGamemode");
                        arguments.add("setArea");
                        arguments.add("setRP");
                    }
                } else if (args[1].equalsIgnoreCase("quickstart")) {
                    if(p.hasPermission(Permissions.PVP_MANAGER.getPermissionNode())) {
                        arguments.add("<map-name>");
                    }
                }
            } else if (args.length == 4) {
                if (args[1].equalsIgnoreCase("quickstart")) {
                    if(p.hasPermission(Permissions.PVP_MANAGER.getPermissionNode())) {
                        arguments.add("test");
                    }
                } else if(args[2].equalsIgnoreCase("setgamemode")){
                    arguments.add("TeamSlayer");
                    arguments.add("TeamDeathmatch");
                    arguments.add("FreeForAll");
                    arguments.add("Infected");
                    arguments.add("OneInTheQuiver");
                    arguments.add("Ringbearer");
                    arguments.add("TeamConquest");
                    arguments.add("KingOfTheHill");
                } else if(args[2].equalsIgnoreCase("setrp")) {
                    arguments.add("eriador");
                    arguments.add("rohan");
                    arguments.add("lothlorien");
                    arguments.add("gondor");
                    arguments.add("dwarven");
                    arguments.add("moria");
                    arguments.add("mordor");
                }
            }
            if (args.length >= 1 && arguments.size() != 0) {
                for (String s : arguments) {
                    if (s.toLowerCase().startsWith(args[args.length -1].toLowerCase())) {
                        Flist.add(s);
                        }
                    }
                    return Flist;
                } else
                    return null;
        } else
            return null;
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
    
    private static void giveRules(Player sendTo, String gm){
        gm = gm.toLowerCase();
        switch(gm){
            case "freeforall":
                sendTo.sendMessage(ChatColor.GREEN + "Free For All Rules");
                sendTo.sendMessage(ChatColor.GRAY + "Every man for himself, madly killing everyone! Highest number of kills wins.");
                break;
            case "infected":
                sendTo.sendMessage(ChatColor.GREEN + "Infected Rules");
                sendTo.sendMessage(ChatColor.GRAY + "Everyone starts as a Survivor, except one person, who is Infected. Infected gets a Speed effect, but has less armor");
                sendTo.sendMessage(ChatColor.GRAY + "If a Survivor is killed, they become Infected. Infected players have infinite respawns");
                sendTo.sendMessage(ChatColor.GRAY + "If all Survivors are infected, Infected team wins. If the time runs out with Survivors remaining, Survivors win.");
                break;
            case "oneinthequiver":
                sendTo.sendMessage(ChatColor.GREEN + "One in the Quiver Rules");
                sendTo.sendMessage(ChatColor.GRAY + "Everyone gets an axe, a bow, and one arrow, which kills in 1 shot if the bow is fully drawn.");
                sendTo.sendMessage(ChatColor.GRAY + "Every man is fighting for himself. If they get a kill or die, they get another arrow, up to a max of 5 arrows");
                sendTo.sendMessage(ChatColor.GRAY + "First to 21 kills wins.");
                break;
            case "ringbearer":
                sendTo.sendMessage(ChatColor.GREEN + "Ringbearer Rules");
                sendTo.sendMessage(ChatColor.GRAY + "Two teams, each with a ringbearer, who gets The One Ring (which of course gives invisibility)");
                sendTo.sendMessage(ChatColor.GRAY + "As long as the ringbearer is alive, the team can respawn.");
                sendTo.sendMessage(ChatColor.GRAY + "Once the ringbearer dies, that team cannot respawn. The first team to run out of members loses.");
                break;
            case "teamconquest":
                sendTo.sendMessage(ChatColor.GREEN + "Team Conquest Rules");
                sendTo.sendMessage(ChatColor.GRAY + "Two teams. There are 3 beacons, which each team can capture by repeatedly right clicking the beacon.");
                sendTo.sendMessage(ChatColor.GRAY + "Points are awarded on kills, based on the difference between each team's number of beacons.");
                sendTo.sendMessage(ChatColor.GRAY + "i.e. if Red has 3 beacons and Blue has 0, Red gets 3 point per kill. If Red has 1 and Blue has 2, Red doesn't get points for a kill.");
                sendTo.sendMessage(ChatColor.GRAY + "First team to a certain point total wins.");
                break;
            case "teamdeathmatch":
                sendTo.sendMessage(ChatColor.GREEN + "Team Deathmatch Rules");
                sendTo.sendMessage(ChatColor.GRAY + "Two teams, and no respawns. First team to run out of players loses.");
                break;
            case "teamslayer":
                sendTo.sendMessage(ChatColor.GREEN + "Team Slayer Rules");
                sendTo.sendMessage(ChatColor.GRAY + "Two teams, and infinite respawns. 1 point per kill. First team to a certain point total wins.");
                break;
            default:
                sendTo.sendMessage(ChatColor.RED + gm + " is not a valid gamemode! Did you add spaces or hyphens?");
                sendTo.sendMessage(ChatColor.GRAY + "Gamemodes are: FreeForAll, Infected, OneInTheQuiver, Ringbearer, TeamConquest, TeamDeathmatch, and TeamSlayer");
        }
    }
    
    public static String removeSpaces(String s){
        String newString = "";
        
        char[] chars = s.toCharArray();
        
        for(char c : chars){
            if(c != ' '){
                newString += String.valueOf(c);
            }
        }
        return newString;
    }
	
	private boolean pvpGameStart(CommandSender sender) {
            if(sender.hasPermission(Permissions.PVP_MANAGER.getPermissionNode())){
                if(queuedGame == null){
                    sender.sendMessage(ChatColor.RED + "Can't start! No game is queued!");
                } else if(queuedGame.getGm().getPlayers().size() == 0 ){
                    sender.sendMessage(ChatColor.RED + "Can't start! No players have joined!");
                } else if(runningGame == null){
                    queuedGame.getGm().Start(queuedGame, parameter);
                    runningGame = queuedGame;
                    queuedGame = null;
                }
                else{
                    sender.sendMessage(ChatColor.RED + "Can't start! There's already a game running!");
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to end games!");
            }
            return true;
	}
	
	private boolean pvpGameQuickstart(CommandSender sender, String map, String[] args) {
            if(sender.hasPermission(Permissions.PVP_MANAGER.getPermissionNode())){
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
                                sendBroadcast((Player)sender,m,args);
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
                        sendBroadcast((Player)sender,m,args);
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
        
        private void sendBroadcast(Player player, Map m,String[] args) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (PVPCommandCore.getRunningGame().getName().equalsIgnoreCase(m.getName())) {

                            ByteArrayDataOutput out = ByteStreams.newDataOutput();
                            out.writeUTF("Message");
                            out.writeUTF("ALL");
                            out.writeUTF("mcme:event");
                            out.writeUTF(ChatColor.GRAY + player.getName() + " has started a game\n"
                                    + ChatColor.GRAY + "Map: " + ChatColor.GREEN + m.getTitle() + ChatColor.GRAY + ", Gamemode: " + ChatColor.GREEN + m.getGmType() + "\n"
                                    + ChatColor.GRAY + "Use " + ChatColor.GREEN + "/pvp join" + ChatColor.GRAY + " to join the game\n"
                                    + ChatColor.GRAY + "There are only " + m.getMax() + " slots left\n"
                                    + ChatColor.GREEN + "Do /pvp rules " + removeSpaces(m.getGmType()) + " if you don't know how this gamemode works!");
                            player.sendPluginMessage(Main.getPlugin(), "BungeeCord", out.toByteArray());
                        } else {
                            cancel();
                        }

                    }
                }.runTaskTimer(Main.getPlugin(), 0L, 1200 * Main.getMinutes());
        }
	
	private boolean pvpGameEnd(CommandSender sender) {
            if(sender.hasPermission(Permissions.PVP_MANAGER.getPermissionNode())){
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
	
	private boolean pvpStat(Player p, int argLength) {
        PlayerStat ps = PlayerStat.getPlayerStats().get(p.getName());
    
        p.sendMessage(ChatColor.GREEN + "Showing stats for " + p.getName());
        p.sendMessage(ChatColor.GRAY + "Kills: " + ps.getKills());
        p.sendMessage(ChatColor.GRAY + "Deaths: " + ps.getDeaths());
        p.sendMessage(ChatColor.GRAY + "Games Played: " + ps.getGamesPlayed());
        p.sendMessage(ChatColor.GRAY + "    Won: " + ps.getGamesWon());
        p.sendMessage(ChatColor.GRAY + "    Lost: " + ps.getGamesLost());
        p.sendMessage(ChatColor.GRAY + "Games Spectated: " + ps.getGamesSpectated());
        return true;
	}
	
	private boolean pvpStatClear() {
		for(File f : new File(PVPCore.getSaveLoc() + Main.getFileSep() + "stats").listFiles()){
            f.delete();
        }
        
        for(PlayerStat ps : PlayerStat.getPlayerStats().values()){
            
            ps.setKills(0);
            ps.setDeaths(0);
            ps.setGamesLost(0);
            ps.setGamesWon(0);
            ps.setGamesSpectated(0);
            ps.setGamesPlayed(0);
            ps.getPlayersKilled().clear();
            
        }
        return true;
	}
	
	private boolean pvpRules(Player p, String gamemode) {
        giveRules(p, gamemode);
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
            }
            else if(runningGame != null){
                m = runningGame;
            }
            else{
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

                
