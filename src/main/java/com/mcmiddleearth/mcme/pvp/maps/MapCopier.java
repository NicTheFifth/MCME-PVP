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

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Eric
 */

public class MapCopier {
   
    private int posLow[];
    private int posHigh[];
    
    public void copyMapAcrossWorld(int pos1[], int pos2[]){
        posLow = pos1;
        posHigh = pos2;
        
        
    }
    
    public void copyMapInWorld(double originPos1[], double originPos2[], double endPos1[]){
        
    }
    
    private void divideAreaIntoChunks(){
        
        Block[][][] chunk;
        
        ArrayList<Block[][][]> chunkList = new ArrayList();
        
         
        
    }
}
