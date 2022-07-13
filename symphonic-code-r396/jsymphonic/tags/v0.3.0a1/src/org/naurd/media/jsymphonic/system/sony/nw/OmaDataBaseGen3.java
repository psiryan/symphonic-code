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
 * OmaDataBaseGen3.java
 *
 * Created on 20 mai 2007, 10:12
 *
 */

package org.naurd.media.jsymphonic.system.sony.nw;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import org.naurd.media.jsymphonic.title.Title;
import org.naurd.media.jsymphonic.toolBox.OmaDataBaseToolBox;
import org.naurd.media.jsymphonic.toolBox.TitleRef;

/**
 * An instance of the OmaDataBaseGen3 class describes a dataBase on a 3rd generation of Sony devices. This generation has the following particularities:
 * - database is containing in a OMGAUDIO folder
 * - no cover
 * - no intelligent features
 * - protected players
 *
 * Example of 3rd generation players: NW-HD5, NW-E10x,...
 *
 * 
 * 
 * 
 * @author Nicolas Cardoso
 * @author Daniel Žalar - added events to ensure GUI independancy
 * @version 06.06.2007
 */
public class OmaDataBaseGen3 extends OmaDataBase{
/* FIELDS */
    private java.io.File omgaudioDir; // OMGAUDIO directory containing all the files for the database
    private Map titleKeys; // List of the key of the titles, a key is a number associated with each titles of the database, this doesn't seem to be usefull...
    private Map titleRefs; // List of the references of the titles, as the keys, it doesn't seem to be usefull
    int magicKey; // A special number, unique for each walkman... as keys and references, utility has to be found...
 
    //Other
    private static Logger logger = Logger.getLogger("org.naurd.media.jsymphonic.system.sony.nw.OmaDataBaseGen3");
      
    
/* CONSTRUCTORS */
    /**
     * Allows to create an instance of OmaDataBaseGen3 from an existing device. 
     * The database, once created is empty. Titles list can be filled with the method "addTitleWithTitleId". Before been written to the device, the database should be updated, with "update" method, to fill in the artists, albums,... lists.
     *
     *@param omgaudioDir The directory OMGAUDIO from the device.
     *
     * @author nicolas_cardoso
     */
    public OmaDataBaseGen3(java.io.File omgaudioDir) {
        // Call the super contructor
        super();
        
        // Save the base directory
        this.omgaudioDir = omgaudioDir;
        
        // Instance new empty lists

        titleKeys = new HashMap();
        titleRefs = new HashMap();
        
        // Walkman are working without a valid magic key, so let's keep it to zero 
        magicKey = 0;
    }
   

/* METHODS */ 
    /**
     * Clear the database. This methods empty all the lists.
     *
     *@author nicolas_cardoso
     */
    @Override
    public void clear() {
        // Call the overwritten method
        super.clear();
        
        // Empty other lists
        titleKeys.clear();
    }

