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
 * Ea3Tag.java
 *
 * Created on 20 mai 2007, 08:21
 *
 */


package org.naurd.media.jsymphonic.title;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Math.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naurd.media.jsymphonic.toolBox.OmaDataBaseToolBox;

/**
 *An instance of the Ea3Tag describes a Sony tag used in ".oma" files, including title information (like Artist, Album, year,...).
 *
 *This class includes methods for viewing, writing and editing tags.
 *
 *@version 30.05.2007
 *@author Nicolas Cardoso
 */
public class Ea3Tag extends Tag{
/* FIELDS */
    // File information (all format)
    protected int length = 210; // Length of the title in seconds
    protected int bitRate = 128; // Bitrate of the title in kbps
    protected boolean vbr = false; // Indicates if the title uses variable or fix bitrate, true = vbr, false = cbr
    protected double frequency = 44.1; // Frequency of the title, given in kHz
    
    // Information for MP3 files
    protected byte mpegVersion = 3; // Mpeg version of the file
    protected byte layer = 1; // Layer of the file    
    
    //Other
    private static Logger logger = Logger.getLogger("org.naurd.media.jsymphonic.title.Ea3Tag");
    
/* CONSTRUCTORS */
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
        String logInfo; // A string to save all the info when reading a title
        int intTagLength;
        int intTagValue;
        int infiniteLoop = 0; // This variable is used to detect and stop infinite loop, in case of...
        
        logInfo = "Scanning file "+omaFile.getName();

        if(omaFile.length() <= 0) {
            logger.warning("ERROR: File is empty.");
            return;
        }
            
