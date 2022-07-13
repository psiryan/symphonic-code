/*
 * OmaDataBaseGen4.java
 *
 * Created on 20 mai 2007, 10:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.system.sony.nw;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import org.naurd.media.jsymphonic.manager.JSymphonic;
import org.naurd.media.jsymphonic.title.Title;

/**
 *An instance of the OmaDataBaseGen4 class describes a dataBase on a 4th generation Sony devices.
 *
 *This class includes methods for building the dataBase and writing it to the config files in the device.
 *
 *
 *@version 06.06.2007
 *@author Nicolas Cardoso
 */
public class OmaDataBaseGen4 implements OmaDataBase{
    private java.io.File omgaudioDir;
    private SymphonicMap titles;
    private SymphonicMap artists;
    private SymphonicMap albums;
    private SymphonicMap genres;
    private Map titleKeys;
    private Map titleRefs;
    int magicKey;
    
    
    /* Constructors */
    /**
    *Allows to create an instance of GenericNW from an existing device.
    *
    *@param omgaudioDir The directory OMGAUDIO from the device.
    *@param sourceName The name of the source, i.e. the name of the device.
    *@param sourceDesc The description of the source.
    *@param sourceGeneration The generation of the source (value can be 1, 2, 3 or 4 but only 4 is supported for the moment).
    *@param sourceIcon The icon of the source.
    */
    public OmaDataBaseGen4(java.io.File omgaudioDir) {
        this.omgaudioDir = omgaudioDir;
        titles = new SymphonicMap();
        artists = new SymphonicMap();
        albums = new SymphonicMap();
        genres = new SymphonicMap();
        titleKeys = new HashMap();
        titleRefs = new HashMap();
//        magicKey = 0x0380d910;
        magicKey = 0;
    }
   

    /* Methods */
    
    public void addTitleWithTitleId(Title title, int titleId) {
        titles.put(title, titleId);
    }
   
    public void removeTitle(Title titleToRemove) {
        titles.remove(titleToRemove);
    }
    
    /**
    *
    *
    *
    *
    public Title[] getTitles() {
        //Get the number of titles
        int count = titles.size();
        //Create the vector to return
        Title[] titlesToReturn = new org.naurd.media.symphonic.title.Title[count];
        
        // Get all the title IDs from titles (= all the titles)
        Set tmpTitles = titles.keySet();
        
        //Convert the set to an array
        Iterator it = tmpTitles.iterator();
        int i = 0;
        while( it.hasNext() ) {
            titlesToReturn[i] = (Title)it.next();
            i++;
        }

        return titlesToReturn;        
    }*/
    public SymphonicMap getTitles() {
        return titles;
    }
    
    
    /**
    *Obtains the title ID associated to a title.
    *
    *@param The title which the title ID is wanted.
    *@return The title ID.
    */   
    public int getTitleId(Title title) {
        return (Integer)titles.getValue(title);
    }

    public int getFreeTitleId() {
        int i = 1;
        
        while(titles.containsValue(i)) { //search the first ID unused
            i++;
        }
        
        return i;
    }

