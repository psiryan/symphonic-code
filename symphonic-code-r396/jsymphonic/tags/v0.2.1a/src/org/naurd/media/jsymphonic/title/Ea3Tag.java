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
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.lang.Math.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
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
    public static final boolean NOKEY = false;
    public static final boolean KEY = true;

    private String albumName= "";
    private String artistName = "";
    private String titleName = "";
    private String genre = "";
    private int trackNumber = 0;
    private int year = 0;
    private int length = 0;
    private int frequency = 44100;
    private boolean vbr = false;
    private int bitRate = 0;
    private byte mpegVersion = 0;
    private byte layer = 0;
    
    
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
        int infiniteLoop = 0; // This variable is used to detect and stop infinite loop, in case of...
        
        if(JSymphonic.debug){System.out.println("Scanning file "+omaFile.getName());}

        try{
            java.io.InputStream omaFileStream = omaFile.toURI().toURL().openStream(); //Opens the file in stream mode (must be in try-catch)
            
            omaFileStream.skip(10); //Skips the header which is ["e" "a" "3" 3 0 0 0 0 17 76]
            
            omaFileStream.read(tagId); //Reads the first tagId

            while(!isZero(tagId)) {
                stringTagId = new String(tagId, 0, 4); //Convert TagId to string
                
                if(JSymphonic.debug) {System.out.println("Tag read - tag:"+stringTagId);}

                
                omaFileStream.read(tagLength); //Reads the length of the tag
                intTagLength = bytes2int(tagLength) - 1; //and convert to integer (-1 is for the skip of character encodage 2)
                
                omaFileStream.skip(3); //Skips 3 bytes (which are [0 0 2])
                
                if(stringTagId.compareTo("GEOB") == 0){
                    // We have a picture
                    if(JSymphonic.debug) {System.out.println("Tag read - a picture has been found");}
                    
                    // Skip the picture
                    // Note that skip method is not very good and reads data it skips, and it may not skip all that was wanted, so it's nessecary to check what was really skiped...
                    long amountToSkip = intTagLength; 
                    long reallyskiped;
                    while(amountToSkip > 0) {
                        reallyskiped = omaFileStream.skip(amountToSkip);
                        amountToSkip -= reallyskiped;
                    }
                    
                    // Read next tag for next iteration
                    omaFileStream.read(tagId); //Reads the next tagId
                    continue; // Rest of the loop isn't to do
                }
                
                if(intTagLength > 3200) { // If the length to be read is wrong, stop the loop
                    System.err.println("Error of tag length while scanning "+ omaFile.getName() + ".\nSkip title.");
                    
                    tagId = new byte[]{0,0,0,0}; //Put the tagId to zero to stop the loop
                    continue; // Rest of the loop isn't to do
                }
                
                // If it's not a picture:
                tagValue = new byte[intTagLength]; 
                omaFileStream.read(tagValue); //Reads the value of the tag
                stringTagValue = new String(tagValue, 0, intTagLength); //and convert it to string
                stringTagValue = stringTagValue.replace(""+(char)0,""); //Remove empty characters from the TagValue (these characters are due to 16-bits encodage in Ea3Tag)
                intTagValue = charBytes2int(tagValue); //also convert to integer if it is a number
                
                
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
                    if(genre.length() == 0){
                        genre = "unknown genre"; // if an empty genre has been read, we put a default value
                    }
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
                    String[] splitenString = stringTagValue.split("/"); // Date can be on the form "2004/01/01" or "04/01/01"
                    stringTagValue = splitenString[0]; // Only the first value is interesting, the year
                    if(stringTagValue.length() == 2 && Integer.parseInt(stringTagValue) < 50) {
                        // If the year is written with two number less than 50 (eg 07) wue suppose that the rigth date is 2007
                        stringTagValue = "20" + stringTagValue;
                    }
                    if(stringTagValue.length() == 2 && Integer.parseInt(stringTagValue) >= 50) {
                        // If the year is written with two number greater than 50 (eg 94) wue suppose that the rigth date is 1994
                        stringTagValue = "19" + stringTagValue;
                    }
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
                omaFileStream.read(tagId); //Reads the next tagId
                
                // Before next iteration, check if we are in an infinite loop
                infiniteLoop++;
                if(infiniteLoop > 1000) {
                    // an infinite loop has been detected, print info and stop the loop
                    System.err.println("An infinite loop has been detected and stop while reading EA3 tag from file " + omaFile.getName() + ".\nPlease contact the developers if you are reading this error.");
                }
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
        vbr = title.getVbr();
        bitRate = title.getBitRate();
        mpegVersion = title.getMpegVersion();
        layer = title.getLayer();
        frequency = title.getFrequency();
    }

    //Private Methods
    /**
     * Convert a bitrate given in a 10-based integer (human understandable) (like 128, 256, 320...) in a integer corresponding to the table given at "http://javamusictag.sourceforge.net/api/index.html"
     */
    private int bitRateConversion(int bitRateHuman, byte layer, byte mpegVersion) {
        // First, fill in the HashMap
        HashMap bitRateTable = new HashMap();
        bitRateTable.put(8, 0);
        bitRateTable.put(16, 1);
        bitRateTable.put(24, 2);
        bitRateTable.put(32, 3);
        bitRateTable.put(40, 4);
        bitRateTable.put(48, 5);
        bitRateTable.put(56, 6);
        bitRateTable.put(64, 7);
        bitRateTable.put(80, 8);
        bitRateTable.put(96, 9);
        bitRateTable.put(112, 10);
        bitRateTable.put(128, 11);
        bitRateTable.put(160, 12);
        bitRateTable.put(192, 13);
        bitRateTable.put(224, 14);
        bitRateTable.put(256, 15);
        bitRateTable.put(288, 16);
        bitRateTable.put(320, 17);
        bitRateTable.put(352, 18);
        bitRateTable.put(384, 19);
        bitRateTable.put(416, 20);
        bitRateTable.put(448, 21);
        // And the table of correspondance,
        int bitRateCorrespondance[] = {1,2,3,4,0,0,7,5,6,0,11,9,10,0,0,13,0,14,0,0,0,0, 0,0,0,1,0,2,3,4,5,6,7,8,9,10,11,12,0,13,0,14,0,0, 0,0,0,1,0,0,0,2,0,3,0,4,5,6,7,8,9,10,11,12,13,14, 0,0,0,1,2,3,4,5,6,7,8,9,10,11,12,13,0,14,0,0,0,0, 
        0,0,0,1,0,2,3,4,5,6,7,8,9,10,11,12,0,13,0,14,0,0, 0,0,0,1,0,0,0,2,0,3,0,4,5,6,7,8,9,10,11,12,13,14};

        
        // Then, we get the index of the bit rate from the first table
        Integer index = (Integer)bitRateTable.get(bitRateHuman);
        
        // And, from the index, the layer and the mpegVersion, we can look into the second table the int we want
        return bitRateCorrespondance[index + (layer - 1)*23 + (mpegVersion -2)*66];
    }
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
    public int getLength(){
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
        int intByte;
        
        while(i >= 0){
            intByte = bytes[i];
            if(intByte < 0) {
                intByte+= 256;
            }
            ret += intByte*Math.pow(16, (bytes.length - (i+1))*2);
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

    public void write(File omaFile, boolean key) {
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
            byte[] constant3;
            byte[] constant4;
            byte[] constant5;
            byte[] constant6;
/*
            This code is working for my walkman and Philipe one, but not Simon, let's try to do better
            if(key) { // if the files are encrypted, the EA3 tag shouldn't contain the same constant... ...
                constant3 = new byte[]{(byte)0x02,0,(byte)0x60,(byte)0xFF,(byte)0xFE,0,0,0,0,0,0,0,0};
                constant4 = new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
                constant5 = new byte[]{3,(byte)0x80,(byte)0xD9,0,0,0,(byte)0x26,(byte)0x78,0,0,(byte)0x01,(byte)0x79,0,0,0,0}; 
            }
            else {
                constant3 = new byte[]{(byte)0x02,0,(byte)0x60,(byte)0xFF,(byte)0xFF,0,0,0,0,1,0xF,0x50,0};
                constant4 = new byte[]{0,4,0,0,0,(byte)0xE8,(byte)0xD8,(byte)0x13,(byte)0x1F,(byte)0xD2,(byte)0xF5,(byte)0xB3,(byte)0xE7,(byte)0x83,(byte)0xDA,(byte)0x62}; 
                constant5 = new byte[]{3,(byte)0x80,(byte)0xD9,0x10,0,4,(byte)0x19,(byte)0xCB,0,0,(byte)0x28,(byte)0x30,0,0,0,0}; 
            }
 */
            if(key) { // if the files are encrypted, the EA3 tag shouldn't contain the same constant... ...
                constant3 = new byte[]{(byte)0x02,0,(byte)0x60,(byte)0xFF,(byte)0xFE,0,0,0,0,1,0xF,0x50,0};
            }
            else {
                constant3 = new byte[]{(byte)0x02,0,(byte)0x60,(byte)0xFF,(byte)0xFF,0,0,0,0,1,0xF,0x50,0};
            }
            
            constant4 = new byte[]{0,4,0,0,0,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0xC8,(byte)0xD8,(byte)0x36,(byte)0xD8,(byte)0x11,(byte)0x22,(byte)0x33,(byte)0x44}; 
            constant5 = new byte[]{3};
            constant6 = new byte[]{(byte)0x10};

            
/*
            What follows are the tests...
            // Constant for NW-A608 nico
            byte[] constant3 = {2,0,0x60,-1,-1,0,0,0,0,1,0xF,0x50,0}; //-1 represent FF in hexa
            byte[] constant4 = {0,4,0,0,0,(byte)0xE8,(byte)0xD8,(byte)0x13,(byte)0x1F,(byte)0xD2,(byte)0xF5,(byte)0xB3,(byte)0xE7,(byte)0x83,(byte)0xDA,(byte)0x62}; 
            byte[] constant5 = {3,(byte)0x80,(byte)0xD9,0x10,0,4,(byte)0x19,(byte)0xCB,0,0,(byte)0x28,(byte)0x30,0,0,0,0}; 

            // Constant for NW-HD5 phillipe
            byte[] constant3 = {2,0,0x60,-1,-2,0,0,0,0,1,0xF,0x50,0}; //-1 represent FF in hexa
            byte[] constant4 = {0,4,0,0,0,(byte)0xEB,(byte)0x4F,(byte)0x2B,(byte)0x7E,(byte)0x8A,(byte)0xF6,(byte)0x40,(byte)0xCB,(byte)0x44,(byte)0x42,(byte)0xB3};
            byte[] constant5 = {3,(byte)0x80,(byte)0xD9,0,0,0,(byte)0x26,(byte)0x78,0,0,(byte)0x01,(byte)0x79,0,0,0,0}; 
            
            // Constant from MP3FM -> JSymphonic_0.2a_build080208a.jar
            byte[] constant3 = {(byte)0x02,0,(byte)0x60,(byte)0xFF,(byte)0xFE,0,0,0,0,0,0,0,0}; 
            byte[] constant4 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            byte[] constant5 = {3,(byte)0x80,(byte)0xD9,0,0,0,(byte)0x26,(byte)0x78,0,0,(byte)0x01,(byte)0x79,0,0,0,0}; 
            
            // Constant common...TEST -> JSymphonic_0.2a_build080208b.jar
            byte[] constant3 = {(byte)0x02,0,(byte)0x60,(byte)0xFF,(byte)0xFE,0,0,0,0,(byte)0x01,(byte)0x0F,(byte)0x50,0}; 
            byte[] constant4 = {0,(byte)0x04,0,0,0,(byte)0xEB,(byte)0x4F,0,0,0,0,0,(byte)0xCB,(byte)0x44,(byte)0x42,(byte)0xB3};
            byte[] constant5 = {3,(byte)0x80,(byte)0xD9,0,0,0,(byte)0x26,(byte)0x78,0,0,(byte)0x01,(byte)0x79,0,0,0,0}; 
*/            
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
            if(titleName.length() > 60) { // We can't store in EA3 tag name containing more than 60 charactere
                titleName = (String)titleName.subSequence(0,59);
            }
            raf.write(OmaDataBaseTools.int2bytes(titleName.length()*2 + 1, 4));
            raf.write(constant2);
            OmaDataBaseTools.WriteString16(raf, titleName);
            headerlength += 4 + 7 + titleName.length()*2;

            //Artist name
            raf.write(tag3.getBytes());
            if(artistName.length() > 60) { // We can't store in EA3 tag name containing more than 60 charactere
                artistName = (String)artistName.subSequence(0,59);
            }
            raf.write(OmaDataBaseTools.int2bytes(artistName.length()*2 + 1, 4));
            raf.write(constant2);
            OmaDataBaseTools.WriteString16(raf, artistName);
            headerlength += 4 + 7 + artistName.length()*2;

            //Album name
            raf.write(tag4.getBytes());
            if(albumName.length() > 60) { // We can't store in EA3 tag name containing more than 60 charactere
                albumName = (String)albumName.subSequence(0,59);
            }
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
            /* Write the track number after a space and with a 0
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
            headerlength += 4 + 7 + 12*2; */
            
            /* Write the track number as it is. */
            if(trackNumber < 10) {
                raf.write(OmaDataBaseTools.int2bytes(23, 4));
            }
            else {
                raf.write(OmaDataBaseTools.int2bytes(25, 4));
            }
            raf.write(constant2);
            OmaDataBaseTools.WriteString16(raf, "OMG_TRACK");
            OmaDataBaseTools.WriteZeros(raf, 2);
            OmaDataBaseTools.WriteString16(raf, Integer.toString(trackNumber));
            if(trackNumber < 10) {
                headerlength += 4 + 7 + 10*2 + 2;
            }
            else {
                headerlength += 4 + 7 + 11*2 + 2;
            }
            
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
            
            //// Add title properties EA3 second header (??...)
            // 1st line
            raf.write(tag10.getBytes());
            raf.write(constant3);
            
            
            // 2nd line
            raf.write(constant4);
            

            // 3rd line
            raf.write(constant5); // 0x3 = MP3
            if(vbr) {
                raf.write(OmaDataBaseTools.int2bytes(144, 1)); // 144 = 0x90 = vbr
            }
            else {
                raf.write(OmaDataBaseTools.int2bytes(128, 1)); // 128 = 0x80 = cbr                
            }
            raf.write(OmaDataBaseTools.int2bytes(bitRateConversion(bitRate, layer, mpegVersion) + layer*16 + mpegVersion*64, 1)); // mpeg version(2bits), layer version(2bits), bitrate(4bits)
            raf.write(OmaDataBaseTools.int2bytes(16, 1)); // 16 = 0x10
            
            // Track length
            raf.write(OmaDataBaseTools.int2bytes(length, 4));
            
            // Number of frames
                                // CODE FROM ML_SONY
				int  SAMPLING_RATES[] = {11025, 12000, 8000, 0, 0, 0, 22050, 24000, 16000, 44100, 48000, 32000};
				int  SAMPLE_PER_FRAME[] = {0,576,1152,384,0,0,0,0,0,576,1152,384,0,1152,1152,384};
				
				//sample per frame 0=reserved
				//          MPG2.5 res        MPG2   MPG1 
				//reserved  0      0          0      0
				//Layer III 576    0          576    1152 	
				//Layer II  1152   0          1152   1152 	
				//Layer I   384    0          384    384 
				
				//int samplingRate = SAMPLING_RATES[(mpegVersion * 3) + samplingRateIndex];
				int samplingRate = frequency;
                                int samplePerFrame = SAMPLE_PER_FRAME[(mpegVersion * 4) + layer];
				int nbFrames = (length * samplingRate) / samplePerFrame;

            raf.write(OmaDataBaseTools.int2bytes(nbFrames, 4));
            
            // 4 zeros to finidh the 3rd line
            OmaDataBaseTools.WriteZeros(raf, 4);
            
            
            //Fill in with zeros
            OmaDataBaseTools.WriteZeros(raf, 3*0x10);
            
            raf.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }    
}