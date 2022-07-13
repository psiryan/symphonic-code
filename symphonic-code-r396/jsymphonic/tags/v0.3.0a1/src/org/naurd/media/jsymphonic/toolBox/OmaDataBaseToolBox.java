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
 * OmaDataBaseToolBox.java
 *
 * Created on 27 mars 2008, 19:15
 *
 */

package org.naurd.media.jsymphonic.toolBox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.naurd.media.jsymphonic.title.Title;

/**
 * Provides basic function to write the data base on the Sony devices, whatever the generation. This tool box should be replaced by the Sony Library coded in C++ when it will be ready.
 *
 * @author nicolas_cardoso
 */
public class OmaDataBaseToolBox {
    //Other
    private static Logger logger = Logger.getLogger("org.naurd.media.jsymphonic.toolBox.OmaDataBaseToolBox");

    
    
/* CONSTRUCTORS */
    /** 
     * This class is a tool box, it shouldn't be instanced.
     * 
     * @author nicolas_cardoso
     */
    public OmaDataBaseToolBox() {
    }
    
    
    
/* METHODS */ 
    /**
     * Write 00GRTLST file in OMGAUDIO folder. This method should be used while writing the database for generation using a OMGAUDIO folder.
     *
     *@param omgaudioDir Instance of the OMGAUDIO folder where to write the file.
     *
     *@author nicolas_cardoso
     */
    public static void write00GRTLST(File omgaudioDir) throws Exception{
        File table0 = new File(omgaudioDir + "/00gtrlst.dat");
        RandomAccessFile rafTable0;
        
        // Create a new file
        table0.createNewFile();

        // Write data in the file :
        rafTable0 = new RandomAccessFile(table0, "rw"); //Open the file in RAF

        // Header
        WriteTableHeader(rafTable0, "GTLT", 2); //Write table header
        WriteClassDescription(rafTable0, "SYSB", 0x30, 0x70); //Write first class description
        WriteClassDescription(rafTable0, "GTLB", 0xa0, 0xe20); //Write second class description

        //Class 1
        WriteClassHeader(rafTable0, "SYSB", 1, 0x50, 0xd0000000, 0x00000000); //Write first class header
        WriteZeros(rafTable0, 6 * 0x10); //Write element 1

        //Class 2
        WriteClassHeader(rafTable0, "GTLB", 0x2d, 0x50, 0x00000006, 0x04000000); //Write second class header
        WriteGTLBelement(rafTable0, 1, 1, 1, "", "", new byte[0]); //Element 1
        WriteGTLBelement(rafTable0, 2, 3, 1, "TPE1", "", new byte[0]); //Element 2
        WriteGTLBelement(rafTable0, 3, 3, 1, "TALB", "", new byte[0]); //Element 3
        WriteGTLBelement(rafTable0, 4, 3, 1, "TCON", "", new byte[0]); //Element 4
        WriteGTLBelement(rafTable0, 0x22, 2, 0, "", "", new byte[0]); //Element 5
        WriteGTLBelement(rafTable0, 0x2d, 3, 2, "TPE1", "TALB", ("TRNOTTCCTTCC").getBytes()); //Element 6

        for( int i = 5; i <= 44; i++) {
            if( i == 34 ){
                continue;
            }
            WriteGTLBelement(rafTable0, i, 0, 0, "", "", new byte[0]); //Elements 5 to 44, avoiding 34
        }

        rafTable0.close();
    }
    
    
    
    /**
     * Write 01TREE01 and 03GINF01 files in OMGAUDIO folder. This method should be used while writing the database for generation using a OMGAUDIO folder.
     *
     *@param omgaudioDir Instance of the OMGAUDIO folder where to write the file.
     *@param titles List of the titles in the data base.
     *@param albums List of the albums in the data base.
     *@param titleKeys List of the titleKeys in the data base.
     *
     *@author nicolas_cardoso
     */
    public static void write01TREE01and03GINF01(File omgaudioDir, Map titles, Map albums, Map titleKeys) throws Exception{
        File table11 = new File(omgaudioDir + "/01tree01.dat");
        File table31 = new File(omgaudioDir + "/03ginf01.dat");
        RandomAccessFile rafTable11;
        RandomAccessFile rafTable31;
        
        // Create a new file
        table11.createNewFile();
        table31.createNewFile();

        // Write data in the file :
        rafTable11 = new RandomAccessFile(table11, "rw"); //Open the file in RAF
        rafTable31 = new RandomAccessFile(table31, "rw"); //Open the file in RAF

        // Header 11
        WriteTableHeader(rafTable11, "TREE", 2); //Write table header
        WriteClassDescription(rafTable11, "GPLB", 0x30, 0x4010); //Write first class description
        int class111Length = titles.size()*0x2 + 0x10; // Calcul class's length
        class111Length += 0x10 - (class111Length % 0x10); // Get an "entire" number
        WriteClassDescription(rafTable11, "TPLB", 0x4040, class111Length); //Write second class description

        // Header 31
        WriteTableHeader(rafTable31, "GPIF", 1); //Write table header
        // ClassDescription can't be written now

        //Sort the titles in the right order
        List sortedTitles = sortByTitleId(titles);

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

//TODO exception are thrown for empty database            if(sortedTitles.size() < 1) { return;} //If there is no title, there is no use to create database
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
        WriteClassDescription(rafTable31, "GPFB", 0x20, albumsId.size()*0x310 + 0x10); //Write first class description

        //11-Class 1    
        WriteClassHeader(rafTable11, "GPLB", albumsId.size(), 0x8, albumsId.size(), 0); //Write first class header
        //31-Class 1    
        WriteClassHeader(rafTable31, "GPFB", albumsId.size(), 0x310); //Write first class header

        //Fill in elements in 11-Class 1 & 31-Class 1
        for( int i = 0; i <albumsId.size(); i++ ) {
            WriteGPLBelement(rafTable11, (Integer)albumsId.get(i), (Integer)titlesIdInTPLBlist.get(i));
            WriteGPFBelement(rafTable31, (Integer)titleKeysSorted.get(i), (String)albumsSorted.get(i), (String)artistsSorted.get(i), (String)genresSorted.get(i));
        }
        WriteZeros(rafTable11, 0x4010 - 0x10 - (0x8 * albumsId.size())); // Fill in the class with zeros

        //11-Class 2    
        WriteClassHeader(rafTable11, "TPLB", sortedTitles.size(), 0x2, sortedTitles.size(), 0); //Write first class header

        //Fill in elements in 11-Class 2
        for( int i = 0; i <sortedTitles.size(); i++ ) {
            tempTitle = (Title) sortedTitles.get(i);
            rafTable11.write(int2bytes((Integer)titles.get(tempTitle), 2));
        }
        WriteZeros(rafTable11, 0x10 - ((titles.size()*0x2) % 0x10)); // Fill in the class with zeros

        rafTable11.close();
        rafTable31.close();
    }
    
    
    