    public void clear() {
        titles.clear();
        artists.clear();
        albums.clear();
        genres.clear();
        titleKeys.clear();
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
        genres.clear();
        titleKeys.clear();
        
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

            // Update genre list
            if(!genres.containsKey(genre)) {
                // If the genre doesn't exist
                // Get a free ID
                genreId = this.getFreeGenreId();
                // Add the genre
                genres.put(genre, genreId);
            }
            
            // Update titleKey list
            Random rand = new Random();
            titleKey = 0;
//            titleKey = rand.nextInt(1000000);
            titleKeys.put(titleToUpdate, titleKey);
            
            // Update titleRef list
//            titleRef = new TitleRef(rand.nextInt(1000000),rand.nextInt(1000000),rand.nextInt(1000000),rand.nextInt(1000000),rand.nextInt(1000000),rand.nextInt(1000000));
            titleRef = new TitleRef(0,0,0,0,0,0);
            titleRefs.put(titleToUpdate, titleRef);
            
        }
    }
    
    /**
    *Writes the dataBase to the corresponding config files in the device.
    */
    public void write(JSymphonic jsymphonic, Double increment) {
        // Write file 00GRTLST ////////////////////////////////////////////////////////////
        File table0 = new File(omgaudioDir + "/00GTRLST.DAT");
        RandomAccessFile rafTable0;
        
        if( !table0.exists() ) { //Write this file only if it doesn't exist (TODO or if the user want to force rewrite config files)
            try{ //Create new file
                table0.createNewFile();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            
            // Write data in the file :
            try{
                rafTable0 = new RandomAccessFile(table0, "rw"); //Open the file in RAF
                
                // Header
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
            
        }
        // Update progress bar in GUI
        jsymphonic.increaseTransfertProgressBar(increment);


        // Write files 01TREE01 & 03GINF01 ////////////////////////////////////////////////////////////
        File table11 = new File(omgaudioDir + "/01TREE01.DAT");
        File table31 = new File(omgaudioDir + "/03GINF01.DAT");
        RandomAccessFile rafTable11;
        RandomAccessFile rafTable31;
        
        try{ //Create new file
            if( table11.exists() ) {
                table11.delete(); // Delete file if it exist
            }
            table11.createNewFile(); // Create a new file

            if( table31.exists() ) {
                table31.delete(); // Delete file if it exist
            }
            table31.createNewFile(); // Create a new file
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // Write data in the file :
        try{
            rafTable11 = new RandomAccessFile(table11, "rw"); //Open the file in RAF
            rafTable31 = new RandomAccessFile(table31, "rw"); //Open the file in RAF

            // Header 11
            OmaDataBaseTools.WriteTableHeader(rafTable11, "TREE", 2); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable11, "GPLB", 0x30, 0x4010); //Write first class description
            int class111Length = titles.size()*0x2 + 0x10; // Calcul class's length
            class111Length += 0x10 - (class111Length % 0x10); // Get an "entire" number
            OmaDataBaseTools.WriteClassDescription(rafTable11, "TPLB", 0x4040, class111Length); //Write second class description

            // Header 31
            OmaDataBaseTools.WriteTableHeader(rafTable31, "GPIF", 1); //Write table header
            // ClassDescription can't be written now

            //Sort the titles in the right order
            List sortedTitles = OmaDataBaseTools.sortByTitleId(titles);
            
            //Get the information needed
            List albumsId = new ArrayList();
            List titlesIdInTPLBlist = new ArrayList();
            List albumsSorted = new ArrayList();
            List artistsSorted = new ArrayList();
            List genresSorted = new ArrayList();
            List titleKeysSorted = new ArrayList();
            String albumName;
            Title tempTitle;
            int tempKey;
            int j = 1;
            
//            if(sortedTitles.size() < 1) { return;} //If there is no title, there is no use to create database
            tempTitle = (Title) sortedTitles.get(0);
            albumName = tempTitle.getAlbum();

            albumsId.add(0, albums.get(albumName));
            titlesIdInTPLBlist.add(0, 1);
            albumsSorted.add(0, tempTitle.getAlbum()); 
            artistsSorted.add(0, tempTitle.getArtist()); 
            genresSorted.add(0, tempTitle.getGenre());
            titleKeysSorted.add(0, titleKeys.get(tempTitle));
            
            for( int i = 1; i < sortedTitles.size(); i++ ) {//For each title :
                tempTitle = (Title) sortedTitles.get(i);
                albumName = tempTitle.getAlbum();

                if( !albumsSorted.get(j-1).equals(albumName) ) { //If current album isn't the same as the last one
                    albumsId.add(j, albums.get(albumName));
                    titlesIdInTPLBlist.add(j, i+1);
                    albumsSorted.add(j, tempTitle.getAlbum()); 
                    artistsSorted.add(j, tempTitle.getArtist()); 
                    genresSorted.add(j, tempTitle.getGenre());
                    titleKeysSorted.add(j, titleKeys.get(tempTitle));
                    j++;
                }
                else { //If it's still the same album, increase the key of this album with this title
                    tempKey = (Integer)titleKeysSorted.get(j-1) + (Integer)titleKeys.get(tempTitle);
                    titleKeysSorted.remove(j-1);
                    titleKeysSorted.add(j-1, tempKey);
                }
            }
            //End of 'Get the information needed'
            
            // Header 31
            OmaDataBaseTools.WriteClassDescription(rafTable31, "GPFB", 0x20, albumsId.size()*0x310 + 0x10); //Write first class description

            //11-Class 1    
            OmaDataBaseTools.WriteClassHeader(rafTable11, "GPLB", albumsId.size(), 0x8, albumsId.size(), 0); //Write first class header
            //31-Class 1    
            OmaDataBaseTools.WriteClassHeader(rafTable31, "GPFB", albumsId.size(), 0x310); //Write first class header
            
            //Fill in elements in 11-Class 1 & 31-Class 1
            for( int i = 0; i <albumsId.size(); i++ ) {
                OmaDataBaseTools.WriteGPLBelement(rafTable11, (Integer)albumsId.get(i), (Integer)titlesIdInTPLBlist.get(i));
                OmaDataBaseTools.WriteGPFBelement(rafTable31, (Integer)titleKeysSorted.get(i), (String)albumsSorted.get(i), (String)artistsSorted.get(i), (String)genresSorted.get(i));
            }
            OmaDataBaseTools.WriteZeros(rafTable11, 0x4010 - 0x10 - (0x8 * albumsId.size())); // Fill in the class with zeros

            //11-Class 2    
            OmaDataBaseTools.WriteClassHeader(rafTable11, "TPLB", sortedTitles.size(), 0x2, sortedTitles.size(), 0); //Write first class header
            
            //Fill in elements in 11-Class 2
            for( int i = 0; i <sortedTitles.size(); i++ ) {
                tempTitle = (Title) sortedTitles.get(i);
                rafTable11.write(OmaDataBaseTools.int2bytes((Integer)titles.get(tempTitle), 2));
            }
            OmaDataBaseTools.WriteZeros(rafTable11, 0x10 - ((titles.size()*0x2) % 0x10)); // Fill in the class with zeros

            rafTable11.close();
            rafTable31.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        // Update progress bar in GUI
        jsymphonic.increaseTransfertProgressBar(increment);
        
        
        // Write files 01TREE02 & 03GINF02 ////////////////////////////////////////////////////////////
        File table12 = new File(omgaudioDir + "/01TREE02.DAT");
        File table32 = new File(omgaudioDir + "/03GINF02.DAT");
        RandomAccessFile rafTable12;
        RandomAccessFile rafTable32;
        
        try{ //Create new file
            if( table12.exists() ) {
                table12.delete(); // Delete file if it exist
            }
            table12.createNewFile(); // Create a new file

            if( table32.exists() ) {
                table32.delete(); // Delete file if it exist
            }
            table32.createNewFile(); // Create a new file
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // Write data in the file :
        try{
            rafTable12 = new RandomAccessFile(table12, "rw"); //Open the file in RAF
            rafTable32 = new RandomAccessFile(table32, "rw"); //Open the file in RAF

            // Header 12
            OmaDataBaseTools.WriteTableHeader(rafTable12, "TREE", 2); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable12, "GPLB", 0x30, 0x4010); //Write first class description
            int class121Length = titles.size()*0x2 + 0x10; // Calcul class's length
            class121Length += 0x10 - (class121Length % 0x10); // Get an "entire" number
            OmaDataBaseTools.WriteClassDescription(rafTable12, "TPLB", 0x4040, class121Length); //Write second class description

            // Header 32
            OmaDataBaseTools.WriteTableHeader(rafTable32, "GPIF", 1); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable32, "GPFB", 0x20, artists.maxValue()*0x90 + 0x10); //Write first class description

            //Sort the titles in the right order
            List sortedTitles = OmaDataBaseTools.sortByArtistTitle(titles);
            
            //Get the information needed
            List artistsId = new ArrayList();
            List titlesIdInTPLBlist = new ArrayList();
            Map titleKeysSorted = new HashMap();
            String aristNameOfCurrentTitle, aristNameOfLastTitle;
            Title tempTitle;
            int tempKey;
            int j = 1;
            
            tempTitle = (Title) sortedTitles.get(0);
            aristNameOfCurrentTitle = tempTitle.getArtist();

            artistsId.add(0, artists.get(aristNameOfCurrentTitle));
            titlesIdInTPLBlist.add(0, 1);
            titleKeysSorted.put(aristNameOfCurrentTitle, titleKeys.get(tempTitle));
            
            aristNameOfLastTitle = aristNameOfCurrentTitle;
            
            for( int i = 1; i < sortedTitles.size(); i++ ) {//For each title :
                tempTitle = (Title) sortedTitles.get(i);
                aristNameOfCurrentTitle = tempTitle.getArtist();

                if( !aristNameOfLastTitle.equals(aristNameOfCurrentTitle) ) { //If current artist isn't the same as the last one
                    artistsId.add(j, artists.get(aristNameOfCurrentTitle));
                    titlesIdInTPLBlist.add(j, i+1);
                    titleKeysSorted.put(aristNameOfCurrentTitle, titleKeys.get(tempTitle));
                    
                    aristNameOfLastTitle = aristNameOfCurrentTitle;
                    j++;
                }
                else { //If it's still the same artist, increase the key of this artist with this title
                    tempKey = (Integer)titleKeysSorted.get(aristNameOfCurrentTitle) + (Integer)titleKeys.get(tempTitle);
                    titleKeysSorted.put(aristNameOfCurrentTitle, tempKey);
                }
            }
            //End of 'Get the information needed'

            //12-Class 1    
            OmaDataBaseTools.WriteClassHeader(rafTable12, "GPLB", artistsId.size(), 0x8, artistsId.size(), 0); //Write first class header
            //32-Class 1    
            OmaDataBaseTools.WriteClassHeader(rafTable32, "GPFB", artists.maxValue(), 0x90); //Write first class header
            
            //Fill in elements in 12-Class 1
            for( int i = 0; i < artistsId.size(); i++ ) {
                OmaDataBaseTools.WriteGPLBelement(rafTable12, (Integer)artistsId.get(i), (Integer)titlesIdInTPLBlist.get(i));
            }
            OmaDataBaseTools.WriteZeros(rafTable12, 0x4010 - 0x10 - (0x8 * artistsId.size())); // Fill in the class with zeros
            
            //Fill in elements in 32-Class 1
            String artistNameTemp;
            for( int i = 1; i <= artists.maxValue(); i++) {
                artistNameTemp = (String)artists.getKey(i);
                
                if( artistNameTemp == null) {
                    OmaDataBaseTools.WriteGPFBelement(rafTable32, 0, "");
                }
                else{
                    OmaDataBaseTools.WriteGPFBelement(rafTable32, (Integer)titleKeysSorted.get(artistNameTemp), artistNameTemp);
                }
            }

            //12-Class 2    
            OmaDataBaseTools.WriteClassHeader(rafTable12, "TPLB", sortedTitles.size(), 0x2, sortedTitles.size(), 0); //Write first class header
            
            //Fill in elements in 12-Class 2
            for( int i = 0; i < sortedTitles.size(); i++ ) {
                tempTitle = (Title) sortedTitles.get(i);
                rafTable12.write(OmaDataBaseTools.int2bytes((Integer)titles.get(tempTitle), 2));
            }
            OmaDataBaseTools.WriteZeros(rafTable12, 0x10 - ((sortedTitles.size()*0x2) % 0x10)); // Fill in the class with zeros

            rafTable12.close();
            rafTable32.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        // Update progress bar in GUI
        jsymphonic.increaseTransfertProgressBar(increment);
        
        
        // Write files 01TREE03 & 03GINF03 ////////////////////////////////////////////////////////////
        File table13 = new File(omgaudioDir + "/01TREE03.DAT");
        File table33 = new File(omgaudioDir + "/03GINF03.DAT");
        RandomAccessFile rafTable13;
        RandomAccessFile rafTable33;
        
        try{ //Create new file
            if( table13.exists() ) {
                table13.delete(); // Delete file if it exist
            }
            table13.createNewFile(); // Create a new file

            if( table33.exists() ) {
                table33.delete(); // Delete file if it exist
            }
            table33.createNewFile(); // Create a new file
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // Write data in the file :
        try{
            rafTable13 = new RandomAccessFile(table13, "rw"); //Open the file in RAF
            rafTable33 = new RandomAccessFile(table33, "rw"); //Open the file in RAF

            // Header 13
            OmaDataBaseTools.WriteTableHeader(rafTable13, "TREE", 2); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable13, "GPLB", 0x30, 0x4010); //Write first class description
            int class131Length = titles.size()*0x2 + 0x10; // Calcul class's length
            class131Length += 0x10 - (class131Length % 0x10); // Get an "entire" number
            OmaDataBaseTools.WriteClassDescription(rafTable13, "TPLB", 0x4040, class131Length); //Write second class description

            // Header 33
            OmaDataBaseTools.WriteTableHeader(rafTable33, "GPIF", 1); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable33, "GPFB", 0x20, albums.maxValue()*0x90 + 0x10); //Write first class description

            //Sort the titles in the right order
            List sortedTitles = OmaDataBaseTools.sortByAlbumTitleNumber(titles);
            
            //Get the information needed
            List albumsId = new ArrayList();
            List titlesIdInTPLBlist = new ArrayList();
            Map titleKeysSorted = new HashMap();
            String albumNameOfCurrentTitle, albumNameOfLastTitle;
            Title tempTitle;
            int tempKey;
            int j = 1;
            
            tempTitle = (Title)sortedTitles.get(0);
            albumNameOfCurrentTitle = tempTitle.getAlbum();

            albumsId.add(0, albums.get(albumNameOfCurrentTitle));
            titlesIdInTPLBlist.add(0, 1);
            titleKeysSorted.put(albumNameOfCurrentTitle, titleKeys.get(tempTitle));
            
            albumNameOfLastTitle = albumNameOfCurrentTitle;
            
            for( int i = 1; i < sortedTitles.size(); i++ ) {//For each title :
                tempTitle = (Title) sortedTitles.get(i);
                albumNameOfCurrentTitle = tempTitle.getAlbum();

                if( !albumNameOfLastTitle.equals(albumNameOfCurrentTitle) ) { //If current artist isn't the same as the last one
                    albumsId.add(j, albums.get(albumNameOfCurrentTitle));
                    titlesIdInTPLBlist.add(j, i+1);
                    titleKeysSorted.put(albumNameOfCurrentTitle, titleKeys.get(tempTitle));
                    
                    albumNameOfLastTitle = albumNameOfCurrentTitle;
                    j++;
                }
                else { //If it's still the same artist, increase the key of this artist with this title
                    tempKey = (Integer)titleKeysSorted.get(albumNameOfCurrentTitle) + (Integer)titleKeys.get(tempTitle);
                    titleKeysSorted.put(albumNameOfCurrentTitle, tempKey);
                }
            }
            //End of 'Get the information needed'

            //13-Class 1    
            OmaDataBaseTools.WriteClassHeader(rafTable13, "GPLB", albumsId.size(), 0x8, albumsId.size(), 0); //Write first class header
            //33-Class 1    
            OmaDataBaseTools.WriteClassHeader(rafTable33, "GPFB", albums.maxValue(), 0x90); //Write first class header
            
            //Fill in elements in 13-Class 1
            for( int i = 0; i < albumsId.size(); i++ ) {
                OmaDataBaseTools.WriteGPLBelement(rafTable13, (Integer)albumsId.get(i), (Integer)titlesIdInTPLBlist.get(i));
            }
            OmaDataBaseTools.WriteZeros(rafTable13, 0x4010 - 0x10 - (0x8 * albumsId.size())); // Fill in the class with zeros
            
            //Fill in elements in 33-Class 1
            String albumNameTemp;
            for( int i = 1; i <= albums.maxValue(); i++) {
                albumNameTemp = (String)albums.getKey(i);
                
                if( albumNameTemp == null) {
                    OmaDataBaseTools.WriteGPFBelement(rafTable33, 0, "");
                }
                else{
//                    OmaDataBaseTools.WriteGPFBelement(rafTable33, (Integer)titleKeysSorted.get(albumNameTemp), albumNameTemp);
                    OmaDataBaseTools.WriteGPFBelement(rafTable33, 0, albumNameTemp);
                }
            }

            //13-Class 2    
            OmaDataBaseTools.WriteClassHeader(rafTable13, "TPLB", sortedTitles.size(), 0x2, sortedTitles.size(), 0); //Write first class header
            
            //Fill in elements in 13-Class 2
            for( int i = 0; i < sortedTitles.size(); i++ ) {
                tempTitle = (Title) sortedTitles.get(i);
                rafTable13.write(OmaDataBaseTools.int2bytes((Integer)titles.get(tempTitle), 2));
            }
            OmaDataBaseTools.WriteZeros(rafTable13, 0x10 - ((sortedTitles.size()*0x2) % 0x10)); // Fill in the class with zeros

            rafTable13.close();
            rafTable33.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        // Update progress bar in GUI
        jsymphonic.increaseTransfertProgressBar(increment);

        
        // Write files 01TREE04 & 03GINF04 ////////////////////////////////////////////////////////////
        File table14 = new File(omgaudioDir + "/01TREE04.DAT");
        File table34 = new File(omgaudioDir + "/03GINF04.DAT");
        RandomAccessFile rafTable14;
        RandomAccessFile rafTable34;
        
        try{ //Create new file
            if( table14.exists() ) {
                table14.delete(); // Delete file if it exist
            }
            table14.createNewFile(); // Create a new file

            if( table34.exists() ) {
                table34.delete(); // Delete file if it exist
            }
            table34.createNewFile(); // Create a new file
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // Write data in the file :
        try{
            rafTable14 = new RandomAccessFile(table14, "rw"); //Open the file in RAF
            rafTable34 = new RandomAccessFile(table34, "rw"); //Open the file in RAF

            // Header 14
            OmaDataBaseTools.WriteTableHeader(rafTable14, "TREE", 2); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable14, "GPLB", 0x30, 0x4010); //Write first class description
            int class141Length = titles.size()*0x2 + 0x10; // Calcul class's length
            class141Length += 0x10 - (class141Length % 0x10); // Get an "entire" number
            OmaDataBaseTools.WriteClassDescription(rafTable14, "TPLB", 0x4040, class141Length); //Write second class description

            // Header 34
            OmaDataBaseTools.WriteTableHeader(rafTable34, "GPIF", 1); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable34, "GPFB", 0x20, genres.maxValue()*0x90 + 0x10); //Write first class description

            //Sort the titles in the right order
            List sortedTitles = OmaDataBaseTools.sortByGenreTitle(titles);
            
            //Get the information needed
            List genresId = new ArrayList();
            List titlesIdInTPLBlist = new ArrayList();
            Map titleKeysSorted = new HashMap();
            String genreOfCurrentTitle, genreOfLastTitle;
            Title tempTitle;
            int tempKey;
            int j = 1;
            
            tempTitle = (Title)sortedTitles.get(0);
            genreOfCurrentTitle = tempTitle.getGenre();

            genresId.add(0, genres.get(genreOfCurrentTitle));
            titlesIdInTPLBlist.add(0, 1);
            titleKeysSorted.put(genreOfCurrentTitle, titleKeys.get(tempTitle));
            
            genreOfLastTitle = genreOfCurrentTitle;
            
            for( int i = 1; i < sortedTitles.size(); i++ ) {//For each title :
                tempTitle = (Title) sortedTitles.get(i);
                genreOfCurrentTitle = tempTitle.getGenre();

                if( !genreOfLastTitle.equals(genreOfCurrentTitle) ) { //If current artist isn't the same as the last one
                    genresId.add(j, genres.get(genreOfCurrentTitle));
                    titlesIdInTPLBlist.add(j, i+1);
                    titleKeysSorted.put(genreOfCurrentTitle, titleKeys.get(tempTitle));
                    
                    genreOfLastTitle = genreOfCurrentTitle;
                    j++;
                }
                else { //If it's still the same artist, increase the key of this artist with this title
                    tempKey = (Integer)titleKeysSorted.get(genreOfCurrentTitle) + (Integer)titleKeys.get(tempTitle);
                    titleKeysSorted.put(genreOfCurrentTitle, tempKey);
                }
            }
            //End of 'Get the information needed'

            //14-Class 1    
            OmaDataBaseTools.WriteClassHeader(rafTable14, "GPLB", genresId.size(), 0x8, genresId.size(), 0); //Write first class header
            //34-Class 1    
            OmaDataBaseTools.WriteClassHeader(rafTable34, "GPFB", genres.maxValue(), 0x90); //Write first class header
            
            //Fill in elements in 14-Class 1
            for( int i = 0; i < genresId.size(); i++ ) {
                OmaDataBaseTools.WriteGPLBelement(rafTable14, (Integer)genresId.get(i), (Integer)titlesIdInTPLBlist.get(i));
            }
            OmaDataBaseTools.WriteZeros(rafTable14, 0x4010 - 0x10 - (0x8 * genresId.size())); // Fill in the class with zeros
            
            //Fill in elements in 34-Class 1
            String genreTemp;
            for( int i = 1; i <= genres.maxValue(); i++) {
                genreTemp = (String)genres.getKey(i);
                
                if( genreTemp == null) {
                    OmaDataBaseTools.WriteGPFBelement(rafTable34, 0, "");
                }
                else{
                    OmaDataBaseTools.WriteGPFBelement(rafTable34, (Integer)titleKeysSorted.get(genreTemp), genreTemp);
                }
            }

            //14-Class 2    
            OmaDataBaseTools.WriteClassHeader(rafTable14, "TPLB", sortedTitles.size(), 0x2, sortedTitles.size(), 0); //Write first class header
            
            //Fill in elements in 14-Class 2
            for( int i = 0; i < sortedTitles.size(); i++ ) {
                tempTitle = (Title) sortedTitles.get(i);
                rafTable14.write(OmaDataBaseTools.int2bytes((Integer)titles.get(tempTitle), 2));
            }
            OmaDataBaseTools.WriteZeros(rafTable14, 0x10 - ((sortedTitles.size()*0x2) % 0x10)); // Fill in the class with zeros

            rafTable14.close();
            rafTable34.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        // Update progress bar in GUI
        jsymphonic.increaseTransfertProgressBar(increment);

        
        // Write files 01TREE22 & 03GINF22 ////////////////////////////////////////////////////////////
        File table122 = new File(omgaudioDir + "/01TREE22.DAT");
        File table322 = new File(omgaudioDir + "/03GINF22.DAT");
        RandomAccessFile rafTable122;
        RandomAccessFile rafTable322;
        
        try{ //Create new file
            if( table122.exists() ) {
                table122.delete(); // Delete file if it exist
            }
            table122.createNewFile(); // Create a new file

            if( table322.exists() ) {
                table322.delete(); // Delete file if it exist
            }
            table322.createNewFile(); // Create a new file
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // Write data in the file :
        try{
            rafTable122 = new RandomAccessFile(table122, "rw"); //Open the file in RAF
            rafTable322 = new RandomAccessFile(table322, "rw"); //Open the file in RAF

            // Header 122
            OmaDataBaseTools.WriteTableHeader(rafTable122, "TREE", 2); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable122, "GPLB", 0x30, 0x10); //Write first class description
            OmaDataBaseTools.WriteClassDescription(rafTable122, "TPLB", 0x40, 0x10); //Write second class description

            // Header 322
            OmaDataBaseTools.WriteTableHeader(rafTable322, "GPIF", 1); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable322, "GPFB", 0x20, 0x10); //Write first class description
            

            //122-Class 1    
            OmaDataBaseTools.WriteClassHeader(rafTable122, "GPLB", 0, 0x8, 0, 0); //Write first class header
            //322-Class 1    
            OmaDataBaseTools.WriteClassHeader(rafTable322, "GPFB", 0, 0x310); //Write first class header
            
            //122-Class 2    
            OmaDataBaseTools.WriteClassHeader(rafTable122, "TPLB", 0, 0x2, 0, 0); //Write first class header

            rafTable122.close();
            rafTable322.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        // Update progress bar in GUI
        jsymphonic.increaseTransfertProgressBar(increment);

        
        // Write files 01TREE2D & 03GINF2D ////////////////////////////////////////////////////////////
        File table12D = new File(omgaudioDir + "/01TREE2D.DAT");
        File table32D = new File(omgaudioDir + "/03GINF2D.DAT");
        RandomAccessFile rafTable12D;
        RandomAccessFile rafTable32D;
        
        try{ //Create new file
            if( table12D.exists() ) {
                table12D.delete(); // Delete file if it exist
            }
            table12D.createNewFile(); // Create a new file

            if( table32D.exists() ) {
                table32D.delete(); // Delete file if it exist
            }
            table32D.createNewFile(); // Create a new file
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // Write data in the file :
        try{
            rafTable12D = new RandomAccessFile(table12D, "rw"); //Open the file in RAF
            rafTable32D = new RandomAccessFile(table32D, "rw"); //Open the file in RAF

            // Header 12D
            OmaDataBaseTools.WriteTableHeader(rafTable12D, "TREE", 2); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable12D, "GPLB", 0x30, 0x4010); //Write first class description
            int class12D1Length = titles.size()*0x2 + 0x10; // Calcul class's length
            class12D1Length += 0x10 - (class12D1Length % 0x10); // Get an "entire" number
            OmaDataBaseTools.WriteClassDescription(rafTable12D, "TPLB", 0x4040, class12D1Length); //Write second class description

            // Header 32D
            OmaDataBaseTools.WriteTableHeader(rafTable32D, "GPIF", 1); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable32D, "GPFB", 0x20, (artists.size() + albums.size() + 1)*0x110 + 0x10); //Write first class description

            //12D-Class 1    
            OmaDataBaseTools.WriteClassHeader(rafTable12D, "GPLB", artists.size() + albums.size() + 1, 0x8, artists.size() + albums.size() + 1, 0); //Write first class header

            //32D-Class 1    
            OmaDataBaseTools.WriteClassHeader(rafTable32D, "GPFB", artists.size() + albums.size() + 1, 0x110); //Write first class header

            //Sort the titles in the right order
            List sortedTitles = OmaDataBaseTools.sortByArtistAlbumTitleNumber(titles);
            
            //Fill in elements in 12D-Class 1 & 32D-Class 1
            //First element is empty
            OmaDataBaseTools.WriteGPLBelement(rafTable12D, 1, 0);
            OmaDataBaseTools.WriteGPFBelement(rafTable32D, 0, "", "");
            
            //Second element
            Title tempTitle;
            String artistName, lastArtistName, albumName, lastAlbumName;
            int elementKey;
            int titlesIdInTPLBlist = 1;
            int j = 3;
            
            tempTitle = (Title)sortedTitles.get(0);
            artistName = tempTitle.getArtist();
            albumName = tempTitle.getAlbum();
            elementKey = (Integer)titleKeys.get(tempTitle);
            OmaDataBaseTools.WriteGPLBelement(rafTable12D, 2, 0);
            OmaDataBaseTools.WriteGPFBelement(rafTable32D, 0, artistName, artistName);
            
            lastArtistName = artistName;
            lastAlbumName = albumName;
            
            //Next elements
            for( int i = 0; i < sortedTitles.size(); i++ ) {
                tempTitle = (Title)sortedTitles.get(i);
                artistName = tempTitle.getArtist();
                albumName = tempTitle.getAlbum();
                
                if( lastArtistName.equals(artistName) ) {
                    if( lastAlbumName.equals(albumName) ) {
                        elementKey += (Integer)titleKeys.get(tempTitle);
                    }
                    else{
                            OmaDataBaseTools.WriteGPLBelement2(rafTable12D, j, titlesIdInTPLBlist);
                            OmaDataBaseTools.WriteGPFBelement(rafTable32D, elementKey, lastAlbumName, lastAlbumName);
                            j++;
                            elementKey = (Integer)titleKeys.get(tempTitle);
                            lastAlbumName = albumName;
                            titlesIdInTPLBlist = i + 1;
                    }
                }
                else{
                    // Write the last album
                    OmaDataBaseTools.WriteGPLBelement2(rafTable12D, j, titlesIdInTPLBlist);
                    OmaDataBaseTools.WriteGPFBelement(rafTable32D, elementKey, lastAlbumName, lastAlbumName);
                    j++;
                    elementKey = (Integer)titleKeys.get(tempTitle);
                    lastAlbumName = albumName;
                    titlesIdInTPLBlist = i + 1;
                    
                    // Write the new artist
                    OmaDataBaseTools.WriteGPLBelement(rafTable12D, j, 0);
                    OmaDataBaseTools.WriteGPFBelement(rafTable32D, 0, artistName, artistName);
                    j++;
                    lastArtistName = artistName;
                }
            }
            
            // Last element
            OmaDataBaseTools.WriteGPLBelement2(rafTable12D, j, titlesIdInTPLBlist);
            OmaDataBaseTools.WriteGPFBelement(rafTable32D, elementKey, lastAlbumName, lastAlbumName);
                
            // Fill in the class with zeros
            OmaDataBaseTools.WriteZeros(rafTable12D, 0x4010 - 0x10 - (0x8 * (artists.size() + albums.size() + 1))); 

            //12D-Class 2    
            OmaDataBaseTools.WriteClassHeader(rafTable12D, "TPLB", sortedTitles.size(), 0x2, sortedTitles.size(), 0); //Write first class header
            
            //Fill in elements in 12D-Class 2
            for( int i = 0; i <sortedTitles.size(); i++ ) {
                tempTitle = (Title) sortedTitles.get(i);
                rafTable12D.write(OmaDataBaseTools.int2bytes((Integer)titles.get(tempTitle), 2));
            }
            OmaDataBaseTools.WriteZeros(rafTable12D, 0x10 - ((titles.size()*0x2) % 0x10)); // Fill in the class with zeros

            rafTable12D.close();
            rafTable32D.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        // Update progress bar in GUI
        jsymphonic.increaseTransfertProgressBar(increment);

        
        // Write file 02TREINF ////////////////////////////////////////////////////////////
        File table2 = new File(omgaudioDir + "/02TREINF.DAT");
        RandomAccessFile rafTable2;
        
        try{ //Create new file
            if( table2.exists() ) {
                table2.delete(); // Delete file if it exist
            }
            table2.createNewFile(); // Create a new file
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // Write data in the file :
        try{
            rafTable2 = new RandomAccessFile(table2, "rw"); //Open the file in RAF

            // Header
            OmaDataBaseTools.WriteTableHeader(rafTable2, "GTIF", 1); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable2, "GTFB", 0x20, 0x1f00); //Write first class description

            //Class 1
            OmaDataBaseTools.WriteClassHeader(rafTable2, "GTFB", 0x2d, 0x90); //Write first class header
            
            //Before writing element, need to know the global key
            Iterator itKey = titleKeys.keySet().iterator();
            int globalKey = 0;
            Title titleTemp;
            while( itKey.hasNext() ) {
                titleTemp = (Title)itKey.next();
                globalKey += (Integer)titleKeys.get(titleTemp); 
            }
            
            // Elements 1 to 4
            for( int i = 1; i <= 4; i++) {
                OmaDataBaseTools.WriteGTFBelement(rafTable2, globalKey,"");
            }
            
            // Elements 5 to 0x21
            OmaDataBaseTools.WriteZeros(rafTable2, (0x21 - 0x5 + 1)*0x90);
            
            // Element 0x22
            OmaDataBaseTools.WriteGTFBelement(rafTable2, 0,"");
            
            // Element 0x23 to 0x2c
            OmaDataBaseTools.WriteZeros(rafTable2, (0x2c - 0x23 + 1)*0x90);
            
            // Element 0x2D
            OmaDataBaseTools.WriteGTFBelement(rafTable2, globalKey,"STD_TPE1");
            
            // Fill in the class with zeros
            OmaDataBaseTools.WriteZeros(rafTable2, 0x1f00 - (0x2d*0x90) - 0x10);

            rafTable2.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        // Update progress bar in GUI
        jsymphonic.increaseTransfertProgressBar(increment);

        
        // Write files 04CNTINF ////////////////////////////////////////////////////////////
        File table4 = new File(omgaudioDir + "/04CNTINF.DAT");
        RandomAccessFile rafTable4;
        
        try{ //Create new file
            if( table4.exists() ) {
                table4.delete(); // Delete file if it exist
            }
            table4.createNewFile(); // Create a new file

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // Write data in the file :
        try{
            rafTable4 = new RandomAccessFile(table4, "rw"); //Open the file in RAF

            // Header
            OmaDataBaseTools.WriteTableHeader(rafTable4, "CNIF", 1); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable4, "CNFB", 0x20, titles.maxValue()*0x290 + 0x10); //Write first class description
            OmaDataBaseTools.WriteClassHeader(rafTable4, "CNFB", titles.maxValue(), 0x290); //Write first class header
            
            //Sort the titles in the right order
            List sortedTitles = OmaDataBaseTools.sortByTitleId(titles);
            
            // Write elements
            Title titleTemp;
            
            for( int i = 1; i <= titles.maxValue(); i++ ) {
                titleTemp = (Title)titles.getKey(i);

                if( titleTemp == null ) {
                    OmaDataBaseTools.WriteCNFBelement(rafTable4, 0, 0, "", "", "", "");
                }
                else{
                    OmaDataBaseTools.WriteCNFBelement(rafTable4, magicKey, (Integer)titleKeys.get(titleTemp), titleTemp.getTitle(), titleTemp.getArtist(), titleTemp.getAlbum(), titleTemp.getGenre());
                }
            }

            rafTable4.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        // Update progress bar in GUI
        jsymphonic.increaseTransfertProgressBar(increment);
        
        
        // Write files 05CIDLST ////////////////////////////////////////////////////////////
        File table5 = new File(omgaudioDir + "/05CIDLST.DAT");
        RandomAccessFile rafTable5;
        
        try{ //Create new file
            if( table5.exists() ) {
                table5.delete(); // Delete file if it exist
            }
            table5.createNewFile(); // Create a new file

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // Write data in the file :
        try{
            rafTable5 = new RandomAccessFile(table5, "rw"); //Open the file in RAF

            // Header
            OmaDataBaseTools.WriteTableHeader(rafTable5, "CIDL", 1); //Write table header
            OmaDataBaseTools.WriteClassDescription(rafTable5, "CILB", 0x20, titles.maxValue()*0x30 + 0x10); //Write first class description
            OmaDataBaseTools.WriteClassHeader(rafTable5, "CILB", titles.maxValue(), 0x30); //Write first class header
            
            //Sort the titles in the right order
            List sortedTitles = OmaDataBaseTools.sortByTitleId(titles);
            
            // Write elements
            Title titleTemp;
            TitleRef titleRefTemp;
            
            for( int i = 1; i <= titles.maxValue(); i++ ) {
                titleTemp = (Title)titles.getKey(i);
                
                if( titleTemp == null ) {
                    OmaDataBaseTools.WriteCILBelement(rafTable5, 0, 0, 0, 0, 0, 0);
                }
                else{
                    titleRefTemp = (TitleRef)titleRefs.get(titleTemp);
                    OmaDataBaseTools.WriteCILBelement(rafTable5, titleRefTemp.getPart1(), titleRefTemp.getPart2(), titleRefTemp.getPart3(), titleRefTemp.getPart4(), titleRefTemp.getPart5(), titleRefTemp.getPart6());
                }
            }

            rafTable5.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        // Update progress bar in GUI
        jsymphonic.increaseTransfertProgressBar(increment);
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

    private int getFreeGenreId() {
        int i = 1;
        
        while(genres.containsValue(i)) { //search the first ID unused
            i++;
        }
        
        return i;
    }
}
