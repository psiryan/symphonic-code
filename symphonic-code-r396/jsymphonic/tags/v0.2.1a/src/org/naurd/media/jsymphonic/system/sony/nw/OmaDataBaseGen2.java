/*
 * OmaDataBaseGen2.java
 *
 * Created on 25 février 2008, 20:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.system.sony.nw;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.naurd.media.jsymphonic.manager.JSymphonic;
import org.naurd.media.jsymphonic.title.Title;

/**
 *
 * @author skiron
 */
public class OmaDataBaseGen2 implements OmaDataBase{
    private java.io.File esys;
    private SymphonicMap titles;
    private SymphonicMap artists;
    private SymphonicMap albums;
    //private Map titleKeys;
    
    /**
     * Creates a new instance of OmaDataBaseGen2
     */
    public OmaDataBaseGen2(java.io.File esys) {
        this.esys = esys;
        titles = new SymphonicMap();
        artists = new SymphonicMap();
        albums = new SymphonicMap();
        //titleKeys = new HashMap();
    }

    public void addTitleWithTitleId(Title title, int titleId) {
        titles.put(title, titleId);
    }

    public void removeTitle(Title titleToRemove) {
        titles.remove(titleToRemove);
    }

    public int getTitleId(Title title) {
        return (Integer)titles.getValue(title);
    }

    public SymphonicMap getTitles() {
         return titles;
    }

    public int getFreeTitleId() {
        int i = 1;
        
        while(titles.containsValue(i)) { //search the first ID unused
            i++;
        }
        
        return i;
    }

    public void write(JSymphonic jsymphonic, Double increment) {
        // Write file PBLIST0 ////////////////////////////////////////////////////////////
        File table0 = new File(esys + "/PBLIST0.DAT");
        RandomAccessFile rafTable0;
        
        try{ //Create new file
            if( table0.exists() ) {
                table0.delete(); // Delete file if it exist
            }
            table0.createNewFile(); // Create a new file
        }
        catch(Exception e) {
            e.printStackTrace();
        }
            
        // Write data in the file :
        try{
            rafTable0 = new RandomAccessFile(table0, "rw"); //Open the file in RAF

            // Header
            String tableName = "WMPLESYS";
            byte[] bytesTableName = tableName.getBytes();
            rafTable0.write(bytesTableName);
            
            OmaDataBaseTools.WriteTableHeader(rafTable0, "GTLT", 2); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable0, "SYSB", 0x30, 0x70); //Write first class description
            OmaDataBaseTools.WriteClassDescription(rafTable0, "GTLB", 0xa0, 0xe20); //Write second class description

            //Class 1
            OmaDataBaseTools.WriteClassHeader(rafTable0, "SYSB", 1, 0x50, 0xd0000000, 0x00000000); //Write first class header
            OmaDataBaseTools.WriteZeros(rafTable0, 6 * 0x10); //Write element 1

            //Class 2
            OmaDataBaseTools.WriteClassHeader(rafTable0, "GTLB", 0x2d, 0x50, 0x00000006, 0x04000000); //Write second class header
            OmaDataBaseTools.WriteGTLBelement(rafTable0, 1, 1, 1, "", "", new byte[0]); //Element 1
            OmaDataBaseTools.WriteGTLBelement(rafTable0, 2, 3, 1, "TPE1", "", new byte[0]); //Element 2
            OmaDataBaseTools.WriteGTLBelement(rafTable0, 3, 3, 1, "TALB", "", new byte[0]); //Element 3
            OmaDataBaseTools.WriteGTLBelement(rafTable0, 4, 3, 1, "TCON", "", new byte[0]); //Element 4
            OmaDataBaseTools.WriteGTLBelement(rafTable0, 0x22, 2, 0, "", "", new byte[0]); //Element 5
            OmaDataBaseTools.WriteGTLBelement(rafTable0, 0x2d, 3, 2, "TPE1", "TALB", ("TRNOTTCCTTCC").getBytes()); //Element 6

            for( int i = 5; i <= 44; i++) {
                if( i == 34 ){
                    continue;
                }
                OmaDataBaseTools.WriteGTLBelement(rafTable0, i, 0, 0, "", "", new byte[0]); //Elements 5 to 44, avoiding 34
            }

            rafTable0.close();
            
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        // Update progress bar in GUI
        jsymphonic.increaseTransfertProgressBar(increment);
    }

    public void update() {
        List titlesList;
        Map albumsOfCurrentArtist = new HashMap();
        Title titleToUpdate;
        Iterator it;
        String artist, album, genre, albumTemp;
        int artistId, albumId, genreId, titleKey, albumSimilarCounter;
        TitleRef titleRef;
        
        // Clear Map (except titles which is the only one up to date)
        artists.clear();
        albums.clear();
        //titleKeys.clear();
        
        // Get all the title in a good order
        titlesList = OmaDataBaseTools.sortByArtistAlbumTitleNumber(titles);
        it = titlesList.iterator();
        
        // For each title
        while( it.hasNext() ) {
            // Get the title
            titleToUpdate = (Title)it.next();
            
            // Get the information of the title
            artist = titleToUpdate.getArtist();
            album = titleToUpdate.getAlbum();
            genre = titleToUpdate.getGenre();
            
            // Update artist list
            if(!artists.containsKey(artist)) {
                // If the artist doesn't exist
                // Get a free ID
                artistId = this.getFreeArtistId();
                // Add the artist
                artists.put(artist, artistId);
                
                //A new artist has been reached, clear the temp list of albums
                albumsOfCurrentArtist.clear();
            }
            
            // Update album list
            if(!albumsOfCurrentArtist.containsKey(album)) {
                // If the album doesn't exist
                // Get a free ID
                albumId = this.getFreeAlbumId();
                
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

//            // Update titleKey list
//            titleKey = 0;
//            titleKeys.put(titleToUpdate, titleKey);
//            
//            // Update titleRef list
//            titleRef = new TitleRef(0,0,0,0,0,0);
//            titleRefs.put(titleToUpdate, titleRef);
        }
    }

    public void clear() {
        titles.clear();
        artists.clear();
        albums.clear();
        //titleKeys.clear();
    }
    

    private int getFreeArtistId() {
        int i = 1;
        
        while(artists.containsValue(i)) { //search the first ID unused
            i++;
        }
        
        return i;
    }

    private int getFreeAlbumId() {
        int i = 1;
        
        while(albums.containsValue(i)) { //search the first ID unused
            i++;
        }
        
        return i;
    }
}
