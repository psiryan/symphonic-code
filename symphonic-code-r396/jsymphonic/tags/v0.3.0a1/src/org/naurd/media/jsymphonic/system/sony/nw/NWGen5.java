/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *****
 * 
 * NWGen5.java
 *
 * Created on 29 mars 2008, 10:18
 *
 */

package org.naurd.media.jsymphonic.system.sony.nw;

import java.io.File;
import java.util.logging.Logger;
import org.naurd.media.jsymphonic.toolBox.Java6ToolBox;


/**
 * Describes a Sony device from fifth generation. This generation concerns none protected player without special features nor cover support. For instance, NW-E005,...
 *
 * @author nicolas_cardoso
 */
public class NWGen5 extends NWGeneric{
/* FIELDS */
    //Other
    private static Logger logger = Logger.getLogger("org.naurd.media.jsymphonic.system.sony.nw.NWGen5");
      
    
/* CONSTRUCTORS */
    /**
     * Creates a new instance of NWGen5
     */
    public NWGen5(File omgaudioDir, String sourceName, String sourceDesc, javax.swing.ImageIcon sourceIcon, NWGenericListener listener, String exportPath) {
        // Call the super contructor
        super(sourceName, sourceDesc, sourceIcon, listener, exportPath);
        
        // Set up the source directory (if it exists). In this generation, the source directory is the OMGAUDIO in the root folder of the device.
        if( !omgaudioDir.exists() ) {
            logger.severe("Invalid OMGAUDIO directory.\nExiting program.");
            System.exit(-1);
        }
        this.sourceDir = omgaudioDir;
        
        // Update space
        Java6ToolBox.FileSpaceInfo spaceInfo = Java6ToolBox.getFileSpaceInfo(sourceDir);
        usableSpace = spaceInfo.getUsableSpace();
        totalSpace = spaceInfo.getTotalSpace();
        
        // Set up the database (gen 5 uses the same data base as generation 3)
        dataBase = new OmaDataBaseGen3(omgaudioDir);
                
        // Set the generation
        generation = 5;
		this.gotkey = false; // Gen5 devices never have a key !!

        // Should be manage in a better way !
        // Fill in the title's list
        loadTitlesFromDevice();
    }
    
/* METHODS */
    // This method implement the abstract method given in the super class
}