        try{
            java.io.InputStream omaFileStream = omaFile.toURI().toURL().openStream(); //Opens the file in stream mode (must be in try-catch)

            omaFileStream.skip(10); //Skips the header which is ["e" "a" "3" 3 0 0 0 0 17 76]
            
            omaFileStream.read(tagId); //Reads the first tagId
              
            while(!isZero(tagId)) {
                stringTagId = new String(tagId, 0, 4); //Convert TagId to string
                
                logInfo += "; "+stringTagId + ": ";

                
                omaFileStream.read(tagLength); //Reads the length of the tag
                intTagLength = bytes2int(tagLength) - 1; //and convert to integer (-1 is for the skip of character encodage 2)
                
                omaFileStream.skip(3); //Skips 3 bytes (which are [0 0 2])
                
                if(stringTagId.compareTo("GEOB") == 0){
                    // We have a picture
                    logInfo += " a picture has been found";
                    
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
                    logger.info(logInfo);
                    logger.warning("Error of tag length while scanning "+ omaFile.getName() + ".\nSkip title.");
                    return;
                }
                
                // If it's not a picture:
                tagValue = new byte[intTagLength]; 
                omaFileStream.read(tagValue); //Reads the value of the tag
                //這裡有問題，並沒有把讀出的字串用UTF8編碼來處理 Michael
                stringTagValue = new String(tagValue,"UTF16"); // Change it for fix Unicode problem by Michael Chen 2008/3/23
//                stringTagValue = new String(tagValue, 0, intTagLength); //and convert it to string
                stringTagValue = stringTagValue.replace(""+(char)0,""); //Remove empty characters from the TagValue (these characters are due to 16-bits encodage in Ea3Tag)
                intTagValue = charBytes2int(tagValue); //also convert to integer if it is a number
                
                if(stringTagId.compareTo("TIT2") == 0){
                    titleName = stringTagValue;
                    logInfo += " titleName:" +stringTagValue;
                }
                if(stringTagId.compareTo("TPE1") == 0){
                    artistName = stringTagValue;
                    logInfo += " artistName:" +stringTagValue;
                }
                if(stringTagId.compareTo("TALB") == 0){
                    albumName = stringTagValue;
                    logInfo += " albumName:" +stringTagValue;
                }
                if(stringTagId.compareTo("TCON") == 0){
                    genre = stringTagValue;
                    if(genre.length() == 0){
                        genre = "unknown genre"; // if an empty genre has been read, we put a default value
                    }
                    logInfo += " genre:" +stringTagValue;
                }
                if(stringTagId.compareTo("TXXX") == 0 && stringTagValue.contains("OMG_TRACK")){
                    stringTagValue = stringTagValue.replace("OMG_TRACK","");
                    stringTagValue = stringTagValue.replace(" ","");
                    trackNumber = Integer.parseInt(stringTagValue);
                    logInfo += " trackNumber:" +trackNumber;
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
                    logInfo += " year:" +year;
                }
                if(stringTagId.compareTo("TYER") == 0){
                    year = intTagValue;
                    logInfo += " year:" +year;
                }
                if(stringTagId.compareTo("TLEN") == 0){
                    length = intTagValue;
                    logInfo += " length:" +length;
                }
                omaFileStream.read(tagId); //Reads the next tagId
                
                // Before next iteration, check if we are in an infinite loop
                infiniteLoop++;
                if(infiniteLoop > 1000) {
                    // an infinite loop has been detected, log info and stop the loop
                    logger.info(logInfo);
                    logger.severe("An infinite loop has been detected and stop while reading EA3 tag from file " + omaFile.getName() + ".\nPlease contact the developers if you are reading this error.");
                    return;
                }
            }
            // Log info:
            logger.info(logInfo);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Ea3Tag(Mp3 title) {
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
    private static int bitRateConversion(int bitRateHuman, byte layer, byte mpegVersion) {
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
        bitRateTable.put(144, 12);
        bitRateTable.put(160, 13);
        bitRateTable.put(176, 14);
        bitRateTable.put(192, 15);
        bitRateTable.put(224, 16);
        bitRateTable.put(256, 17);
        bitRateTable.put(288, 18);
        bitRateTable.put(320, 19);
        bitRateTable.put(352, 20);
        bitRateTable.put(384, 21);
        bitRateTable.put(416, 22);
        bitRateTable.put(448, 23);
        // And the table of correspondance,
        int bitRateCorrespondance[][][] = { // [mpeg][layer][bitrate]
            {// mpeg2
                {// mpeg2 - layer3
                    1,// 8kpbs
                    2,// 16kpbs
                    3,// 24kpbs
                    4,// 32kpbs
                    5,// 40kpbs
                    6,// 48kpbs
                    7,// 56kpbs
                    8,// 64kpbs
                    9,// 80kpbs
                    10,// 96kpbs
                    11,// 112kpbs
                    12,// 128kpbs
                    13,// 144kpbs
                    14,// 160kpbs
                    0,// 176kpbs INVALID for this mpeg version and layer
                    0,// 192kpbs INVALID for this mpeg version and layer
                    0,// 224kpbs INVALID for this mpeg version and layer
                    0,// 256kpbs INVALID for this mpeg version and layer
                    0,// 288kpbs INVALID for this mpeg version and layer
                    0,// 320kpbs INVALID for this mpeg version and layer
                    0,// 352kpbs INVALID for this mpeg version and layer
                    0,// 384kpbs INVALID for this mpeg version and layer
                    0,// 416kpbs INVALID for this mpeg version and layer
                    0// 448kpbs INVALID for this mpeg version and layer
                },
                {// mpeg2 - layer2
                    1,// 8kpbs
                    2,// 16kpbs
                    3,// 24kpbs
                    4,// 32kpbs
                    5,// 40kpbs
                    6,// 48kpbs
                    7,// 56kpbs
                    8,// 64kpbs
                    9,// 80kpbs
                    10,// 96kpbs
                    11,// 112kpbs
                    12,// 128kpbs
                    13,// 144kpbs
                    14,// 160kpbs
                    0,// 176kpbs INVALID for this mpeg version and layer
                    0,// 192kpbs INVALID for this mpeg version and layer
                    0,// 224kpbs INVALID for this mpeg version and layer
                    0,// 256kpbs INVALID for this mpeg version and layer
                    0,// 288kpbs INVALID for this mpeg version and layer
                    0,// 320kpbs INVALID for this mpeg version and layer
                    0,// 352kpbs INVALID for this mpeg version and layer
                    0,// 384kpbs INVALID for this mpeg version and layer
                    0,// 416kpbs INVALID for this mpeg version and layer
                    0// 448kpbs INVALID for this mpeg version and layer                 
                },
                {// mpeg2 - layer1
                    0,// 8kpbs INVALID for this mpeg version and layer
                    0,// 16kpbs INVALID for this mpeg version and layer
                    0,// 24kpbs INVALID for this mpeg version and layer
                    1,// 32kpbs
                    0,// 40kpbs INVALID for this mpeg version and layer
                    2,// 48kpbs
                    3,// 56kpbs
                    4,// 64kpbs
                    5,// 80kpbs
                    6,// 96kpbs
                    7,// 112kpbs
                    8,// 128kpbs
                    9,// 144kpbs
                    10,// 160kpbs
                    11,// 176kpbs
                    12,// 192kpbs
                    13,// 224kpbs
                    14,// 256kpbs
                    0,// 288kpbs INVALID for this mpeg version and layer
                    0,// 320kpbs INVALID for this mpeg version and layer
                    0,// 352kpbs INVALID for this mpeg version and layer
                    0,// 384kpbs INVALID for this mpeg version and layer
                    0,// 416kpbs INVALID for this mpeg version and layer
                    0// 448kpbs INVALID for this mpeg version and layer
                }
            },
            {// mpeg1
                {// mpeg1 - layer3
                    0,// 8kpbs INVALID for this mpeg version and layer
                    0,// 16kpbs INVALID for this mpeg version and layer
                    0,// 24kpbs INVALID for this mpeg version and layer
                    1,// 32kpbs
                    2,// 40kpbs
                    3,// 48kpbs
                    4,// 56kpbs
                    5,// 64kpbs
                    6,// 80kpbs
                    7,// 96kpbs
                    8,// 112kpbs
                    9,// 128kpbs
                    0,// 144kpbs INVALID for this mpeg version and layer
                    10,// 160kpbs
                    0,// 176kpbs INVALID for this mpeg version and layer
                    11,// 192kpbs
                    12,// 224kpbs
                    13,// 256kpbs
                    0,// 288kpbs INVALID for this mpeg version and layer
                    14,// 320kpbs
                    0,// 352kpbs INVALID for this mpeg version and layer
                    0,// 384kpbs INVALID for this mpeg version and layer
                    0,// 416kpbs INVALID for this mpeg version and layer
                    0// 448kpbs INVALID for this mpeg version and layer
                },
                {// mpeg1 - layer2
                    0,// 8kpbs INVALID for this mpeg version and layer
                    0,// 16kpbs INVALID for this mpeg version and layer
                    0,// 24kpbs INVALID for this mpeg version and layer
                    1,// 32kpbs
                    0,// 40kpbs INVALID for this mpeg version and layer
                    2,// 48kpbs
                    3,// 56kpbs
                    4,// 64kpbs
                    5,// 80kpbs
                    6,// 96kpbs
                    7,// 112kpbs
                    8,// 128kpbs
                    0,// 144kpbs INVALID for this mpeg version and layer
                    9,// 160kpbs
                    0,// 176kpbs INVALID for this mpeg version and layer
                    10,// 192kpbs
                    11,// 224kpbs
                    12,// 256kpbs
                    0,// 288kpbs INVALID for this mpeg version and layer
                    13,// 320kpbs
                    0,// 352kpbs INVALID for this mpeg version and layer
                    14,// 384kpbs
                    0,// 416kpbs INVALID for this mpeg version and layer
                    0// 448kpbs INVALID for this mpeg version and layer
                },
                {// mpeg1 - layer1
                    0,// 8kpbs INVALID for this mpeg version and layer
                    0,// 16kpbs INVALID for this mpeg version and layer
                    0,// 24kpbs INVALID for this mpeg version and layer
                    1,// 32kpbs
                    0,// 40kpbs INVALID for this mpeg version and layer
                    0,// 48kpbs INVALID for this mpeg version and layer
                    0,// 56kpbs INVALID for this mpeg version and layer
                    2,// 64kpbs
                    0,// 80kpbs INVALID for this mpeg version and layer
                    3,// 96kpbs
                    0,// 112kpbs INVALID for this mpeg version and layer
                    4,// 128kpbs
                    0,// 144kpbs INVALID for this mpeg version and layer
                    5,// 160kpbs
                    0,// 176kpbs INVALID for this mpeg version and layer
                    6,// 192kpbs
                    7,// 224kpbs
                    8,// 256kpbs
                    9,// 288kpbs
                    10,// 320kpbs
                    11,// 352kpbs
                    12,// 384kpbs
                    13,// 416kpbs
                    14// 448kpbs
                }
            }};
        
        // Then, we get the index of the bit rate from the first table
        Integer index = (Integer)bitRateTable.get(bitRateHuman);
        
        // And, from the index, the layer and the mpegVersion, we can look into the second table the int we want
        //mpegVersion is 3 for version 1 and 2 for version 2, so we substract 2 to this value to have 1 for version 1 or 0 for version 2
        //layer is 3 for version 1, 2 for version 2 and 1 for version 3 so we substract 1 to this value to have 2 for version 1 or 1 for version 2 or 0 for version 3
        return bitRateCorrespondance[mpegVersion -2][layer - 1][index]; 
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
    * Obtains the frequency.
    *
    * @return The frequency in kHz.
    */
    public double getFrequency() {
        return frequency;
    }

    /**
    * Obtains the bitRate.
    *
    * @return The bitRate in kbps.
    */
    private int getBitRate() {
        return bitRate;
    }

    /**
    * Obtains the layer.
    *
    * @return The layer.
    */    private byte getLayer() {
        return layer;
    }

    /**
    * Obtains the MpegVersion.
    *
    * @return The MpegVersion .
    */
    private byte getMpegVersion() {
        return mpegVersion;
    }

    /**
    * Tell if the track is VBR or not.
    *
    * @return True if VBR, false if CBR.
    */
    private boolean getVbr() {
        return vbr;
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
    private static int bytes2int(byte[] bytes){
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


    public static void write(FileOutputStream out, Ea3Tag destinationTag, boolean gotKey, int titleWrappedFormat) throws Exception {
        int headerlength = 0; // count the amount of bytes writen. Since the length of the tag should be determined, this variable will be used to fill with zero the space left
        byte[] encodageCode = {0,0,2}; // used to announce the encodage of the info writen in the tag
        byte partOneSizeA; // First of the two bytes describing the size of the first part of the EA3 tag
        byte partOneSizeB; // Second of the two bytes describing the size of the first part of the EA3 tag

        // Store tag info
        String titleNameS = destinationTag.getTitleName();
        String artistNameS = destinationTag.getArtistName();
        String albumNameS = destinationTag.getAlbumName();
        String genreS = destinationTag.getGenre();
        int trackNumberS = destinationTag.getTrackNumber();
        int yearS = destinationTag.getYear();
        int lengthS = destinationTag.getLength();
        int bitRateS= destinationTag.getBitRate();
        boolean vbrS = destinationTag.getVbr();
        double frequencyS = destinationTag.getFrequency();
        byte mpegVersionS = destinationTag.getMpegVersion();
        byte layerS = destinationTag.getLayer();


        //// Add first part of the tag (the one called "ea3" with title info (title name, album name...))
        //Header: tag label (ea3) and size
        byte[] bytes4 = {(byte)0x65,(byte)0x61,(byte)0x33,(byte)0x03}; // "e","a","3",0x3
        out.write(bytes4); headerlength += 4; // Write label

        // Compute the size of the first part
        // If no cover is writen, SonicStage always uses a given lenght: 0xC00 bytes counted as follows: 0x17*0x80 + 0x80. Here, one byte is enough to code the lenght (0x17) but 5 bytes are reserved for that, see what follows.
        // When a cover is writen, if the cover is large (around 0x8000), the length of the tag may be coded over 2 bytes, as follows 0x0A*0x4000 + 0x0F*0x80 + 0x80 (the two bytes here are 0A 0F)
//NOT IMPLEMENTED YET        if(!COVER){
        partOneSizeA = (byte)0x0;
        partOneSizeB = (byte)0x17;
//NOT IMPLEMENTED YET        }
//NOT IMPLEMENTED YET        else { // Compute and write the size of the first part of the tag
//NOT IMPLEMENTED YET            partOneSizeA = 0x0;
//NOT IMPLEMENTED YET            partOneSizeB = 0x0;
//NOT IMPLEMENTED YET        }
        byte[] bytes6 = {0,0,0,partOneSizeA,partOneSizeB,(byte)0x76}; // The size of the first part of the tag is "0x17", "0X76" is an unknown constant
        out.write(bytes6); headerlength += 6;

        //Title name
        String label = "TIT2";
        out.write(label.getBytes()); headerlength += 4; // Write the label to announce the upcoming info
        if(titleNameS.length() > 60) { // We can't store in EA3 tag name containing more than 60 charactere
            titleNameS = (String)titleNameS.subSequence(0,59);
        }
        out.write(OmaDataBaseToolBox.int2bytes(titleNameS.length()*2 + 1, 4)); headerlength += 4; // Write the length to announce the size of the upcoming info
        out.write(encodageCode); headerlength += 3; // Write the encodage code for the size of the upcoming info
        OmaDataBaseToolBox.WriteString16(out, titleNameS); headerlength += titleNameS.length()*2; // Write the info
        
        //Artist name
        label = "TPE1";
        out.write(label.getBytes()); headerlength += 4; // Write the label to announce the upcoming info
        if(artistNameS.length() > 60) { // We can't store in EA3 tag name containing more than 60 charactere
            artistNameS = (String)artistNameS.subSequence(0,59);
        }
        out.write(OmaDataBaseToolBox.int2bytes(artistNameS.length()*2 + 1, 4)); headerlength += 4; // Write the length to announce the size of the upcoming info
        out.write(encodageCode); headerlength += 3; // Write the encodage code for the size of the upcoming info
        OmaDataBaseToolBox.WriteString16(out, artistNameS); headerlength += artistNameS.length()*2; // Write the info

        //Album name
        label = "TALB";
        out.write(label.getBytes()); headerlength += 4; // Write the label to announce the upcoming info
        if(albumNameS.length() > 60) { // We can't store in EA3 tag name containing more than 60 charactere
            albumNameS = (String)albumNameS.subSequence(0,59);
        }
        out.write(OmaDataBaseToolBox.int2bytes(albumNameS.length()*2 + 1, 4)); headerlength += 4; // Write the length to announce the size of the upcoming info
        out.write(encodageCode); headerlength += 3; // Write the encodage code for the size of the upcoming info
        OmaDataBaseToolBox.WriteString16(out, albumNameS); headerlength += albumNameS.length()*2; // Write the info

        //Genre
        label = "TCON";
        out.write(label.getBytes()); headerlength += 4; // Write the label to announce the upcoming info
        out.write(OmaDataBaseToolBox.int2bytes(genreS.length()*2 + 1, 4)); headerlength += 4; // Write the length to announce the size of the upcoming info
        out.write(encodageCode); headerlength += 3; // Write the encodage code for the size of the upcoming info
        OmaDataBaseToolBox.WriteString16(out, genreS); headerlength += genreS.length()*2; // Write the info

        //Track number
        label = "TXXX";
        out.write(label.getBytes()); headerlength += 4; // Write the label to announce the upcoming info
        trackNumberS = trackNumberS % 100; // Track number is between 1 and 99
        if(trackNumberS < 10) { // need to know the number of digit
            out.write(OmaDataBaseToolBox.int2bytes(23, 4)); headerlength += 4; // Write the length to announce the size of the upcoming info
        }
        else {
            out.write(OmaDataBaseToolBox.int2bytes(25, 4)); headerlength += 4; // Write the length to announce the size of the upcoming info
        }
        out.write(encodageCode); headerlength += 3; // Write the encodage code for the size of the upcoming info
        OmaDataBaseToolBox.WriteString16(out, "OMG_TRACK"); headerlength += 8*2; // Write a OMG text to wrapp track number
        OmaDataBaseToolBox.WriteZeros(out, 2); headerlength += 2; // Two zeros between the text and the actual number
        OmaDataBaseToolBox.WriteString16(out, Integer.toString(trackNumberS)); if(trackNumberS < 10) {headerlength += 2*2;}else{headerlength += 3*2;} // Write the info

        //Year
        label = "TYER";
        out.write(label.getBytes()); headerlength += 4; // Write the label to announce the upcoming info
        out.write(OmaDataBaseToolBox.int2bytes(4*2 + 1, 4)); headerlength += 4; // Write the length to announce the size of the upcoming info
        out.write(encodageCode); headerlength += 3; // Write the encodage code for the size of the upcoming info
        yearS = yearS%10000; // year should be less than 10000
        if(yearS < 1000) { // Check date validity
            OmaDataBaseToolBox.WriteString16(out, "0000"); headerlength += 4*2; // Write the info
        }
        else {
            OmaDataBaseToolBox.WriteString16(out, Integer.toString(yearS)); headerlength += 4*2; // Write the info
        }

        //Year again (there are several way to put the year... This is maybe the date where the title was transfered...)
        label = "TXXX";
        out.write(label.getBytes()); headerlength += 4; // Write the label to announce the upcoming info
        out.write(OmaDataBaseToolBox.int2bytes(59, 4)); headerlength += 4; // Write the length to announce the size of the upcoming info
        out.write(encodageCode); headerlength += 3; // Write the encodage code for the size of the upcoming info
        OmaDataBaseToolBox.WriteString16(out, "OMG_TRLDA "); headerlength += 10*2; // Write a OMG text to wrapp year
        if(yearS < 1000) {
            OmaDataBaseToolBox.WriteString16(out, "0000"); headerlength += 4*2; // Write the info
        }
        else {
            OmaDataBaseToolBox.WriteString16(out, Integer.toString(yearS)); headerlength += 4*2; // Write the info
        }
        OmaDataBaseToolBox.WriteString16(out, "/01/01 00:00:00"); headerlength += 15*2; // Write the whole date and time...

        // Track length (only writen if valid)
        if(lengthS > 0) {
            label = "TLEN";
            out.write(label.getBytes()); headerlength += 4; // Write the label to announce the upcoming info
            String lengthString = Long.toString(lengthS); // Get the length in a string
            out.write(OmaDataBaseToolBox.int2bytes(lengthString.length()*2 + 1, 4)); headerlength += 4; // Write the length to announce the size of the upcoming info
            out.write(encodageCode); headerlength += 3; // Write the encodage code for the size of the upcoming info
            OmaDataBaseToolBox.WriteString16(out, lengthString); headerlength += lengthString.length()*2; // Write the info
        }

        //Fill in with zeros the first part of the tag (ea3) to reach the length we had writen at the beginning of the tag
        OmaDataBaseToolBox.WriteZeros(out, partOneSizeA*0x4000 + partOneSizeB*0x80 + 0x80 - headerlength);


        //// Add second part of the tag (the one called "EA3" with title properties)
        // 1st line (a line is 0x10 bytes)
        label = "EA3";
        out.write(label.getBytes()); // Write label
        // Next depends on the generation, if protected or not
        byte[] bytes12;
        if(gotKey) { // if the files are encrypted, the EA3 tag shouldn't contain the same constant... ...
            bytes12 = new byte[]{(byte)0x02,0,(byte)0x60,(byte)0xFF,(byte)0xFE,0,0,0,0,1,0xF,0x50,0};
        }
        else {
            bytes12 = new byte[]{(byte)0x02,0,(byte)0x60,(byte)0xFF,(byte)0xFF,0,0,0,0,1,0xF,0x50,0};
        }
        out.write(bytes12);


        // 2nd line (unknown constants...)
        byte[] bytes16 = new byte[]{0,4,0,0,0,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0xC8,(byte)0xD8,(byte)0x36,(byte)0xD8,(byte)0x11,(byte)0x22,(byte)0x33,(byte)0x44};
        out.write(bytes16);


        // 3rd line contains properties of the file
        // First, the format
        byte[] bytes1;
        switch(titleWrappedFormat){
            case Title.OMA:
                bytes1 = new byte[]{0x1};
                break;
            case Title.MP3:
                bytes1 = new byte[]{0x3};
                break;
            case Title.WMA:
                bytes1 = new byte[]{0x5};
                break;
            default:
                logger.severe("Unsupported format to wrap in an OMA file !!");
                return ;
        }
        out.write(bytes1);

        // Then if the title is VBR
        //TODO OK for MP3, but for others !!??
        if(vbrS) {
            out.write(OmaDataBaseToolBox.int2bytes(144, 1)); // 144 = 0x90 = vbr
        }
        else {
            out.write(OmaDataBaseToolBox.int2bytes(128, 1)); // 128 = 0x80 = cbr
        }

        // Then the bitrate, layer, mpeg version
        //TODO OK for MP3, but for others !!??
        out.write(OmaDataBaseToolBox.int2bytes(bitRateConversion(bitRateS, layerS, mpegVersionS) + layerS*16 + mpegVersionS*64, 1)); // mpeg version(2bits), layer version(2bits), bitrate(4bits)
        out.write(OmaDataBaseToolBox.int2bytes(16, 1)); // 16 = 0x10

        // Then the track length
        out.write(OmaDataBaseToolBox.int2bytes(lengthS, 4));

        // And finally the number of frames
        // To have if need to make some computation
            // CODE FROM ML_SONY
            //int  SAMPLING_RATES[] = {11025, 12000, 8000, 0, 0, 0, 22050, 24000, 16000, 44100, 48000, 32000};
            int  SAMPLE_PER_FRAME[] = {0,576,1152,384,0,0,0,0,0,576,1152,384,0,1152,1152,384};

            //sample per frame 0=reserved
            //          MPG2.5 res        MPG2   MPG1
            //reserved  0      0          0      0
            //Layer III 576    0          576    1152
            //Layer II  1152   0          1152   1152
            //Layer I   384    0          384    384

            //int samplingRate = SAMPLING_RATES[(mpegVersion * 3) + samplingRateIndex];
        double samplingRate = frequencyS;
        int samplePerFrame = SAMPLE_PER_FRAME[(mpegVersionS * 4) + layerS]; // Compute the sample per frame
        // Compute the number of frames
        int nbFrames = (int)((lengthS * samplingRate*1000) / samplePerFrame); // frequency is in kHz, multiple by 1000 to have it in Hz
/*System.out.println("frequency: "+samplingRate);
System.out.println("mpegVersion: "+mpegVersionS);
System.out.println("layer: "+layerS);
System.out.println("lengthS: "+lengthS);
System.out.println("samplePerFrame: "+samplePerFrame);
System.out.println("nbFrames: "+nbFrames);*/
        // and write it
        out.write(OmaDataBaseToolBox.int2bytes(nbFrames, 4));

        // 4 zeros to finish the 3rd line
        OmaDataBaseToolBox.WriteZeros(out, 4);

        //Fill in the second part of the tag with zeros
        OmaDataBaseToolBox.WriteZeros(out, 3*0x10);
    }
/* ARCHIVE from write method
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

    /**
     * Return the format of the file wrapped into the OMA file. Could be MP3, OMA,...
     *
     * @return the format as a code, defined into the Title class. "Title.MP3" stands for MP3,... (return -1 in case of problem) and return Title.AUDIO when format is unknown.
     */
    public static int getFormat(File source) {
        java.io.InputStream omaFileStream; // The stream used to open the file
        byte[] buffer = new byte[1]; //Buffer (1 byte long)

        // First, get the size of the first part of the tag
        long ea3TagSize = getEa3TagLenght(source);

        // Open file to read it
        try {
            omaFileStream = source.toURI().toURL().openStream();
        } catch (Exception ex) {
            Logger.getLogger(Ea3Tag.class.getName()).log(Level.SEVERE, "Error while opening file" , ex);
            ex.printStackTrace();
            return -1;
        }

        try {
            // Move to the second part of the tag, just before the format code
            long bytesToskip = ea3TagSize + 32; // amont of bytes to be skipped: the first part of the tag + 8*4 bytes
            long bytesSkipped = 0; // count the amount of bytes really skipped
            // call skip method until the correct number of bytes have been skipped
            while((bytesSkipped = omaFileStream.skip(bytesToskip)) > 0) { // This turns out to be necessary since the skip method is not able to skip the correct amount of bytes once when tag holds a cover
                bytesToskip = bytesToskip - bytesSkipped; // substract bytes correctly skipped
            }

            // Read the format of the file wrapped into this OMA
            omaFileStream.read(buffer);
            int formatCode = bytes2int(buffer);

            // Compare the formatCode to the code known and return the correponding format
            switch(formatCode){
                case 0x1:
                    return Title.OMA;
                case 0x3:
                    return Title.MP3;
                case 0x5:
                    return Title.WMA;
/*TODO                case 3:
                    return Title.AAC;
                case 4:
                    return Title.WAV;*/
                default:
                    return Title.AUDIO;
            }
        } catch (Exception ex) {
            Logger.getLogger(Ea3Tag.class.getName()).log(Level.SEVERE, "Error while reading file", ex);
            ex.printStackTrace();
            return -1;
        } finally {
            try {
                // Always close the stream
                omaFileStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Ea3Tag.class.getName()).log(Level.SEVERE, "Error while closing file", ex);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Returns the size ot the first part of the EA3 tag (the "ea3" part, the second part is labeled "EA3")
     *
     * @param source OMA file to search.
     * @return The size ot the first part of the EA3 tag.
     */
    public static long getEa3TagLenght(File source) {
        java.io.InputStream omaFileStream; // The stream used to open the file
        byte[] buffer1 = new byte[1]; //Buffer (1 byte long)
        byte[] buffer4 = new byte[4]; //Buffer (4 bytes long)
        int sizePart1;
        int sizePart2;

        // Open file to read it
        try {
            omaFileStream = source.toURI().toURL().openStream();
        } catch (Exception ex) {
            Logger.getLogger(Ea3Tag.class.getName()).log(Level.SEVERE, "Error while opening file" , ex);
            ex.printStackTrace();
            return 0;
        }

        // First, search the size of the first part of the tag
        try {
            omaFileStream.skip(4); //Skips 3 bytes (which are "e", "a", "3", 0x03)
            omaFileStream.read(buffer4); //Read the first part of the size
            sizePart1 = bytes2int(buffer4); // Convert it to int
            omaFileStream.read(buffer1); //Reads the second part of the size
            sizePart2 = buffer1[0]; // Convert it to int
            if(sizePart2 < 0) {sizePart2+= 256;} // Convert it to a positive number !
        } catch (Exception ex) {
            Logger.getLogger(Ea3Tag.class.getName()).log(Level.SEVERE, "Error while reading file", ex);
            ex.printStackTrace();
            return 0;
        } finally {
            try {
                // Always close the stream
                omaFileStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Ea3Tag.class.getName()).log(Level.SEVERE, "Error while closing file", ex);
                ex.printStackTrace();
            }
        }

        // Compute and return the size
        return 0x4000*sizePart1 + 0x80*sizePart2 + 0x80;
    }

    /**
     * Returns the byte position of the first music Frame that the <code>file</code> argument refers to. This is the
     * first byte of music data and not the EA3 Tag Frame.
     *
     * @param source OMA file to search.
     * @return The byte position of the first music Frame.
     */
    public static long getMusicStartByte(File source) {
        java.io.InputStream omaFileStream; // The stream used to open the file
        byte[] buffer = new byte[1]; //Buffer (1 byte long)

        // First, get the size of the first part of the tag
        long ea3TagSize = getEa3TagLenght(source);

        // Open file to read it
        try {
            omaFileStream = source.toURI().toURL().openStream();
        } catch (Exception ex) {
            Logger.getLogger(Ea3Tag.class.getName()).log(Level.SEVERE, "Error while opening file" , ex);
            ex.printStackTrace();
            return 0;
        }

        try {
            // Move to the second part of the tag
            long bytesToskip = ea3TagSize; // amont of bytes to be skipped
            long bytesSkipped = 0; // count the amount of bytes really skipped
            // call skip method until the correct number of bytes have been skipped
            while((bytesSkipped = omaFileStream.skip(bytesToskip)) > 0) { // This turns out to be necessary since the skip method is not able to skip the correct amount of bytes once when tag holds a cover
                bytesToskip = bytesToskip -bytesSkipped; // substract bytes correctly skipped
            }

            // Skip the header of the second part of the tag ("E","A","3")
            omaFileStream.skip(3);

            // Read the type of the second part
            omaFileStream.read(buffer);
            int type = buffer[0]; // Convert it to int
            if(type < 0) {type+= 256;} // Convert it to a positive number !

            if(type == 1){
                // First type of second part of the tag length is 0x4e0
                return ea3TagSize+0x4e0;
            }
            else {
                // Second type of second part of the tag length is 0x60
                return ea3TagSize+0x60;
            }
        } catch (Exception ex) {
            Logger.getLogger(Ea3Tag.class.getName()).log(Level.SEVERE, "Error while reading file", ex);
            ex.printStackTrace();
            return 0;
        } finally {
            try {
                // Always close the stream
                omaFileStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Ea3Tag.class.getName()).log(Level.SEVERE, "Error while closing file", ex);
                ex.printStackTrace();
            }
        }
    }
}