    /**
     * Write 01TREE02 and 03GINF02 files in OMGAUDIO folder. This method should be used while writing the database for generation using a OMGAUDIO folder.
     *
     *@param omgaudioDir Instance of the OMGAUDIO folder where to write the file.
     *@param titles List of the titles in the data base.
     *@param artists List of the artists in the data base.
     *@param titleKeys List of the titleKeys in the data base.
     *
     *@author nicolas_cardoso
     */
    public static void write01TREE02and03GINF02(File omgaudioDir, Map titles, JSymphonicMap artists, Map titleKeys) throws Exception{
        File table12 = new File(omgaudioDir + "/01tree02.dat");
        File table32 = new File(omgaudioDir + "/03ginf02.dat");
        RandomAccessFile rafTable12;
        RandomAccessFile rafTable32;
        
        // Create a new file
        table12.createNewFile();
        table32.createNewFile();
        
        // Write data in the file :
        rafTable12 = new RandomAccessFile(table12, "rw"); //Open the file in RAF
        rafTable32 = new RandomAccessFile(table32, "rw"); //Open the file in RAF

        // Header 12
        WriteTableHeader(rafTable12, "TREE", 2); //Write table header
        WriteClassDescription(rafTable12, "GPLB", 0x30, 0x4010); //Write first class description
        int class121Length = titles.size()*0x2 + 0x10; // Calcul class's length
        class121Length += 0x10 - (class121Length % 0x10); // Get an "entire" number
        WriteClassDescription(rafTable12, "TPLB", 0x4040, class121Length); //Write second class description

        // Header 32
        WriteTableHeader(rafTable32, "GPIF", 1); //Write table header
        WriteClassDescription(rafTable32, "GPFB", 0x20, artists.maxValue()*0x90 + 0x10); //Write first class description

        //Sort the titles in the right order
        List sortedTitles = sortByArtistTitle(titles);

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
        WriteClassHeader(rafTable12, "GPLB", artistsId.size(), 0x8, artistsId.size(), 0); //Write first class header
        //32-Class 1    
        WriteClassHeader(rafTable32, "GPFB", artists.maxValue(), 0x90); //Write first class header

        //Fill in elements in 12-Class 1
        for( int i = 0; i < artistsId.size(); i++ ) {
            WriteGPLBelement(rafTable12, (Integer)artistsId.get(i), (Integer)titlesIdInTPLBlist.get(i));
        }
        WriteZeros(rafTable12, 0x4010 - 0x10 - (0x8 * artistsId.size())); // Fill in the class with zeros

        //Fill in elements in 32-Class 1
        String artistNameTemp;
        for( int i = 1; i <= artists.maxValue(); i++) {
            artistNameTemp = (String)artists.getKey(i);

            if( artistNameTemp == null) {
                WriteGPFBelement(rafTable32, 0, "");
            }
            else{
                WriteGPFBelement(rafTable32, (Integer)titleKeysSorted.get(artistNameTemp), artistNameTemp);
            }
        }

        //12-Class 2    
        WriteClassHeader(rafTable12, "TPLB", sortedTitles.size(), 0x2, sortedTitles.size(), 0); //Write first class header

        //Fill in elements in 12-Class 2
        for( int i = 0; i < sortedTitles.size(); i++ ) {
            tempTitle = (Title) sortedTitles.get(i);
            rafTable12.write(int2bytes((Integer)titles.get(tempTitle), 2));
        }
        WriteZeros(rafTable12, 0x10 - ((sortedTitles.size()*0x2) % 0x10)); // Fill in the class with zeros

        rafTable12.close();
        rafTable32.close();
    }

    
    
    /**
     * Write 01TREE03 and 03GINF03 files in OMGAUDIO folder. This method should be used while writing the database for generation using a OMGAUDIO folder.
     *
     *@param omgaudioDir Instance of the OMGAUDIO folder where to write the file.
     *@param titles List of the titles in the data base.
     *@param albums List of the albums in the data base.
     *@param titleKeys List of the titleKeys in the data base.
     *
     *@author nicolas_cardoso
     */
    public static void write01TREE03and03GINF03(File omgaudioDir, Map titles, JSymphonicMap albums, Map titleKeys) throws Exception{
        File table13 = new File(omgaudioDir + "/01tree03.dat");
        File table33 = new File(omgaudioDir + "/03ginf03.dat");
        RandomAccessFile rafTable13;
        RandomAccessFile rafTable33;
        
        // Create a new file
        table13.createNewFile();
        table33.createNewFile();
        
        // Write data in the file :
        rafTable13 = new RandomAccessFile(table13, "rw"); //Open the file in RAF
        rafTable33 = new RandomAccessFile(table33, "rw"); //Open the file in RAF

        // Header 13
        WriteTableHeader(rafTable13, "TREE", 2); //Write table header
        WriteClassDescription(rafTable13, "GPLB", 0x30, 0x4010); //Write first class description
        int class131Length = titles.size()*0x2 + 0x10; // Calcul class's length
        class131Length += 0x10 - (class131Length % 0x10); // Get an "entire" number
        WriteClassDescription(rafTable13, "TPLB", 0x4040, class131Length); //Write second class description

        // Header 33
        WriteTableHeader(rafTable33, "GPIF", 1); //Write table header
        WriteClassDescription(rafTable33, "GPFB", 0x20, albums.maxValue()*0x90 + 0x10); //Write first class description

        //Sort the titles in the right order
        List sortedTitles = sortByAlbumTitleNumber(titles);

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
        WriteClassHeader(rafTable13, "GPLB", albumsId.size(), 0x8, albumsId.size(), 0); //Write first class header
        //33-Class 1    
        WriteClassHeader(rafTable33, "GPFB", albums.maxValue(), 0x90); //Write first class header

        //Fill in elements in 13-Class 1
        for( int i = 0; i < albumsId.size(); i++ ) {
            WriteGPLBelement(rafTable13, (Integer)albumsId.get(i), (Integer)titlesIdInTPLBlist.get(i));
        }
        WriteZeros(rafTable13, 0x4010 - 0x10 - (0x8 * albumsId.size())); // Fill in the class with zeros

        //Fill in elements in 33-Class 1
        String albumNameTemp;
        for( int i = 1; i <= albums.maxValue(); i++) {
            albumNameTemp = (String)albums.getKey(i);

            if( albumNameTemp == null) {
                WriteGPFBelement(rafTable33, 0, "");
            }
            else{
//                    WriteGPFBelement(rafTable33, (Integer)titleKeysSorted.get(albumNameTemp), albumNameTemp);
                WriteGPFBelement(rafTable33, 0, albumNameTemp);
            }
        }

        //13-Class 2    
        WriteClassHeader(rafTable13, "TPLB", sortedTitles.size(), 0x2, sortedTitles.size(), 0); //Write first class header

        //Fill in elements in 13-Class 2
        for( int i = 0; i < sortedTitles.size(); i++ ) {
            tempTitle = (Title) sortedTitles.get(i);
            rafTable13.write(int2bytes((Integer)titles.get(tempTitle), 2));
        }
        WriteZeros(rafTable13, 0x10 - ((sortedTitles.size()*0x2) % 0x10)); // Fill in the class with zeros

        rafTable13.close();
        rafTable33.close();
    }
        
        
    /**
     * Write 01TREE04 and 03GINF04 files in OMGAUDIO folder. This method should be used while writing the database for generation using a OMGAUDIO folder.
     *
     *@param omgaudioDir Instance of the OMGAUDIO folder where to write the file.
     *@param titles List of the titles in the data base.
     *@param genres List of the genres in the data base.
     *@param titleKeys List of the titleKeys in the data base.
     *
     *@author nicolas_cardoso
     */
    public static void write01TREE04and03GINF04(File omgaudioDir, Map titles, JSymphonicMap genres, Map titleKeys) throws Exception{
        File table14 = new File(omgaudioDir + "/01tree04.dat");
        File table34 = new File(omgaudioDir + "/03ginf04.dat");
        RandomAccessFile rafTable14;
        RandomAccessFile rafTable34;
        
        // Create a new file
        table14.createNewFile();
        table34.createNewFile();
        
        // Write data in the file :
        rafTable14 = new RandomAccessFile(table14, "rw"); //Open the file in RAF
        rafTable34 = new RandomAccessFile(table34, "rw"); //Open the file in RAF

        // Header 14
        WriteTableHeader(rafTable14, "TREE", 2); //Write table header
        WriteClassDescription(rafTable14, "GPLB", 0x30, 0x4010); //Write first class description
        int class141Length = titles.size()*0x2 + 0x10; // Calcul class's length
        class141Length += 0x10 - (class141Length % 0x10); // Get an "entire" number
        WriteClassDescription(rafTable14, "TPLB", 0x4040, class141Length); //Write second class description

        // Header 34
        WriteTableHeader(rafTable34, "GPIF", 1); //Write table header
        WriteClassDescription(rafTable34, "GPFB", 0x20, genres.maxValue()*0x90 + 0x10); //Write first class description

        //Sort the titles in the right order
        List sortedTitles = sortByGenreTitle(titles);

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
        WriteClassHeader(rafTable14, "GPLB", genresId.size(), 0x8, genresId.size(), 0); //Write first class header
        //34-Class 1    
        WriteClassHeader(rafTable34, "GPFB", genres.maxValue(), 0x90); //Write first class header

        //Fill in elements in 14-Class 1
        for( int i = 0; i < genresId.size(); i++ ) {
            WriteGPLBelement(rafTable14, (Integer)genresId.get(i), (Integer)titlesIdInTPLBlist.get(i));
        }
        WriteZeros(rafTable14, 0x4010 - 0x10 - (0x8 * genresId.size())); // Fill in the class with zeros

        //Fill in elements in 34-Class 1
        String genreTemp;
        for( int i = 1; i <= genres.maxValue(); i++) {
            genreTemp = (String)genres.getKey(i);

            if( genreTemp == null) {
                WriteGPFBelement(rafTable34, 0, "");
            }
            else{
                WriteGPFBelement(rafTable34, (Integer)titleKeysSorted.get(genreTemp), genreTemp);
            }
        }

        //14-Class 2    
        WriteClassHeader(rafTable14, "TPLB", sortedTitles.size(), 0x2, sortedTitles.size(), 0); //Write first class header

        //Fill in elements in 14-Class 2
        for( int i = 0; i < sortedTitles.size(); i++ ) {
            tempTitle = (Title) sortedTitles.get(i);
            rafTable14.write(int2bytes((Integer)titles.get(tempTitle), 2));
        }
        WriteZeros(rafTable14, 0x10 - ((sortedTitles.size()*0x2) % 0x10)); // Fill in the class with zeros

        rafTable14.close();
        rafTable34.close();
    }

        
    /**
     * Write 01TREE22 and 03GINF22 files in OMGAUDIO folder. This method should be used while writing the database for generation using a OMGAUDIO folder.
     *
     *@param omgaudioDir Instance of the OMGAUDIO folder where to write the file.
     *
     *@author nicolas_cardoso
     */
    public static void write01TREE22and03GINF22(File omgaudioDir) throws Exception{
        File table122 = new File(omgaudioDir + "/01tree22.dat");
        File table322 = new File(omgaudioDir + "/03ginf22.dat");
        RandomAccessFile rafTable122;
        RandomAccessFile rafTable322;
        
        //Create a new file
        table122.createNewFile();
        table322.createNewFile();

        // Write data in the file :
        rafTable122 = new RandomAccessFile(table122, "rw"); //Open the file in RAF
        rafTable322 = new RandomAccessFile(table322, "rw"); //Open the file in RAF

        // Header 122
        WriteTableHeader(rafTable122, "TREE", 2); //Write table header
        WriteClassDescription(rafTable122, "GPLB", 0x30, 0x10); //Write first class description
        WriteClassDescription(rafTable122, "TPLB", 0x40, 0x10); //Write second class description

        // Header 322
        WriteTableHeader(rafTable322, "GPIF", 1); //Write table header
        WriteClassDescription(rafTable322, "GPFB", 0x20, 0x10); //Write first class description


        //122-Class 1    
        WriteClassHeader(rafTable122, "GPLB", 0, 0x8, 0, 0); //Write first class header
        //322-Class 1    
        WriteClassHeader(rafTable322, "GPFB", 0, 0x310); //Write first class header

        //122-Class 2    
        WriteClassHeader(rafTable122, "TPLB", 0, 0x2, 0, 0); //Write first class header

        rafTable122.close();
        rafTable322.close();
    }

    
    
