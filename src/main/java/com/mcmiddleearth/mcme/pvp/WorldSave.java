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
package com.mcmiddleearth.mcme.pvp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.mcmiddleearth.mcme.pvp.PVPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan
 */
public class WorldSave {
    
    private static InputStream in;
    private static OutputStream out;
    
    public static void saveWorld(final World w, final String name, final Player sender){
        w.save();
        World notw = Bukkit.getWorld(PVPPlugin.getPlugin().getSpawnWorld());
        if(notw == null){
            for(Player p : Bukkit.getOnlinePlayers()){
                p.kickPlayer("reseting world");
            }
        }else{
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.getWorld().equals(w)){
                    p.teleport(notw.getSpawnLocation());
                }
            }
        }
        final File old = w.getWorldFolder();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PVPPlugin.getPlugin(), new Runnable() {
                    @Override
                    public void run() {   
                        copyWorld(old, new File(PVPPlugin.getPlugin().getDataFolder()+ System.getProperty("file.separator") + name));
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PVPPlugin.getPlugin(), new Runnable() {
                            @Override
                            public void run() {   
                                sender.teleport(w.getSpawnLocation());
                            }
                        }, 20L);
                    }
            }, 20L);
    }
    
    public static boolean loadWorld(World w, final String name){
        if(!new File(PVPPlugin.getPlugin().getDataFolder() + System.getProperty("file.separator") + name).exists()){
            return false;
        }
        final String wname = w.getName();
        World notw = Bukkit.getWorld(PVPPlugin.getPlugin().getSpawnWorld());
        if(notw == null){
            for(Player p : Bukkit.getOnlinePlayers()){
                p.kickPlayer("reseting world");
            }
        }else{
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.getWorld().equals(w)){
                    p.teleport(notw.getSpawnLocation());
                        Inventory i = p.getInventory();
                        i.clear();
                        p.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
                        p.getInventory().setContents(i.getContents());
                }
            }
        }
        Bukkit.getServer().unloadWorld(w, true);
        final File path = w.getWorldFolder();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PVPPlugin.getPlugin(), new Runnable() {
                @Override
                public void run() {   
                    copyWorld(new File(PVPPlugin.getPlugin().getDataFolder() + System.getProperty("file.separator") + name), path);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PVPPlugin.getPlugin(), new Runnable() {
                        @Override
                        public void run() {   
                            Bukkit.getServer().getWorlds().add(Bukkit.getServer().createWorld(new WorldCreator(wname)));
                        }
                    }, 20L);
                }
            }, 20L);
        return false;
    }
    
    private static boolean deleteWorld(File path) {
      if(path.exists()) {
//          System.out.println(path.toString());
          if (path.isDirectory()) {
            File files[] = path.listFiles();
            for (String file : path.list()) {
                deleteWorld(new File(path, file));
            }
          } else {
            path.delete();
          }
      }
      return(path.delete());
    }
    private static void copyWorld(File src, File dest){
        deleteWorld(dest);
        try {
            if(src.isDirectory()){
    		if(!dest.exists()){
    		   dest.mkdir();
    		}
    		String files[] = src.list();
    		for (String file : files) {
    		   File srcFile = new File(src, file);
    		   File destFile = new File(dest, file);
    		   copyWorld(srcFile,destFile);
    		}
 
    	}else{
    		in = new FileInputStream(src);
                out = new FileOutputStream(dest); 
    	        byte[] buffer = new byte[1024];
    	        int length;
    	        while ((length = in.read(buffer)) > 0){
    	    	   out.write(buffer, 0, length);
                } 
    	        in.close();
    	        out.close();
    	}
        } catch (IOException e) {
            Bukkit.getLogger().info(e.getMessage());
        }
    }
}
