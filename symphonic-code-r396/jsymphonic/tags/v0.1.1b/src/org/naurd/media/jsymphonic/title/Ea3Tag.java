/*
 * Ea3Tag.java
 *
 * Created on 20 mai 2007, 08:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.title;
import java.io.File;
import java.io.RandomAccessFile;
import java.lang.Math.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.naurd.media.jsymphonic.manager.JSymphonic;
import org.naurd.media.jsymphonic.system.sony.nw.OmaDataBaseTools;

/**
 *An instance of the Ea3Tag describes a Sony tag used in ".oma" files, including title information (like Artist, Album, year,...).
 *
 *This class includes methods for viewing and editing tags.
 *
 *@version 30.05.2007
 *@author Nicolas Cardoso
 */
public class Ea3Tag{
    private String albumName= "";
    private String artistName = "";
    private String titleName = "";
    private String genre = "";
    private int trackNumber = 0;
    private int year = 0;
    private long length = 0;
    
    /* Constructors */
    /**
    *Allows to create an instance of Ea3Tag from an existing ".oma" file.
    *
    *@param omaFile The file which is read to draw up the tag.
    */
    public Ea3Tag(java.io.File omaFile) {
        byte[] tagId = new byte[4]; //Used to read the name of the tag (like 'TIT2' or 'TLEN')
        byte[] tagLength = new byte[4]; //Used to read the number of bytes in the tag
        byte[] tagValue; //Used to read the value of the tag (length is defined later)
        String stringTagValue;
        String stringTagId;
        int intTagLength;
        int intTagValue;

        try{
            java.io.InputStream omaFileStream = omaFile.toURI().toURL().openStream(); //Opens the file in stream mode (must be in try-catch)
            
            omaFileStream.skip(10); //Skips the header which is ["e" "a" "3" 3 0 0 0 0 17 76]
            
            omaFileStream.read(tagId); //Reads the first tagId

            while(!isZero(tagId)) {
                omaFileStream.read(tagLength); //Reads the length of the tag
                intTagLength = bytes2int(tagLength) - 1; //and convert to integer (-1 is for the skip of character encodage 2)

                if((intTagLength < 0) || (intTagLength > 128) ){ //Check read data
                    continue;
                }
                
                omaFileStream.skip(3); //Skips 3 bytes (which are [0 0 2])
                tagValue = new byte[intTagLength];
                omaFileStream.read(tagValue); //Reads the value of the tag
                stringTagValue = new String(tagValue, 0, intTagLength); //and convert it to string
                stringTagValue = stringTagValue.replace(""+(char)0,""); //Remove empty characters from the TagValue (these characters are due to 16-bits encodage in Ea3Tag)
                intTagValue = charBytes2int(tagValue); //also convert to integer if it is a number
                stringTagId = new String(tagId, 0, 4); //Convert TagId to string
                
                if(stringTagId.compareTo("TIT2") == 0){
                    titleName = stringTagValue;
                    if(JSymphonic.debug) {System.out.println("Tag read - titleName:" +stringTagValue);}
                }
                if(stringTagId.compareTo("TPE1") == 0){
                    artistName = stringTagValue;
                    if(JSymphonic.debug) {System.out.println("Tag read - artistName:" +stringTagValue);}
                }
                if(stringTagId.compareTo("TALB") == 0){
                    albumName = stringTagValue;
                    if(JSymphonic.debug) {System.out.println("Tag read - albumName:" +stringTagValue);}
                }
                if(stringTagId.compareTo("TCON") == 0){
                    genre = stringTagValue;
                    if(JSymphonic.debug) {System.out.println("Tag read - genre:" +stringTagValue);}
                }
                if(stringTagId.compareTo("TXXX") == 0 && stringTagValue.contains("OMG_TRACK")){
                    stringTagValue = stringTagValue.replace("OMG_TRACK","");
                    stringTagValue = stringTagValue.replace(" ","");
                    trackNumber = Integer.parseInt(stringTagValue);
                    if(JSymphonic.debug) {System.out.println("Tag read - trackNumber:" +trackNumber);}
                }
                if(stringTagId.compareTo("TXXX") == 0 && stringTagValue.contains("OMG_TRLDA")){
                    stringTagValue = stringTagValue.replace("OMG_TRLDA","");
                    stringTagValue = stringTagValue.replace(" ","");
                    stringTagValue = stringTagValue.replace("/01/0100:00:00","");
                    year = Integer.parseInt(stringTagValue);
                    if(JSymphonic.debug) {System.out.println("Tag read - year:" +year);}
                }
                if(stringTagId.compareTo("TYER") == 0){
                    year = intTagValue;
                    if(JSymphonic.debug) {System.out.println("Tag read - year:" +year);}
                }
                if(stringTagId.compareTo("TLEN") == 0){
                    length = intTagValue;
                    if(JSymphonic.debug) {System.out.println("Tag read - length:" +length);}
                }
                omaFileStream.mark(3200); //Replaces the mark before read tagId, to allow reset if data read is not good, with readLimit=3200 (because full EA3Tag is 3168)
                omaFileStream.read(tagId); //Reads the next tagId
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Ea3Tag(Title title) {
        titleName= title.getTitle();
        artistName = title.getArtist();
        albumName = title.getAlbum();
        genre = title.getGenre();
        trackNumber = title.getTitleNumber();
        year = title.getYear();
        length = title.getLength();
    }

    //Private Methods
    /**
    *Obtains the album name.
    *
    *@return The album name.
    */
    public String getAlbumName(){
        return albumName;
    }

    /**
    *Obtains the artist name.
    *
    *@return The artist name.
    */
    public String getArtistName(){
        return artistName;
    }

    /**
    *Obtains the title name.
    *
    *@return The title name.
    */
    public String getTitleName(){
        return titleName;
    }

    /**
    *Obtains the genre.
    *
    *@return The genre.
    */
    public String getGenre(){
        return genre;
    }

    /**
    *Obtains the length.
    *
    *@return The length in milliseconds.
    */
    public long getLength(){
        return length;
    }

    /**
    *Obtains the year.
    *
    *@return The year (YYYY).
    */
    public int getYear(){
        return year;
    }

    /**
    *Obtains the track number.
    *
    *@return The track number.
    */
    public int getTrackNumber(){
        return trackNumber;
    }
    
    //Private Methods
    /**
    *Tells is an array of bytes is full with zeros.
    *
    *@param bytes The array of bytes to test
    *@return The answer, true for "yes" and false for "no"
    */
    private boolean isZero(byte[] bytes){
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
    *Converts an array of bytes to an int. For example, if the array is : [1;2;3;4], the corresponding int will be "1234".
    *
    *@param bytes The array of bytes to convert
    *@return The converted number.
    */
    private int bytes2int(byte[] bytes){
        int i = bytes.length - 1;
        int ret = 0;
        
        while(i >= 0){
            ret += bytes[i]*Math.pow(10, bytes.length - (i+1));
            i--;
        }
        return ret;
    }

    /**
    *Converts the number represented by value in an array of bytes to an int. For example, if the array is : ["1";"2";"3";"4"]=[49;50;51;52], the corresponding int will be "1234".
    *
    *@param bytes The array of bytes to convert
    *@return The converted number.
    */
    private int charBytes2int(byte[] bytes){
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

    public void write(File omaFile) {
        RandomAccessFile raf;
        
        try{ //Create new file
            if( omaFile.exists() ) {
                omaFile.delete(); // Delete file if it exist
            }
            omaFile.createNewFile(); // Create a new file
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // Write data in the file :
        try{
            raf = new RandomAccessFile(omaFile, "rw"); //Open the file in RAF
            int headerlength = 0;
            
            byte[] constant1 = {3,0,0,0,0,23,118};
            byte[] constant2 = {0,0,2};
            byte[] constant3 = {2,0,0x60,-1,-1,0,0,0,0,1,0xF,0x50,0}; //-1 represent FF in hexa
            byte[] constant4 = {0,4,0,0,0,(byte)0xE8,(byte)0xD8,(byte)0x13,(byte)0x1F,(byte)0xD2,(byte)0xF5,(byte)0xB3,(byte)0xE7,(byte)0x83,(byte)0xDA,(byte)0x62}; 
            byte[] constant5 = {3,(byte)0x80,(byte)0xD9,0x10,0,4,(byte)0x19,(byte)0xCB,0,0,(byte)0x28,(byte)0x30,0,0,0,0}; 

            String tag1 = "ea3";
            String tag2 = "TIT2";
            String tag3 = "TPE1";
            String tag4 = "TALB";
            String tag5 = "TCON";
            String tag6 = "TXXX";
            String tag7 = "TYER";
            String tag8 = "TLEN";
            String tag10 = "EA3";

            
            //// Add tag with title info (title name, album name...)
            //Header
            raf.write(tag1.getBytes());
            raf.write(constant1);
            headerlength += 10;
            
            //Title name
            raf.write(tag2.getBytes());
            raf.write(OmaDataBaseTools.int2bytes(titleName.length()*2 + 1, 4));
            raf.write(constant2);
            OmaDataBaseTools.WriteString16(raf, titleName);
            headerlength += 4 + 7 + titleName.length()*2;

            //Artist name
            raf.write(tag3.getBytes());
            raf.write(OmaDataBaseTools.int2bytes(artistName.length()*2 + 1, 4));
            raf.write(constant2);
            OmaDataBaseTools.WriteString16(raf, artistName);
            headerlength += 4 + 7 + artistName.length()*2;

            //Album name
            raf.write(tag4.getBytes());
            raf.write(OmaDataBaseTools.int2bytes(albumName.length()*2 + 1, 4));
            raf.write(constant2);
            OmaDataBaseTools.WriteString16(raf, albumName);
            headerlength += 4 + 7 + albumName.length()*2;
            
            //Genre
            raf.write(tag5.getBytes());
            raf.write(OmaDataBaseTools.int2bytes(genre.length()*2 + 1, 4));
            raf.write(constant2);
            OmaDataBaseTools.WriteString16(raf, genre);
            headerlength += 4 + 7 + genre.length()*2;

            //Track number
            raf.write(tag6.getBytes());
            raf.write(OmaDataBaseTools.int2bytes(25, 4));
            raf.write(constant2);
            OmaDataBaseTools.WriteString16(raf, "OMG_TRACK ");
            if(trackNumber < 10) {
                OmaDataBaseTools.WriteString16(raf, "0");
                OmaDataBaseTools.WriteString16(raf, Integer.toString(trackNumber));
            }
            else {
                OmaDataBaseTools.WriteString16(raf, Integer.toString(trackNumber));
            }
            headerlength += 4 + 7 + 12*2;
            
            //Year
            raf.write(tag7.getBytes());
            raf.write(OmaDataBaseTools.int2bytes(4*2 + 1, 4));
            raf.write(constant2);
            if(year < 1000) {
                OmaDataBaseTools.WriteString16(raf, "0000");
            }
            else {
                OmaDataBaseTools.WriteString16(raf, Integer.toString(year));
            }
            headerlength += 4 + 7 + 4*2;
            
            //Year again
            raf.write(tag6.getBytes());
            raf.write(OmaDataBaseTools.int2bytes(59, 4));
            raf.write(constant2);
            OmaDataBaseTools.WriteString16(raf, "OMG_TRLDA ");
            if(year < 1000) {
                OmaDataBaseTools.WriteString16(raf, "0000");
            }
            else {
                OmaDataBaseTools.WriteString16(raf, Integer.toString(year));
            }
            OmaDataBaseTools.WriteString16(raf, "/01/01 00:00:00");
            headerlength += 4 + 7 + 29*2;
            
            // Track length
            if(length >0) {
                raf.write(tag8.getBytes());
                String lengthString = Long.toString(length);
                raf.write(OmaDataBaseTools.int2bytes(lengthString.length()*2 + 1, 4));
                raf.write(constant2);
                OmaDataBaseTools.WriteString16(raf, lengthString);
                headerlength += 4 + 7 + lengthString.length()*2;
            }
            

            //Fill in with zeros
            OmaDataBaseTools.WriteZeros(raf, 3072 - headerlength);
            
            //// Add title properties (??...)
            raf.write(tag10.getBytes());
            raf.write(constant3);
            raf.write(constant4);
            raf.write(constant5);
            
            //Fill in with zeros
            OmaDataBaseTools.WriteZeros(raf, 3*0x10);
            
            raf.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}