    /**
     * Write 01TREE2D and 03GINF2D files in OMGAUDIO folder. This method should be used while writing the database for generation using a OMGAUDIO folder.
     *
     *@param omgaudioDir Instance of the OMGAUDIO folder where to write the file.
     *@param titles List of the titles in the data base.
     *@param artists List of the artists in the data base.
     *@param albums List of the albums in the data base.
     *@param titleKeys List of the titleKeys in the data base.
     *
     *@author nicolas_cardoso
     */
    public static void write01TREE2Dand03GINF2D(File omgaudioDir, Map titles, JSymphonicMap artists, JSymphonicMap albums, Map titleKeys) throws Exception{
        File table12D = new File(omgaudioDir + "/01tree2D.dat");
        File table32D = new File(omgaudioDir + "/03ginf2D.dat");
        RandomAccessFile rafTable12D;
        RandomAccessFile rafTable32D;
        
        //Create new file
        table12D.createNewFile();
        table32D.createNewFile();

        // Write data in the file :
        rafTable12D = new RandomAccessFile(table12D, "rw"); //Open the file in RAF
        rafTable32D = new RandomAccessFile(table32D, "rw"); //Open the file in RAF

        // Header 12D
        WriteTableHeader(rafTable12D, "TREE", 2); //Write table header
        WriteClassDescription(rafTable12D, "GPLB", 0x30, 0x4010); //Write first class description
        int class12D1Length = titles.size()*0x2 + 0x10; // Calcul class's length
        class12D1Length += 0x10 - (class12D1Length % 0x10); // Get an "entire" number
        WriteClassDescription(rafTable12D, "TPLB", 0x4040, class12D1Length); //Write second class description

        // Header 32D
        WriteTableHeader(rafTable32D, "GPIF", 1); //Write table header
        WriteClassDescription(rafTable32D, "GPFB", 0x20, (artists.size() + albums.size() + 1)*0x110 + 0x10); //Write first class description

        //12D-Class 1    
        WriteClassHeader(rafTable12D, "GPLB", artists.size() + albums.size() + 1, 0x8, artists.size() + albums.size() + 1, 0); //Write first class header

        //32D-Class 1    
        WriteClassHeader(rafTable32D, "GPFB", artists.size() + albums.size() + 1, 0x110); //Write first class header

        //Sort the titles in the right order
        List sortedTitles = sortByArtistAlbumTitleNumber(titles);

        //Fill in elements in 12D-Class 1 & 32D-Class 1
        //First element is empty
        WriteGPLBelement(rafTable12D, 1, 0);
        WriteGPFBelement(rafTable32D, 0, "", "");

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
        WriteGPLBelement(rafTable12D, 2, 0);
        WriteGPFBelement(rafTable32D, 0, artistName, artistName);

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
                        WriteGPLBelement2(rafTable12D, j, titlesIdInTPLBlist);
                        WriteGPFBelement(rafTable32D, elementKey, lastAlbumName, lastAlbumName);
                        j++;
                        elementKey = (Integer)titleKeys.get(tempTitle);
                        lastAlbumName = albumName;
                        titlesIdInTPLBlist = i + 1;
                }
            }
            else{
                // Write the last album
                WriteGPLBelement2(rafTable12D, j, titlesIdInTPLBlist);
                WriteGPFBelement(rafTable32D, elementKey, lastAlbumName, lastAlbumName);
                j++;
                elementKey = (Integer)titleKeys.get(tempTitle);
                lastAlbumName = albumName;
                titlesIdInTPLBlist = i + 1;

                // Write the new artist
                WriteGPLBelement(rafTable12D, j, 0);
                WriteGPFBelement(rafTable32D, 0, artistName, artistName);
                j++;
                lastArtistName = artistName;
            }
        }

        // Last element
        WriteGPLBelement2(rafTable12D, j, titlesIdInTPLBlist);
        WriteGPFBelement(rafTable32D, elementKey, lastAlbumName, lastAlbumName);

        // Fill in the class with zeros
        WriteZeros(rafTable12D, 0x4010 - 0x10 - (0x8 * (artists.size() + albums.size() + 1))); 

        //12D-Class 2    
        WriteClassHeader(rafTable12D, "TPLB", sortedTitles.size(), 0x2, sortedTitles.size(), 0); //Write first class header

        //Fill in elements in 12D-Class 2
        for( int i = 0; i <sortedTitles.size(); i++ ) {
            tempTitle = (Title) sortedTitles.get(i);
            rafTable12D.write(int2bytes((Integer)titles.get(tempTitle), 2));
        }
        WriteZeros(rafTable12D, 0x10 - ((titles.size()*0x2) % 0x10)); // Fill in the class with zeros

        rafTable12D.close();
        rafTable32D.close();
    }
        
        
    /**
     * Write 02TREINF file in OMGAUDIO folder. This method should be used while writing the database for generation using a OMGAUDIO folder.
     *
     *@param omgaudioDir Instance of the OMGAUDIO folder where to write the file.
     *@param titleKeys List of the titleKeys in the data base.
     *
     *@author nicolas_cardoso
     */
    public static void write02TREINF(File omgaudioDir, Map titleKeys) throws Exception{
        File table2 = new File(omgaudioDir + "/02treinf.dat");
        RandomAccessFile rafTable2;
        
        //Create new file
        table2.createNewFile();
        
        // Write data in the file :
        rafTable2 = new RandomAccessFile(table2, "rw"); //Open the file in RAF

        // Header
        WriteTableHeader(rafTable2, "GTIF", 1); //Write table header
        WriteClassDescription(rafTable2, "GTFB", 0x20, 0x1f00); //Write first class description

        //Class 1
        WriteClassHeader(rafTable2, "GTFB", 0x2d, 0x90); //Write first class header

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
            WriteGTFBelement(rafTable2, globalKey,"");
        }

        // Elements 5 to 0x21
        WriteZeros(rafTable2, (0x21 - 0x5 + 1)*0x90);

        // Element 0x22
        WriteGTFBelement(rafTable2, 0,"");

        // Element 0x23 to 0x2c
        WriteZeros(rafTable2, (0x2c - 0x23 + 1)*0x90);

        // Element 0x2D
        WriteGTFBelement(rafTable2, globalKey,"STD_TPE1");

        // Fill in the class with zeros
        WriteZeros(rafTable2, 0x1f00 - (0x2d*0x90) - 0x10);

        rafTable2.close();
    }
        
    
    
    /**
     * Write 04CNTINF file in OMGAUDIO folder. This method should be used while writing the database for generation using a OMGAUDIO folder.
     *
     *@param omgaudioDir Instance of the OMGAUDIO folder where to write the file.
     *@param magicKey The unique key of the walkman
     *@param titleKeys List of the titleKeys in the data base.
     *
     *@author nicolas_cardoso
     */
    public static void write04CNTINF(File omgaudioDir, JSymphonicMap titles, int magicKey, Map titleKeys) throws Exception{
        File table4 = new File(omgaudioDir + "/04cntinf.dat");
        RandomAccessFile rafTable4;
        
        //Create new file
        table4.createNewFile();

        // Write data in the file :
        rafTable4 = new RandomAccessFile(table4, "rw"); //Open the file in RAF

        // Header
        WriteTableHeader(rafTable4, "CNIF", 1); //Write table header
        WriteClassDescription(rafTable4, "CNFB", 0x20, titles.maxValue()*0x290 + 0x10); //Write first class description
        WriteClassHeader(rafTable4, "CNFB", titles.maxValue(), 0x290); //Write first class header

        //Sort the titles in the right order
        List sortedTitles = sortByTitleId(titles);

        // Write elements
        Title titleTemp;

        for( int i = 1; i <= titles.maxValue(); i++ ) {
            titleTemp = (Title)titles.getKey(i);

            if( titleTemp == null ) {
                WriteCNFBelement(rafTable4, 0, 0, "", "", "", "");
                logger.warning("A title with no information has been found, names in the database may be missing.");
            }
            else{
                WriteCNFBelement(rafTable4, magicKey, (Integer)titleKeys.get(titleTemp), titleTemp.getTitle(), titleTemp.getArtist(), titleTemp.getAlbum(), titleTemp.getGenre());
            }
        }

        rafTable4.close();
    }
        
        
        
    /**
     * Write 05CIDLST file in OMGAUDIO folder. This method should be used while writing the database for generation using a OMGAUDIO folder.
     *
     *@param omgaudioDir Instance of the OMGAUDIO folder where to write the file.
     *@param titleRefs List of the title references in the data base.
     *
     *@author nicolas_cardoso
     */
    public static void write05CIDLST(File omgaudioDir, JSymphonicMap titles, Map titleRefs) throws Exception{
        File table5 = new File(omgaudioDir + "/05cidlst.dat");
        RandomAccessFile rafTable5;
        
        //Create new file
        table5.createNewFile(); 
        
        // Write data in the file :
        rafTable5 = new RandomAccessFile(table5, "rw"); //Open the file in RAF

        // Header
        WriteTableHeader(rafTable5, "CIDL", 1); //Write table header
        WriteClassDescription(rafTable5, "CILB", 0x20, titles.maxValue()*0x30 + 0x10); //Write first class description
        WriteClassHeader(rafTable5, "CILB", titles.maxValue(), 0x30); //Write first class header

        //Sort the titles in the right order
        List sortedTitles = sortByTitleId(titles);

        // Write elements
        Title titleTemp;
        TitleRef titleRefTemp;

        for( int i = 1; i <= titles.maxValue(); i++ ) {
            titleTemp = (Title)titles.getKey(i);

            if( titleTemp == null ) {
                WriteCILBelement(rafTable5, 0, 0, 0, 0, 0, 0);
            }
            else{
                titleRefTemp = (TitleRef)titleRefs.get(titleTemp);
                WriteCILBelement(rafTable5, titleRefTemp.getPart1(), titleRefTemp.getPart2(), titleRefTemp.getPart3(), titleRefTemp.getPart4(), titleRefTemp.getPart5(), titleRefTemp.getPart6());
            }
        }

        rafTable5.close();
    }
        

    /**
     * Write the so called "table header" in the files in the OMGAUDIO folder. This method is used by "write00GRTLST" method for instance.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the header to.
     *@param tableName the name of the table (for instance "TREE" in the "write00GRTLST" method).
     *@param numberOfClasses number of class in this file  (for instance 2 in the "write00GRTLST" method).
     *
     *@author nicolas_cardoso
     */
    public static void WriteTableHeader(RandomAccessFile raf, String tableName, int numberOfClasses) throws java.io.IOException {
        byte[] bytesTableName;
        byte[] constant = {1,1,0,0};
        
        if( tableName.length() != 4 ) { //Control the name of the table (must be 4 characters long)
            logger.severe("Invalid table name while writing config files. Exiting the program.");
            System.exit(-1);
        }

        if( numberOfClasses != 1 && numberOfClasses != 2 ) { //Control the number of classes (must be 1 or 2)
            logger.severe("Invalid number of classes while writing config files. Exiting the program.");
            System.exit(-1);
        }
        
        bytesTableName = tableName.getBytes();
        raf.write(bytesTableName);

        raf.write(constant);
        
        raf.write(numberOfClasses);
        
        WriteZeros(raf, 7);
    }
    
    
    /**
     * Write the so called "table description" in the files in the OMGAUDIO folder. This method is used by "write00GRTLST" method for instance.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the description to.
     *@param className name of the table (for instance "SYSB" in the "write00GRTLST" method).
     *@param startAdress adress where the class starts  (for instance 0x30 in the "write00GRTLST" method).
     *@param length length of the class  (for instance 0x70 in the "write00GRTLST" method).
     *
     *@author nicolas_cardoso
     */
    public static void WriteClassDescription(RandomAccessFile raf, String className, int startAdress, int length) throws java.io.IOException {
        byte[] bytesClassName;
        byte[] bytesLength;
        byte[] bytesStartAdress;
        
        if( className.length() != 4 ) { //Control the name of the class (must be 4 characters long)
            logger.severe("Invalid table name while writing config files. Exiting the program.");
            System.exit(-1);
        }
        
        bytesClassName = className.getBytes();
        raf.write(bytesClassName);
        
        bytesStartAdress = int2bytes(startAdress, 4);
        raf.write(bytesStartAdress);
        
        bytesLength = int2bytes(length, 4);
        raf.write(bytesLength);
        
        WriteZeros(raf, 4);
    }
    
    
    /**
     * Write the so called "class header" in the files in the OMGAUDIO folder. This method is used by "write01TREE01and03GINF01" method for instance.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the header to.
     *@param className name of the table (for instance "GPFB" in the "write01TREE01and03GINF01" method).
     *@param numberOfElement number of element in the class  (for instance the number of album ID in the "write01TREE01and03GINF01" method).
     *@param lengthOfOneElement length of one element of the class  (for instance 0x310 in the "write01TREE01and03GINF01" method).
     *
     *@author nicolas_cardoso*/
    public static void WriteClassHeader(RandomAccessFile raf, String className, int numberOfElement, int lengthOfOneElement) throws java.io.IOException {
        byte[] bytesClassName;
        byte[] bytesNumberOfElement;
        byte[] bytesLengthOfOneElement;
        
        if( className.length() != 4 ) { //Control the name of the class (must be 4 characters long)
            logger.severe("Invalid table name while writing config files. Exiting the program.");
            System.exit(-1);
        }
        
        bytesClassName = className.getBytes();
        raf.write(bytesClassName);

        bytesNumberOfElement = int2bytes(numberOfElement, 2);
        raf.write(bytesNumberOfElement);

        bytesLengthOfOneElement = int2bytes(lengthOfOneElement, 2);
        raf.write(bytesLengthOfOneElement);
        
        WriteZeros(raf, 8);
    }
    
    
    /**
     * Write the so called "class header" in the files in the OMGAUDIO folder. This method is used by "write00GRTLST" method for instance.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the header to.
     *@param className name of the table (for instance "SYSB" in the "write00GRTLST" method).
     *@param numberOfElement number of element in the class  (for instance 0x30 in the "write00GRTLST" method).
     *@param lengthOfOneElement length of one element of the class  (for instance 0x70 in the "write00GRTLST" method).
     *@param classHeaderComplement1 a 4 bytes word to be written in the class header (for instance 0x00000006 in the "write00GRTLST" method).
     *@param classHeaderComplement2 a 4 bytes word to be written in the class header after classHeaderComplement1 (for instance 0x04000000 in the "write00GRTLST" method).
     *
     *@author nicolas_cardoso
     */
    public static void WriteClassHeader(RandomAccessFile raf, String className, int numberOfElement, int lengthOfOneElement, int classHeaderComplement1, int classHeaderComplement2) throws java.io.IOException {
        if( className.length() != 4 ) { //Control the name of the class (must be 4 characters long)
            logger.severe("Invalid table name while writing config files. Exiting the program.");
            System.exit(-1);
        }
        
        raf.write(className.getBytes());
        raf.write(int2bytes(numberOfElement, 2));
        raf.write(int2bytes(lengthOfOneElement, 2));
        raf.write(int2bytes(classHeaderComplement1, 4));
        raf.write(int2bytes(classHeaderComplement2, 4));
    }
    
    
    /**
     * Write a string in a random access file using "UTF-16" encoding.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the string to.
     *@param string the string to be written.
     *@author nicolas_cardoso
     */
    public static void WriteString16(RandomAccessFile raf, String string) throws java.io.IOException {
        if( string.length() == 0 ){
            return;
        }
        
        raf.write(string.getBytes("UTF-16"), 2, string.getBytes("UTF-16").length - 2);
    }

    /**
     * Write a string in a FileOutputStream using "UTF-16" encoding.
     * This class throws exceptions.
     *
     *@param out FileOutputStream instance of the file to write the string to.
     *@param string the string to be written.
     *@author nicolas_cardoso
     */
    public static void WriteString16(FileOutputStream out, String string) throws java.io.IOException {
        if( string.length() == 0 ){
            return;
        }

        out.write(string.getBytes("UTF-16"), 2, string.getBytes("UTF-16").length - 2);
    }
    
    /**
     * Write a given number of zeros in a FileOutputStream.
     * This class throws exceptions.
     *
     *@param out FileOutputStream instance of the file to write the zeros to.
     *@param numberOfZeros number of zeros to be written.
     *
     *@author nicolas_cardoso
     */
    public static void WriteZeros(FileOutputStream out, int numberOfZeros) throws java.io.IOException {
        for( int i = 0; i < numberOfZeros; i++) {
            out.write(0);
        }
    }
    /**
     * Write a given number of zeros in a random access file.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the zeros to.
     *@param numberOfZeros number of zeros to be written.
     *
     *@author nicolas_cardoso
     */
    public static void WriteZeros(RandomAccessFile raf, int numberOfZeros) throws java.io.IOException {
        for( int i = 0; i < numberOfZeros; i++) {
            raf.write(0);
        }
    }
    
    
    /**
     * Write an element of the GTLB class in a random access file. This class is used to describ the database strucure in 00GRTLST file.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the element to.
     *@param fileRef reference of the file represented by the current element.
     *@param unknown1 unknow paramater.
     *@param numberOfTag number of EA3 tag in the file represented by the current element (for instance "TPE1").
     *@param tag1 first tag value.
     *@param tag2 second tag value.
     *@param unknown2 unknow paramater.
     *
     *@author nicolas_cardoso
     */
    public static void WriteGTLBelement(RandomAccessFile raf, int fileRef, int unknown1, int numberOfTag, String tag1, String tag2, byte[] unknown2) throws java.io.IOException {
        byte[] bytesFileRef;
        byte[] bytesUnknow1;
        byte[] bytesNumberOfTag;
        byte[] bytesTag1;
        byte[] bytesTag2;

        bytesFileRef = int2bytes(fileRef, 2);
        raf.write(bytesFileRef);

        bytesUnknow1 = int2bytes(unknown1, 2);
        raf.write(bytesUnknow1);
       
        WriteZeros(raf, 12);
        
        bytesNumberOfTag = int2bytes(numberOfTag, 2);
        raf.write(bytesNumberOfTag);

        WriteZeros(raf, 2);

        if( numberOfTag > 0) {
            bytesTag1 = tag1.getBytes();
            raf.write(bytesTag1);
            
            // Control that the right number of bytes have been written
            if( tag1.length() < 4 ){
                WriteZeros(raf, 4 - tag1.length());
            }
        }
        else {
            WriteZeros(raf, 4);
        }

        if( numberOfTag > 1 ) {
            bytesTag2 = tag2.getBytes();
            raf.write(bytesTag2);
            
            // Control that the right number of bytes have been written
            if( tag2.length() < 4 ){
                WriteZeros(raf, 4 - tag2.length());
            }
        }
        else {
            WriteZeros(raf, 4);
        }
        
        WriteZeros(raf, 20);

        raf.write(unknown2);
        
        //Complete the end of the element with zeros
        WriteZeros(raf, 0x50 - 2*2 - 12 - 2*2 - 4*2 - 20 - unknown2.length); //Complete element with zeros
    }
    
    
    /**
     * Write an element of the GPLB class in a random access file. This method is used to order the title (referenced by their ID) and uses the "1"constant.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the element to.
     *@param itemIdIn03GINFXX ID of the title in the 03GINFXX files.
     *@param titleIdInTPLBlist ID of the title in the TPLB list.
     *
     *@author nicolas_cardoso
     */
    public static void WriteGPLBelement(RandomAccessFile raf, int itemIdIn03GINFXX, int titleIdInTPLBlist) throws java.io.IOException {
        byte[] constant = {1,0};

        raf.write(int2bytes(itemIdIn03GINFXX, 2));
        raf.write(constant);
        raf.write(int2bytes(titleIdInTPLBlist, 2));
        WriteZeros(raf, 2);
    }
    
    
    /**
     * Write an element of the GPLB class in a random access file. This method is used to order the title (referenced by their ID) and uses the "2"constant.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the element to.
     *@param itemIdIn03GINFXX ID of the title in the 03GINFXX files.
     *@param titleIdInTPLBlist ID of the title in the TPLB list.
     *
     *@author nicolas_cardoso
     */
    public static void WriteGPLBelement2(RandomAccessFile raf, int itemIdIn03GINFXX, int titleIdInTPLBlist) throws java.io.IOException {
        byte[] constant = {2,0};

        raf.write(int2bytes(itemIdIn03GINFXX, 2));
        raf.write(constant);
        raf.write(int2bytes(titleIdInTPLBlist, 2));
        WriteZeros(raf, 2);
    }
    
    
    /**
     * Write an element of the GPFB class in a random access file.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the element to.
     *@param albumKey album key of the title represented by the current element.
     *@param albumName album name of the title represented by the current element.
     *@param artistName artist name of the title represented by the current element.
     *@param genre genre of the title represented by the current element.
     *
     *@author nicolas_cardoso
     */
    public static void WriteGPFBelement(RandomAccessFile raf, int albumKey, String albumName, String artistName, String genre) throws java.io.IOException {
        byte[] constant1 = {0,6,0,-128}; //-128 as a signed byte equals 128 as an unsigned byte equal 0x80 in hex
        byte[] constant2 = {0,2};
        String tag1 = "TIT2";
        String tag2 = "TPE1";
        String tag3 = "TCON";
        String tag4 = "TSOP";
        String tag5 = "PICP";
        String tag6 = "PIC0";
        
        //Element header
        WriteZeros(raf, 8);
        raf.write(int2bytes(albumKey, 4));
        raf.write(constant1);
        
        //Fist sub-element
        raf.write(tag1.getBytes());
        raf.write(constant2);
        WriteString16(raf, albumName);
        WriteZeros(raf, 0x80 - 4 - 2 - (albumName.length()*2) );

        //Second sub-element
        raf.write(tag2.getBytes());
        raf.write(constant2);
        WriteString16(raf, artistName);
        WriteZeros(raf, 0x80 - 4 - 2 - (artistName.length()*2) );
        
        //Third sub-element
        raf.write(tag3.getBytes());
        raf.write(constant2);
        WriteString16(raf, genre);
        WriteZeros(raf, 0x80 - 4 - 2 - (genre.length()*2) );
        
        //Fourth sub-element
        raf.write(tag4.getBytes());
        raf.write(constant2);
        WriteZeros(raf, 0x80 - 4 - 2 );

        //Fifth sub-element
        raf.write(tag5.getBytes());
        raf.write(constant2);
        WriteZeros(raf, 0x80 - 4 - 2 );

        //Sixth sub-element
        raf.write(tag6.getBytes());
        raf.write(constant2);
        WriteZeros(raf, 0x80 - 4 - 2 );
    }
    
    
    /**
     * Write an element of the GPFB class in a random access file.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the element to.
     *@param albumKey album key of the title represented by the current element.
     *@param artistName artist name of the title represented by the current element.
     *
     *@author nicolas_cardoso
     */
    public static void WriteGPFBelement(RandomAccessFile raf, int albumKey, String artistName) throws java.io.IOException {
        byte[] constant1 = {0,1,0,-128}; //-128 as a signed byte equals 128 as an unsigned byte equal 0x80 in hex
        byte[] constant2 = {0,2};
        String tag1 = "TIT2";
        
        //Element header
        WriteZeros(raf, 8);
        raf.write(int2bytes(albumKey, 4));
        raf.write(constant1);
        
        //Fist sub-element
        raf.write(tag1.getBytes());
        raf.write(constant2);
        WriteString16(raf, artistName);
        WriteZeros(raf, 0x80 - 4 - 2 - (artistName.length()*2) );
    }
    
    
    /**
     * Write an element of the GPFB class in a random access file.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the element to.
     *@param elementName1 first element name of the title represented by the current element.
     *@param elementName2 second element name of the title represented by the current element.
     *
     *@author nicolas_cardoso
     */
    public static void WriteGPFBelement(RandomAccessFile raf, int albumKey, String elementName1, String elementName2) throws java.io.IOException {
        byte[] constant1 = {0,2,0,-128}; //-128 as a signed byte equals 128 as an unsigned byte equal 0x80 in hex
        byte[] constant2 = {0,2};
        String tag1 = "TIT2";
        String tag2 = "XSOT";
        
        //Element header
        WriteZeros(raf, 8);
        raf.write(int2bytes(albumKey, 4));
        raf.write(constant1);
        
        //Fist sub-element
        raf.write(tag1.getBytes());
        raf.write(constant2);
        WriteString16(raf, elementName1);
        WriteZeros(raf, 0x80 - 4 - 2 - (elementName1.length()*2) );
        
        //Second sub-element
        raf.write(tag2.getBytes());
        raf.write(constant2);
        WriteString16(raf, elementName2);
        WriteZeros(raf, 0x80 - 4 - 2 - (elementName2.length()*2) );
    }
    
    
    /**
     * Write an element of the GPFB class in a random access file.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the element to.
     *@param key first a key of the title represented by the current element.
     *@param unknown a name of the title represented by the current element.
     *
     *@author nicolas_cardoso
     */
    public static void WriteGTFBelement(RandomAccessFile raf, int key, String unknown) throws java.io.IOException {
        byte[] constant1 = {0,1,0,-128}; //-128 as a signed byte equals 128 as an unsigned byte equal 0x80 in hex
        byte[] constant2 = {0,2};
        String tag = "TIT2";
        
        WriteZeros(raf, 8);
        raf.write(int2bytes(key, 4));
        raf.write(constant1);
        raf.write(tag.getBytes());
        raf.write(constant2);
        WriteString16(raf, unknown);
        
        WriteZeros(raf, 0x80 - 4 - 2 - (unknown.length()*2) );
    }
    
    
    /**
     * Write an element of the CNFB class in a random access file.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the element to.
     *@param magicKey the so called magic key number corresponding to the current walkman.
     *@param titleKey title key of the title represented by the current element.
     *@param titleName title name of the title represented by the current element.
     *@param artistName artist name of the title represented by the current element.
     *@param albumName album name of the title represented by the current element.
     *@param genreName genre of the title represented by the current element.
     *
     *@author nicolas_cardoso
     */
    public static void WriteCNFBelement(RandomAccessFile raf, int magicKey, int titleKey, String titleName, String artistName, String albumName, String genreName) throws java.io.IOException {
        byte[] constant1 = {0,5,0,-128}; //-128 as a signed byte equals 128 as an unsigned byte equal 0x80 in hex
        byte[] constant2 = {0,2};
        byte[] constant3 = {0,0,-1,-1}; //-1 as a signed byte equals 256 as an unsigned byte equal 0xFF in hex
        String tag1 = "TIT2";
        String tag2 = "TPE1";
        String tag3 = "TALB";
        String tag4 = "TCON";
        String tag5 = "TSOP";
        
        //Element header
        raf.write(constant3);
        raf.write(int2bytes(magicKey, 4));
        raf.write(int2bytes(titleKey, 4));
        raf.write(constant1);
        
        //Fist sub-element
        raf.write(tag1.getBytes());
        raf.write(constant2);
        WriteString16(raf, titleName);
        WriteZeros(raf, 0x80 - 4 - 2 - (titleName.length()*2) );
        
        //Second sub-element
        raf.write(tag2.getBytes());
        raf.write(constant2);
        WriteString16(raf, artistName);
        WriteZeros(raf, 0x80 - 4 - 2 - (artistName.length()*2) );

        //Third sub-element
        raf.write(tag3.getBytes());
        raf.write(constant2);
        WriteString16(raf, albumName);
        WriteZeros(raf, 0x80 - 4 - 2 - (albumName.length()*2) );
        
        //Fourth sub-element
        raf.write(tag4.getBytes());
        raf.write(constant2);
        WriteString16(raf, genreName);
        WriteZeros(raf, 0x80 - 4 - 2 - (genreName.length()*2) );
        
        //Fifth sub-element
        raf.write(tag5.getBytes());
        raf.write(constant2);
        WriteZeros(raf, 0x80 - 4 - 2);
    }
    
    
    /**
     * Write an element of the CILB class in a random access file.
     * This class throws exceptions.
     *
     *@param raf Random access file instance of the file to write the element to.
     *@param part1 first part of the element.
     *@param part2 second part of the element.
     *@param part3 third part of the element.
     *@param part4 fourth part of the element.
     *@param part5 fifth part of the element.
     *@param part6 sixth part of the element.
     *
     *@author nicolas_cardoso
     */
    public static void WriteCILBelement(RandomAccessFile raf, int part1, int part2, int part3, int part4, int part5, int part6) throws java.io.IOException {
        raf.write(int2bytes(part1, 4));
        raf.write(int2bytes(part2, 4));
        raf.write(int2bytes(part3, 4));
        raf.write(int2bytes(part4, 4));
        raf.write(int2bytes(part5, 4));
        raf.write(int2bytes(part6, 4));
        WriteZeros(raf, 0x30 - 4*6);
    }
    
    /**
     * Tells if an array of bytes is full with zeros.
     *
     *@param bytes the array of bytes to test
     *@return true for "yes" and false for "no"
     *
     *@author nicolas_cardoso
     */
    public static boolean isZero(byte[] bytes){
        int i = 0;
        boolean ret = true;
        
        while(i < bytes.length){
            if(bytes[i] == 0){
                ret = ret && true;
            }
            else{
                ret = false;
            }
            i++;
        }
        return ret;
    }
    
    
    /**
     * Converts an array of bytes to an int. For example, if the array is : [1;2;3;4], the corresponding int will be "1234".
     *
     *@param bytes The array of bytes to convert
     *
     *@return The converted number.
     *
     *@author nicolas_cardoso
     */
    public static int bytes2int(byte[] bytes){
        int i = bytes.length - 1;
        int ret = 0;
        
        while(i >= 0){
            ret += bytes[i]*Math.pow(10, bytes.length - (i+1));
            i--;
        }
        return ret;
    }
    
    /**
     * Converts an integer into an array of bytes.
     *
     *@param integer The integer to convert.
     *
     *@return The array of bytes representating the integer.
     *
     *@author nicolas_cardoso
     */
    public static byte[] int2bytes(Integer integer){
        int numberOfDigit;
        Integer digit;
        byte[] bytesToReturn;
        
        // Search the number of digit in the integer
        numberOfDigit = (int)((Integer.toBinaryString(integer).length() / 8.0 ) + 0.99); // 0.99 is added to round up the number (can't add 1 beacause in some case, the right number is found, so added 1 make it false althought it's not)
        
        // Create the array of bytes
        bytesToReturn = new byte[numberOfDigit];
        
        // Fill in the array
        for( int i = numberOfDigit - 1; i >= 0; i-- ) {
            digit = integer % 256;
            integer = integer / 256;
            bytesToReturn[i] = (byte) digit.byteValue();
        }
        
        return bytesToReturn;
    }    
    
    
    /**
     * Converts an integer into an array of bytes of a given length.
     *
     *@param integer The integer to convert.
     *@param bytesLength The desired length of the array.
     *
     *@return The array of bytes representating the integer.
     *
     *@author nicolas_cardoso
     */    
    public static byte[] int2bytes(int integer, int bytesLength){
        int numberOfDigit;
        Integer digit;
        byte[] bytesToReturn;
        
        // Search the number of digit in the integer
        numberOfDigit = (int)((Integer.toBinaryString(integer).length() / 8.0 ) + 0.99); // 0.99 is added to round up the number (can't add 1 beacause in some case, the right number is found, so added 1 make it false althought it's not)

        // Create the array of bytes
        bytesToReturn = new byte[bytesLength];
        
        if( numberOfDigit > bytesLength ) {
            logger.severe("Impossible to fit the integer in that bytesLength in method 'int2bytes'. Existing program.");
            System.exit(-1);
        }
        
        // Fill in the integer in the array
        for( int i = bytesLength - 1; i >= bytesLength - numberOfDigit; i-- ) {
            digit = integer % 256;
            integer = integer / 256;
            bytesToReturn[i] = digit.byteValue();
        }

        // Fill in the rest of the array with zeros
        for( int i = bytesLength - numberOfDigit - 1; i >= 0; i-- ) {
            bytesToReturn[i] = 0;
        }
        
        return bytesToReturn;
    }   
    
    
    /**
     * Converts an long into an array of bytes of a given length.
     *
     *@param longnb The long number to convert.
     *@param bytesLength The desired length of the array.
     *
     *@return The array of bytes representating the long number.
     *
     *@author nicolas_cardoso
     */    
    public static byte[] long2bytes(long longnb, int bytesLength){
        int numberOfDigit;
        Long digit;
        byte[] bytesToReturn;
        
        // Search the number of digit in the integer
        numberOfDigit = (int)((Long.toBinaryString(longnb).length() / 8.0 ) + 0.99); // 0.99 is added to round up the number (can't add 1 beacause in some case, the right number is found, so added 1 make it false althought it's not)

        // Create the array of bytes
        bytesToReturn = new byte[bytesLength];
        
        if( numberOfDigit > bytesLength ) {
            logger.severe("Impossible to fit the integer in that bytesLength in method 'int2bytes'. Existing program.");
            System.exit(-1);
        }
        
        // Fill in the integer in the array
        for( int i = bytesLength - 1; i >= bytesLength - numberOfDigit; i-- ) {
            digit = longnb % 256;
            longnb = longnb / 256;
            bytesToReturn[i] = digit.byteValue();
        }

        // Fill in the rest of the array with zeros
        for( int i = bytesLength - numberOfDigit - 1; i >= 0; i-- ) {
            bytesToReturn[i] = 0;
        }
        
        return bytesToReturn;
    }       


    /**
     * Sort a map of title according to their artist name then title name.
     *
     *@param titles map of the titles to be sorted.
     *
     *@return sorted list of titles.
     *
     *@author nicolas_cardoso
     */
    public static List sortByArtistTitle(Map titles){
        List listToReturn = new ArrayList();
        Set titlesSet = titles.keySet();
        int i = 1;
        String a,b;
        int j;
        Object temp;
        
        Iterator it = titlesSet.iterator();
        
        if( it.hasNext() ) {
            listToReturn.add(0, it.next()); //If the set isn't empty, fill in the first element
        }
        
        while( it.hasNext() ) { //For each title
            listToReturn.add(i, it.next()); // Add the current element
            
            //Sort the current List in "artist - title" order
            a = ((Title)listToReturn.get(i-1)).getArtist() + ((Title)listToReturn.get(i-1)).getTitle();
            b = ((Title)listToReturn.get(i)).getArtist() + ((Title)listToReturn.get(i)).getTitle();
            j = i;
            while( a.compareTo(b) > 0 ) {
                temp = listToReturn.get(j);
                listToReturn.remove(j);
                listToReturn.add(j, listToReturn.get(j-1));
                listToReturn.remove(j-1);
                listToReturn.add(j-1, temp);
                
                if( j-2 < 0 ) {
                    break; //If j-2 is negative, the beginnig of the list is reached, there is nothing left to sort
                }
                j--;
                a = ((Title)listToReturn.get(j-1)).getArtist() + ((Title)listToReturn.get(j-1)).getTitle();
                b = ((Title)listToReturn.get(j)).getArtist() + ((Title)listToReturn.get(j)).getTitle();
            }
            
            i++;
        }
        
        return listToReturn;
    }


    /**
     * Sort a map of title according to their genre then title name.
     *
     *@param titles map of the titles to be sorted.
     *
     *@return sorted list of titles.
     *
     *@author nicolas_cardoso
     */
    public static List sortByGenreTitle(Map titles){
        List listToReturn = new ArrayList();
        Set titlesSet = titles.keySet();
        int i = 1;
        String a,b;
        int j;
        Object temp;
        
        Iterator it = titlesSet.iterator();
        
        if( it.hasNext() ) {
            listToReturn.add(0, it.next()); //If the set isn't empty, fill in the first element
        }
        
        while( it.hasNext() ) { //For each title
            listToReturn.add(i, it.next()); // Add the current element
            
            //Sort the current List in "genre - title" order
            a = ((Title)listToReturn.get(i-1)).getGenre() + ((Title)listToReturn.get(i-1)).getTitle();
            b = ((Title)listToReturn.get(i)).getGenre() + ((Title)listToReturn.get(i)).getTitle();
            j = i;
            while( a.compareTo(b) > 0 ) {
                temp = listToReturn.get(j);
                listToReturn.remove(j);
                listToReturn.add(j, listToReturn.get(j-1));
                listToReturn.remove(j-1);
                listToReturn.add(j-1, temp);
                
                if( j-2 < 0 ) {
                    break; //If j-2 is negative, the beginnig of the list is reached, there is nothing left to sort
                }
                j--;
                a = ((Title)listToReturn.get(j-1)).getGenre() + ((Title)listToReturn.get(j-1)).getTitle();
                b = ((Title)listToReturn.get(j)).getGenre() + ((Title)listToReturn.get(j)).getTitle();
            }
            
            i++;
        }
        
        return listToReturn;
    }


    /**
     * Sort a map of title according to their genre then artist name then album name.
     *
     *@param titles map of the titles to be sorted.
     *
     *@return sorted list of titles.
     *
     *@author nicolas_cardoso
     */
    public static List sortByGenreArtistAlbumTitle(Map titles){
        List listToReturn = new ArrayList();
        Set titlesSet = titles.keySet();
        int i = 1;
        String a,b;
        int j,tempTitleNumber;
        Object temp;
        
        Iterator it = titlesSet.iterator();
        
        if( it.hasNext() ) {
            listToReturn.add(0, it.next()); //If the set isn't empty, fill in the first element
        }
        
        while( it.hasNext() ) { //For each title
            listToReturn.add(i, it.next()); // Add the current element
            
            //Sort the current List in "genre - artist - album - titleNumber" order
            tempTitleNumber = ((Title)listToReturn.get(i-1)).getTitleNumber();
            if(tempTitleNumber < 10) { //Add a zero to the title number is it's less than 10 to be properly compared as a string
                a = ((Title)listToReturn.get(i-1)).getGenre() + ((Title)listToReturn.get(i-1)).getArtist() + ((Title)listToReturn.get(i-1)).getAlbum() + "0" + tempTitleNumber;
            }
            else{
                a = ((Title)listToReturn.get(i-1)).getGenre() + ((Title)listToReturn.get(i-1)).getArtist() + ((Title)listToReturn.get(i-1)).getAlbum() + tempTitleNumber;
            }
            tempTitleNumber = ((Title)listToReturn.get(i)).getTitleNumber();
            if(tempTitleNumber < 10) {
                b = ((Title)listToReturn.get(i)).getGenre() + ((Title)listToReturn.get(i)).getArtist() + ((Title)listToReturn.get(i)).getAlbum() + "0" + tempTitleNumber;
            }
            else{
                b = ((Title)listToReturn.get(i)).getGenre() + ((Title)listToReturn.get(i)).getArtist() + ((Title)listToReturn.get(i)).getAlbum() + tempTitleNumber;
            }
            j = i;
            while( a.compareTo(b) > 0 ) {
                temp = listToReturn.get(j);
                listToReturn.remove(j);
                listToReturn.add(j, listToReturn.get(j-1));
                listToReturn.remove(j-1);
                listToReturn.add(j-1, temp);
                
                if( j-2 < 0 ) {
                    break; //If j-2 is negative, the beginnig of the list is reached, there is nothing left to sort
                }
                j--;
                tempTitleNumber = ((Title)listToReturn.get(j-1)).getTitleNumber();
                if(tempTitleNumber < 10) { //Add a zero to the title number is it's less than 10 to be properly compared as a string
                    a = ((Title)listToReturn.get(i-1)).getGenre() + ((Title)listToReturn.get(j-1)).getArtist() + ((Title)listToReturn.get(j-1)).getAlbum() + "0" + tempTitleNumber;
                }
                else{
                    a = ((Title)listToReturn.get(i-1)).getGenre() + ((Title)listToReturn.get(j-1)).getArtist() + ((Title)listToReturn.get(j-1)).getAlbum() + tempTitleNumber;
                }
                tempTitleNumber = ((Title)listToReturn.get(j)).getTitleNumber();
                if(tempTitleNumber < 10) {
                    b = ((Title)listToReturn.get(i)).getGenre() + ((Title)listToReturn.get(j)).getArtist() + ((Title)listToReturn.get(j)).getAlbum() + "0" + tempTitleNumber;
                }
                else{
                    b = ((Title)listToReturn.get(i)).getGenre() + ((Title)listToReturn.get(j)).getArtist() + ((Title)listToReturn.get(j)).getAlbum() + tempTitleNumber;
                }
            }
            
            i++;
        }
        return listToReturn;
    }


    /**
     * Sort a map of title according to their album name then title number.
     *
     *@param titles map of the titles to be sorted.
     *
     *@return sorted list of titles.
     *
     *@author nicolas_cardoso
     */
    public static List sortByAlbumTitleNumber(Map titles){
        List listToReturn = new ArrayList();
        Set titlesSet = titles.keySet();
        int i = 1;
        String a,b;
        int j,tempTitleNumber;
        Object temp;
        
        Iterator it = titlesSet.iterator();
        
        if( it.hasNext() ) {
            listToReturn.add(0, it.next()); //If the set isn't empty, fill in the first element
        }
        
        while( it.hasNext() ) { //For each title
            listToReturn.add(i, it.next()); // Add the current element
            
            //Sort the current List in "album - titleNumber" order
            tempTitleNumber = ((Title)listToReturn.get(i-1)).getTitleNumber();
            if(tempTitleNumber < 10) { //Add a zero to the title number is it's less than 10 to be properly compared as a string
                a = ((Title)listToReturn.get(i-1)).getAlbum() + "0" + tempTitleNumber;
            }
            else{
                a = ((Title)listToReturn.get(i-1)).getAlbum() + tempTitleNumber;
            }
            tempTitleNumber = ((Title)listToReturn.get(i)).getTitleNumber();
            if(tempTitleNumber < 10) {
                b = ((Title)listToReturn.get(i)).getAlbum() + "0" + tempTitleNumber;
            }
            else{
                b = ((Title)listToReturn.get(i)).getAlbum() + tempTitleNumber;
            }
            j = i;
            while( a.compareTo(b) > 0 ) {
                temp = listToReturn.get(j);
                listToReturn.remove(j);
                listToReturn.add(j, listToReturn.get(j-1));
                listToReturn.remove(j-1);
                listToReturn.add(j-1, temp);
                
                if( j-2 < 0 ) {
                    break; //If j-2 is negative, the beginnig of the list is reached, there is nothing left to sort
                }
                j--;
                tempTitleNumber = ((Title)listToReturn.get(j-1)).getTitleNumber();
                if(tempTitleNumber < 10) { //Add a zero to the title number is it's less than 10 to be properly compared as a string
                    a = ((Title)listToReturn.get(j-1)).getAlbum() + "0" + tempTitleNumber;
                }
                else{
                    a = ((Title)listToReturn.get(j-1)).getAlbum() + tempTitleNumber;
                }
                tempTitleNumber = ((Title)listToReturn.get(j)).getTitleNumber();
                if(tempTitleNumber < 10) {
                    b = ((Title)listToReturn.get(j)).getAlbum() + "0" + tempTitleNumber;
                }
                else{
                    b = ((Title)listToReturn.get(j)).getAlbum() + tempTitleNumber;
                }
            }
            
            i++;
        }
        
        return listToReturn;
    }


    /**
     * Sort a map of title according to their artist name then album name then title number.
     *
     *@param titles map of the titles to be sorted.
     *
     *@return sorted list of titles.
     *
     *@author nicolas_cardoso
     */
    public static List sortByArtistAlbumTitleNumber(Map titles){
        List listToReturn = new ArrayList();
        Set titlesSet = titles.keySet();
        int i = 1;
        String a,b;
        int j,tempTitleNumber;
        Object temp;
        
        Iterator it = titlesSet.iterator();
        
        if( it.hasNext() ) {
            listToReturn.add(0, it.next()); //If the set isn't empty, fill in the first element
        }
        
        while( it.hasNext() ) { //For each title
            listToReturn.add(i, it.next()); // Add the current element
            
            //Sort the current List in "album - titleNumber" order
            tempTitleNumber = ((Title)listToReturn.get(i-1)).getTitleNumber();
            if(tempTitleNumber < 10) { //Add a zero to the title number is it's less than 10 to be properly compared as a string
                a = ((Title)listToReturn.get(i-1)).getArtist() + ((Title)listToReturn.get(i-1)).getAlbum() + "0" + tempTitleNumber;
            }
            else{
                a = ((Title)listToReturn.get(i-1)).getArtist() + ((Title)listToReturn.get(i-1)).getAlbum() + tempTitleNumber;
            }
            tempTitleNumber = ((Title)listToReturn.get(i)).getTitleNumber();
            if(tempTitleNumber < 10) {
                b = ((Title)listToReturn.get(i)).getArtist() + ((Title)listToReturn.get(i)).getAlbum() + "0" + tempTitleNumber;
            }
            else{
                b = ((Title)listToReturn.get(i)).getArtist() + ((Title)listToReturn.get(i)).getAlbum() + tempTitleNumber;
            }
            j = i;
            while( a.compareTo(b) > 0 ) {
                temp = listToReturn.get(j);
                listToReturn.remove(j);
                listToReturn.add(j, listToReturn.get(j-1));
                listToReturn.remove(j-1);
                listToReturn.add(j-1, temp);
                
                if( j-2 < 0 ) {
                    break; //If j-2 is negative, the beginnig of the list is reached, there is nothing left to sort
                }
                j--;
                tempTitleNumber = ((Title)listToReturn.get(j-1)).getTitleNumber();
                if(tempTitleNumber < 10) { //Add a zero to the title number is it's less than 10 to be properly compared as a string
                    a = ((Title)listToReturn.get(j-1)).getArtist() + ((Title)listToReturn.get(j-1)).getAlbum() + "0" + tempTitleNumber;
                }
                else{
                    a = ((Title)listToReturn.get(j-1)).getArtist() + ((Title)listToReturn.get(j-1)).getAlbum() + tempTitleNumber;
                }
                tempTitleNumber = ((Title)listToReturn.get(j)).getTitleNumber();
                if(tempTitleNumber < 10) {
                    b = ((Title)listToReturn.get(j)).getArtist() + ((Title)listToReturn.get(j)).getAlbum() + "0" + tempTitleNumber;
                }
                else{
                    b = ((Title)listToReturn.get(j)).getArtist() + ((Title)listToReturn.get(j)).getAlbum() + tempTitleNumber;
                }
            }
            
            i++;
        }
        
        return listToReturn;
    }

    
    /**
     * Sort a map of title according to their title ID.
     *
     *@param titles map of the titles to be sorted.
     *
     *@return sorted list of titles.
     *
     *@author nicolas_cardoso
     */
    public static List sortByTitleId(Map titles){
        List listToReturn = new ArrayList();
        Set titlesSet = titles.keySet();
        int i = 1;
        int a,b,j;
        Object temp;
        
        Iterator it = titlesSet.iterator();
        
        if( it.hasNext() ) {
            listToReturn.add(0, it.next()); //If the set isn't empty, fill in the first element
        }
        
        while( it.hasNext() ) { //For each title
            listToReturn.add(i, it.next()); // Add the current element
            
            //Sort the current List
            a = (Integer)titles.get(listToReturn.get(i-1));
            b = (Integer)titles.get(listToReturn.get(i));
            j = i;
            while( a > b ) {
                temp = listToReturn.get(j);
                listToReturn.remove(j);
                listToReturn.add(j, listToReturn.get(j-1));
                listToReturn.remove(j-1);
                listToReturn.add(j-1, temp);
                
                if( j-2 < 0 ) {
                    break; //If j-2 is negative, the beginnig of the list is reached, there is nothing left to sort
                }
                j--;
                a = (Integer)titles.get(listToReturn.get(j-1));
                b = (Integer)titles.get(listToReturn.get(j));
            }
            
            i++;
        }
        
        return listToReturn;
    }
  
    /**
     *Converts the number represented by some value in an array of bytes to an int. For example, if the array is : ["1";"2";"3";"4"]=[49;50;51;52], the corresponding int will be "1234".
     *
     *@param bytes The array of bytes to convert
     *
     *@return The converted number.
     *
     *@author nicolas_cardoso
     */
    public static int charBytes2int(byte[] bytes){
        int i = bytes.length - 1;
        int ret = 0;
        byte val;
        int pow = 0;
        
        while(i >= 0){
            switch(bytes[i]){
                case 48:
                    val = 0;
                    break;
                case 49:
                    val = 1;
                    break;
                case 50:
                    val = 2;
                    break;
                case 51:
                    val = 3;
                    break;
                case 52:
                    val = 4;
                    break;
                case 53:
                    val = 5;
                    break;
                case 54:
                    val = 6;
                    break;
                case 55:
                    val = 7;
                    break;
                case 56:
                    val = 8;
                    break;
                case 57:
                    val = 9;
                    break;
                default:
                    val = -1;
            }

            if(val >= 0){
                ret += val*Math.pow(10, pow);
                pow++;
            }
            i--;
        }
        return ret;
    }

}