    /**
     * Update the database, complete all the other list (than the title list).
     *
     *@author nicolas_cardoso
     */
    public void update() {
        List titlesList; // the list of the titles (in a list object)
        Map albumsOfCurrentArtist = new HashMap(); // the known albums for the artist currently scanned
        Title titleToUpdate; // the title currently scanned
        Iterator it;
        String artist, album, genre, albumTemp; // some string to contains info on the title
        int artistId, albumId, genreId, titleKey, albumSimilarCounter; // some integer to contains info on the title
        TitleRef titleRef; // reference used in the database
        
        // Clear Map (except titles which is the only one up to date)
        artists.clear();
        albums.clear();
        genres.clear();
        titleKeys.clear();
               
        // Get all the title in a good order
        titlesList = OmaDataBaseToolBox.sortByArtistAlbumTitleNumber(titles);
        it = titlesList.iterator();
        
        // For each title
        while( it.hasNext() ) {
            // Get the title
            titleToUpdate = (Title)it.next();
            
            // If the title's status is TOENCODE or TODECODE, it means that another similar title exist in the list (the encoded title), so this one should removed
            if(titleToUpdate.getStatus() == Title.TOENCODE || titleToUpdate.getStatus() == Title.TODECODE) {
                titles.remove(titleToUpdate); // remove the title
                continue; // go to next title 
            }
            
            // Get the information of the title
            artist = titleToUpdate.getArtist();
            album = titleToUpdate.getAlbum();
            genre = titleToUpdate.getGenre();
            
            // Update artist list
            if(!artists.containsKey(artist)) {
                // If the artist doesn't exist
                // Get a free ID
                artistId = super.getFreeArtistId();
                // Add the artist
                artists.put(artist, artistId);
                
                //A new artist has been reached, clear the temp list of albums
                albumsOfCurrentArtist.clear();
            }
            
            // Update album list
            if(!albumsOfCurrentArtist.containsKey(album)) {
                // If the album doesn't exist
                // Get a free ID
                albumId = super.getFreeAlbumId();
                
                // Check if the albumName isn't already used with another artist, if it's the case, search a new name for the album
                albumTemp = album;
                albumSimilarCounter = 2;
                while(albums.containsKey(albumTemp)) {
                    albumTemp = album + " (" + albumSimilarCounter + ")";
                    albumSimilarCounter++;
                }
                
                // Add the album
                albums.put(albumTemp, albumId);
                
                // Add the album to the temporary list
                albumsOfCurrentArtist.put(album, albumId);
            }

            // Update genre list
            if(!genres.containsKey(genre)) {
                // If the genre doesn't exist
                // Get a free ID
                genreId = super.getFreeGenreId();
                // Add the genre
                genres.put(genre, genreId);
            }
            
            // Update titleKey list
//            Random rand = new Random();
            titleKey = 0; // Doesn't need to be more precise...
            titleKeys.put(titleToUpdate, titleKey);
            
            // Update titleRef list
            titleRef = new TitleRef(0,0,0,0,0,0); // Doesn't need to be more precise...
            titleRefs.put(titleToUpdate, titleRef);
        }
    }
    
    
    /**
     * Write the database to the player.
     *
     *@param genNw The instance of the Net walkman.
     *
     *@author nicolas_cardoso
     */
    public void write(NWGeneric genNw) {
        int fileCounter = 0; // Counter of the database files scanned
        
        // Inform GUI
        genNw.sendTransferStepStarted(NWGenericListener.UPDATING);
        
        //Backup the database if it is not already done
        File backupDir = new File(omgaudioDir + "/db_backup");
        if(!backupDir.exists()){
            logger.info("Backup of the database has not been found, it is created now.");
            backupDir.mkdir(); // Create the backup directory
            String[] databaseFiles = omgaudioDir.list(); // Search all the files in the database
            for(int i=0; i<databaseFiles.length; i++) {
                String databaseFileName = databaseFiles[i];
                File databaseFile = new File(omgaudioDir + "/" + databaseFileName);
                File databaseFileCopy = new File(omgaudioDir + "/db_backup/" + databaseFileName);
                try {
                    copyFile(databaseFile, databaseFileCopy);
                } catch (IOException ex) {
                    logger.severe("Error while creating backup of the database.");
                }
            }
        }
        
        // Erase all files in the OMGAUDIO folders (which are database files, and the database is going to be re-written) exepct the key file DvID.DAT
        String[] databaseFiles = omgaudioDir.list(); // Search all the files in the database
        for(int i=0; i<databaseFiles.length; i++) {
            String databaseFileName = databaseFiles[i];
            if((databaseFileName.compareTo("DvID.DAT") !=0) || (databaseFileName.compareTo("DvID.dat") !=0) || (databaseFileName.compareTo("ffmpeg.exe") !=0) || (databaseFileName.compareTo("pthreadGC2.dll") !=0) || (databaseFileName.compareTo("ffmpeg-win32.zip") !=0) || (databaseFileName.compareTo("ffmpeg-win32") !=0)){ // If the file is not the key file or the FFMPEG exe, delete it
                File databaseFile = new File(omgaudioDir + "/" + databaseFileName);
                databaseFile.delete();
            }
        }

        // Write 00GRTLST file (info about the database)
        try {
            OmaDataBaseToolBox.write00GRTLST(omgaudioDir);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 00GRTLST file");
            ex.printStackTrace();
        }
        
        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "OOGRTLST");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);

        // Write 01TREE01 and 03GINF01 files (info about the titles)
        try {
            OmaDataBaseToolBox.write01TREE01and03GINF01(omgaudioDir, super.titles, super.albums, titleKeys);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 01TREE01 and 03GINF01 files");
            ex.printStackTrace();
        }
       
        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "01TREE01");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        genNw.sendFileChanged(NWGenericListener.UPDATING, "03GINF01");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
               
        
        // Write 01TREE02 and 03GINF02 files (info about the artists)
        try {
            OmaDataBaseToolBox.write01TREE02and03GINF02(omgaudioDir, super.titles, super.artists, titleKeys);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 01TREE02 and 03GINF02 files");
            ex.printStackTrace();
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "01TREE02");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        genNw.sendFileChanged(NWGenericListener.UPDATING, "03GINF02");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        
        // Write 01TREE03 and 03GINF03 files (info about the albums)
        try {
            OmaDataBaseToolBox.write01TREE03and03GINF03(omgaudioDir, super.titles, super.albums, titleKeys);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 01TREE03 and 03GINF03 files");
            ex.printStackTrace();
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "01TREE03");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        genNw.sendFileChanged(NWGenericListener.UPDATING, "03GINF03");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        
        // Write 01TREE04 and 03GINF04 files (info about the genres)
        try {
            OmaDataBaseToolBox.write01TREE04and03GINF04(omgaudioDir, super.titles, super.genres, titleKeys);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 01TREE04 and 03GINF04 files");
            ex.printStackTrace();
        }
        
        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "01TREE04");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        genNw.sendFileChanged(NWGenericListener.UPDATING, "03GINF04");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        
        // Write 01TREE22 and 03GINF22 files
        try {
            OmaDataBaseToolBox.write01TREE22and03GINF22(omgaudioDir);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 01TREE22 and 03GINF22 files");
            ex.printStackTrace();
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "01TREE22");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        genNw.sendFileChanged(NWGenericListener.UPDATING, "03GINF22");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        
        // Write 01TREE2D and 03GINF2D files
        try {
            OmaDataBaseToolBox.write01TREE2Dand03GINF2D(omgaudioDir, titles, artists, albums, titleKeys);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 01TREE2D and 03GINF2D files");
            ex.printStackTrace();
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "01TREE2D");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        genNw.sendFileChanged(NWGenericListener.UPDATING, "03GINF2D");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        
        // Write 02TREINF file (list of the keys)
        try {
            OmaDataBaseToolBox.write02TREINF(omgaudioDir, titleKeys);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 02TREINF file");
            ex.printStackTrace();
        }
        
        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "02TREINF");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);

        
        // Write 04CNTINF file (list of the titles)
        try {
            OmaDataBaseToolBox.write04CNTINF(omgaudioDir, titles, magicKey, titleKeys);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 04CNTINF file");
            ex.printStackTrace();
        }
        
        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "04CNTINF");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        
        
        // Write 05CIDLST file
        try {
            OmaDataBaseToolBox.write05CIDLST(omgaudioDir, titles, titleRefs);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 05CIDLST file");
            ex.printStackTrace();
        }
        
        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "05CIDLST");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);

        // Database is up to date
        genNw.sendTransferStepFinished(NWGenericListener.UPDATING, NWGenericListener.NO_ERROR);
    }

    @Override
    public int getNumberOfFiles() {
        return 16;
    }

    // TODO documentation
   public static void copyFile(File src, File dest) throws IOException {
      if (!src.exists()) throw new IOException(
         "File not found '" + src.getAbsolutePath() + "'");
      BufferedOutputStream out = new BufferedOutputStream(
         new FileOutputStream(dest));
      BufferedInputStream in = new BufferedInputStream(
         new FileInputStream(src));
         
      byte[] read = new byte[128];
      int len = 128;
      while ((len = in.read(read)) > 0)
         out.write(read, 0, len);
      
      out.flush();
      out.close();
      in.close();
   }
}
