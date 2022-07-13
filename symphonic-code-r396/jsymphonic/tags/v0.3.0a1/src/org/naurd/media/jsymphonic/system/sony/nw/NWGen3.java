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
 * NWGen3.java
 *
 * Created on 29 mars 2008, 10:18
 *
 */

package org.naurd.media.jsymphonic.system.sony.nw;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;
import org.naurd.media.jsymphonic.toolBox.Java6ToolBox;


/**
 * Describes a Sony device from third generation. This generation concerns protected player without special features nor cover support. For instance, NW-HD5, NW-E4xx,...
 *
 * @author nicolas_cardoso
 */
public class NWGen3 extends NWGeneric{
/* FIELDS */
    //private byte[] key = {0,0,0,0}; //Key of the player
    //private boolean gotkey = false;//Do we have a key for the player?
    //private long uintKeyDvID;//The key as an unsigned integer
    
    //Other
    private static Logger logger = Logger.getLogger("org.naurd.media.jsymphonic.system.sony.nw.NWGen3");
  
/* CONSTRUCTORS */
    /**
     * Creates a new instance of NWGen3
     */
    public NWGen3(File omgaudioDir, String sourceName, String sourceDesc, javax.swing.ImageIcon sourceIcon, NWGenericListener listener, String exportPath) {
        // Call the super contructor
        super(sourceName, sourceDesc, sourceIcon, listener, exportPath);
        
        // Set the source directory (if it exists). In this generation, the source directory is the OMGAUDIO in the root folder of the device.
        if( !omgaudioDir.exists() ) {
            logger.severe("Invalid OMGAUDIO directory.\nExiting program.");
            System.exit(-1);
        }
        this.sourceDir = omgaudioDir;
		this.gotkey = true; // Gen3 devices always have a key !!
        
        // Update space
        Java6ToolBox.FileSpaceInfo spaceInfo = Java6ToolBox.getFileSpaceInfo(sourceDir);
        usableSpace = spaceInfo.getUsableSpace();
        totalSpace = spaceInfo.getTotalSpace();

        // Set up the database
        dataBase = new OmaDataBaseGen3(omgaudioDir);
        
        // Set the generation
        generation = 3;
        
        // Load the key
        loadKey(sourceDir.getParent());
        
        // Fill in the title's list
        loadTitlesFromDevice();
    }
    
/* METHODS */                
	/*
	 * Trys to locate the DvID.dat file and load the key.
	 */
	protected void loadKey(String playerPath)
	{
		try
		{
			testKeyFileName(playerPath + "/DvID.dat");
			testKeyFileName(playerPath + "/DvID.DAT");
			testKeyFileName(playerPath + "/OMGAUDIO/DvID.dat");
			testKeyFileName(playerPath + "/OMGAUDIO/DvID.DAT");
			testKeyFileName(playerPath + "/omgaudio/DvID.dat");
			testKeyFileName(playerPath + "/omgaudio/DvID.DAT");
			testKeyFileName(playerPath + "/MP3FM/DvID.dat");
			testKeyFileName(playerPath + "/MP3FM/DvID.DAT");
			testKeyFileName(playerPath + "/mp3fm/DvID.dat");
			testKeyFileName(playerPath + "/mp3fm/DvID.DAT");
			testKeyFileName(playerPath + "/JSYMPHONIC/DvID.DAT");
			testKeyFileName(playerPath + "/JSYMPHONIC/DvID.dat");
			testKeyFileName(playerPath + "/jsymphonic/DvID.DAT");
			testKeyFileName(playerPath + "/jsymphonic/DvID.dat");
		}
		catch (KeyFileFoundException e)
		{
			//A key file has bee found, now read the key
			try
			{
                byte[] bytesKey = new byte[4]; // Tab of bytes to read the key from the file

				// Open file in stream mode
				// Is this really necessary (toURI.toURL)?
				InputStream dvid_datStream = new File(e.getMessage()).toURI().toURL().openStream();

				// Skip the first useless bytes
				dvid_datStream.skip(10);
				// Read the key
				// Save for compatibility with unmodified methods
				dvid_datStream.read(bytesKey);

				//Extract the DvID key into a uint:
				this.uintKey = (0x0FFL & ((int)bytesKey[3]));
				this.uintKey += 0x0100L * (0x0FFL & ((int)bytesKey[2]));
				this.uintKey += 0x010000L * (0x0FFL & ((int)bytesKey[1]));
				this.uintKey += 0x01000000L * (0x0FFL & ((int)bytesKey[0]));

				// Debug info
                logger.fine("A key has been read: "+ uintKey);

				// Close the stream
				dvid_datStream.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}


	/*
	 * Exception to use when a key file is found
	 */
	private class KeyFileFoundException extends Exception
	{
		KeyFileFoundException(String s)
		{
			super(s);
		}
	}

	/*
	 * Check for the file and throw an exception if it exists
	 */
	public void testKeyFileName(String keyFilePath)
		throws KeyFileFoundException
	{
		File testFile = new File(keyFilePath);
		if(testFile.exists())
			throw new KeyFileFoundException(keyFilePath);
	}
}
