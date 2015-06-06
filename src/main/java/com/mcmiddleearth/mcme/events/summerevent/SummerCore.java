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
package com.mcmiddleearth.mcme.events.summerevent;

import com.mcmiddleearth.mcme.events.Main;
import com.mcmiddleearth.mcme.events.summerevent.PVP.Servlet.PVPServer;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;

/**
 *
 * @author Donovan <dallen@dallen.xyz>
 */
public class SummerCore {
    PVPServer server;
    
    @Getter
    private static File saveLoc = new File(Main.getPluginDirectory() + Main.getFileSep() + "Summer");
    
    public void onEnable(){
        try {
            server = new PVPServer(8080);
            server.getServ().start();
        } catch (Exception ex) {
            Logger.getLogger(SummerCore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void onDisable(){
        try {
            server.getServ().stop();
        } catch (Exception ex) {
            Logger.getLogger(SummerCore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
