/*
 * Mp3.java
 *
 * Created on October 2, 2006, 10:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.title;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.logging.Logger;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagConstant;
import org.farng.mp3.id3.ID3v1;


import org.farng.mp3.id3.ID3v1_1;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringSizeTerminated;
import org.farng.mp3.id3.AbstractID3v2Frame;
import org.farng.mp3.id3.ID3v2_3Frame;

/**
 *
 * @author Pat
 */
public class Mp3 extends Title {
    protected byte mpegVersion = 3; // Mpeg version of the file
    protected byte layer = 1; // Layer of the file
    
    //Other
    private static Logger logger = Logger.getLogger("org.naurd.media.jsymphonic.title.Mp3");

    private String getID3FrameString(org.farng.mp3.MP3File mp3, String frameId) {
        if(!mp3.getID3v2Tag().hasFrame(frameId)) {
            return "";
        }
        
        AbstractID3v2Frame abstractID3v2Frame =
                (AbstractID3v2Frame)mp3.getID3v2Tag().getFrame(frameId);
        if (!(abstractID3v2Frame instanceof ID3v2_3Frame)) {
            return "";
        }
        
        ID3v2_3Frame frame = (ID3v2_3Frame)abstractID3v2Frame;
        
        Iterator it = mp3.getID3v2Tag().getFrame(frameId).getBody().getObjectListIterator();
        
        String encoding = ((ObjectNumberHashMap)it.next()).toString();
        
        return ((ObjectStringSizeTerminated)it.next()).toString();
    }

    public Mp3(String mp3Path){
        super();
        format = Title.MP3;
        sourceFile = new File(mp3Path);
    }
    public Mp3(File f){
        super(f);
        format = Title.MP3;
        
        try{
            org.farng.mp3.MP3File mp3 = new org.farng.mp3.MP3File(f);
            if (mp3.hasID3v2Tag()){
/*System.out.println("DEBUG ----------------------------------------------------- Mp3-50");
WORK TO FIND HOW TO HANDLE ALL ENCODINGS... you can delete it if you want (Nicolas)

Object temp = (ObjectStringSizeTerminated)it.next();
System.out.println("encoding:" +encoding);
System.out.println("essai1:" +temp);
//        static public final String charsetUTF_8 = "UTF-8";
String charsetUTF_8 = "UTF-8";
String charsetUTF_16BE = "UTF-16BE";
String charsetUTF_16LE = "UTF-16LE";
String charsetUTF_16 = "UTF-16";
String charsetISO_8859_1 = "ISO-8859-1";
String charsetEUC_KR = "EUC-KR";t) 

System.out.println("test   encoding:" + new String(temp.toString().getBytes(), encoding));
System.out.println("test      UTF_8:" + new String(temp.toString().getBytes(), charsetUTF_8));
System.out.println("test     UTF_16:" + new String(temp.toString().getBytes(), charsetUTF_16));
System.out.println("test   UTF_16BE:" + new String(temp.toString().getBytes(), charsetUTF_16BE));
System.out.println("test   UTF_16LE:" + new String(temp.toString().getBytes(), charsetUTF_16LE));
System.out.println("test ISO_8859_1:" + new String(temp.toString().getBytes(), charsetISO_8859_1));
System.out.println("test     EUC_KR:" + new String(temp.toString().getBytes(), charsetEUC_KR));

titleName = new String(temp.toString().getBytes(), encoding);*/
            
                bitRate = mp3.getBitRate();
                fileSize = f.length();
                frequency = mp3.getFrequency();
                vbr = mp3.isVariableBitRate();
                mpegVersion = mp3.getMpegVersion();
                layer = mp3.getLayer();
                
                String numericString;
                tag.setTitleName(getID3FrameString(mp3, "TIT2"));
                tag.setAlbumName(getID3FrameString(mp3, "TALB"));
                tag.setArtistName(getID3FrameString(mp3, "TPE1"));
                try {
                    // try to convert the number representing the genre to string
                    tag.setGenre(genreNumberToString(Integer.parseInt(getID3FrameString(mp3, "TCON"))));
                }
                catch(Exception e){
                    //If it fails, put default value
                    tag.setGenre("unknown genre");
                }
                    
                numericString = getID3FrameString(mp3, "TLEN");
                if (numericString.trim().length() != 0) {
                    length = Integer.parseInt(numericString);
                }
                else{
                    // Compute the length
                    if(vbr){
// TODO : get the number of frame !
/*                        long nbFrames = 0;
                        // first, compute the number of samples per frame
                        int  SAMPLE_PER_FRAME[] = {0,576,1152,384,0,0,0,0,0,576,1152,384,0,1152,1152,384};
                        int samplePerFrame = SAMPLE_PER_FRAME[(mpegVersion * 4) + layer];
                        // then the length
                        length = (int) ((nbFrames * samplePerFrame) / (frequency * 1000)); // frequency is in kHz, multiple by 1000 to have it in Hz */
length = (int)((fileSize / bitRate * 8)) ; // since we don't have the number of frame, use the CBR scheme to compute the length
                    }
                    else {
                        // For vbr file, it's simple to get the length from the size of the file and the bitrate
                        length = (int)((fileSize / bitRate * 8)) ; // length in milliseconds
                    }
                }
                
                numericString = getID3FrameString(mp3, "TRCK");
                String[] splitenString = numericString.split("/"); // Track can be on the form "01/15" or  just "01" for track number 1 over 15 track in the album
                numericString = splitenString[0]; // Only the first value is interesting, the track number
                if (numericString.trim().length() != 0) {
                    tag.setTrackNumber(Integer.parseInt(numericString));
                }
                
                // Year
                String yearFrame = "";
                if(mp3.getID3v2Tag().hasFrameOfType("TDRL")) { // First try to see if the year is defined for ID3v2.4 (Release time)
                    yearFrame = "TDRL";
                }
                else if(mp3.getID3v2Tag().hasFrameOfType("TDRC")) { // Or for ID3v2.4 (Recording time)
                    yearFrame = "TDRC";
                }
                else if(mp3.getID3v2Tag().hasFrameOfType("TYER")) { // Or for ID3v2.3
                    yearFrame = "TYER";
                }
                else if(mp3.getID3v2Tag().hasFrameOfType("TYE")) { // Or for ID3v2.2
                    yearFrame = "TYE";
                }
                if(yearFrame.length() != 0) { // If a year has been found, read it
                    numericString = getID3FrameString(mp3, yearFrame);
                    tag.setYear(Integer.parseInt(numericString.trim())); //make sure there are no spaces!
                }
            }
            if (mp3.hasID3v1Tag()){
                if (tag.getTitleName().trim().length()==0 || tag.getTitleName().compareTo("unknown title") == 0){
                    tag.setTitleName(mp3.getID3v1Tag().getTitle());
                }
                if (tag.getAlbumName().trim().length()==0 || tag.getAlbumName().compareTo("unknown album") == 0){
                    tag.setAlbumName(mp3.getID3v1Tag().getAlbum());
                }
                if (tag.getArtistName().trim().length()==0 || tag.getArtistName().compareTo("unknown artist") == 0){
                    tag.setArtistName(mp3.getID3v1Tag().getArtist());
                }
                if (tag.getGenre().trim().length()==0 || tag.getGenre().compareTo("unknown genre") == 0){
                    tag.setGenre(genreNumberToString(mp3.getID3v1Tag().getGenre())); // Convert the number into the corresponding string
                }
                if (tag.getTrackNumber() <= 0){
                    try {
                        ID3v1_1 tagId3v1_1 = new ID3v1_1(new RandomAccessFile(f, "r"));
                        tag.setTrackNumber(tagId3v1_1.getTrack());
                    }
                    catch(Exception e) {
                        logger.warning("No track number found for file:"+f.getPath());
                    }
                }
                if (tag.getYear() <= 0){
                    try {
                        tag.setYear(Integer.parseInt(mp3.getID3v1Tag().getYear()));
                    }
                    catch(Exception e) {
                        logger.warning("No year found for file:"+f.getPath());
                    }
                }
            }
        } catch(Exception e){
            logger.warning("Exception loading " + f.getName());
            e.printStackTrace();
        }
        if (tag.getAlbumName().trim().length()==0 || tag.getAlbumName().compareTo("unknown album") == 0){
            tag.setAlbumName(f.getParentFile().getName());
        }
        if (tag.getArtistName().trim().length()==0 || tag.getArtistName().compareTo("unknown artist") == 0){
            if (f.getParentFile().getParentFile()!=null){
                tag.setArtistName(f.getParentFile().getParentFile().getName());
            }
        }
        if (tag.getTitleName().trim().length()==0 || tag.getTitleName().compareTo("unknown title") == 0){
            String tmp = f.getName();
            tmp = tmp.replace(".mp3", "");
            tag.setTitleName(tmp);
        }
        if (tag.getTrackNumber() <= 0){
            String tmpStr = f.getName();
            Integer tmpInt = 0;
            tmpStr = (String)tmpStr.subSequence(0,2);
            
            try {
                 tmpInt = Integer.parseInt(tmpStr);
            }
            catch(Exception e) {
                tmpStr = (String)tmpStr.subSequence(0,1);
                try {
                    tmpInt = Integer.parseInt(tmpStr);
                }
                catch(Exception ex) {
                    // No track number found at the begining of the file name
                }
            }
            tag.setTrackNumber(tmpInt);
        }
    }
    
    
    private byte genreStringToByte(String genre) {
        // Correspondance from genre name to genre number can be found in genreStringToId Map in TagConstant class
        Long genreLong = (Long)TagConstant.genreStringToId.get(genre);
        
        return genreLong.byteValue();
    }
            
    private String genreNumberToString(int genreInt) {
        // Instance a long from the given int
        Long genreLong = new Long(genreInt);
        
        // Call the main method which use a Long
        return genreNumberToString(genreLong);
    }
    
    
    private String genreNumberToString(long genreLong) {
        // Correspondance from genre number to genre name can be found in genreIdToString Map in TagConstant class
        String genre = (String)TagConstant.genreIdToString.get(genreLong);
        
        // If genre has not been found, put the default value
        if(genre == null){
            genre = "unknown genre";
        }
        
        return genre;
        
        /* not usefull anymore 
        switch(genreInt) {
            case 0 : return "Blues" ;
            case 1 : return "Classic Rock" ;
            case 2 : return "Country" ;
            case 3 : return "Dance" ;
            case 4 : return "Disco" ;
            case 5 : return "Funk" ;
            case 6 : return "Grunge" ;
            case 7 : return "Hip-Hop" ;
            case 8 : return "Jazz" ;
            case 9 : return "Metal" ;
            case 10 : return "New Age" ;
            case 11 : return "Oldies" ;
            case 12 : return "Other" ;
            case 13 : return "Pop" ;
            case 14 : return "R&B" ;
            case 15 : return "Rap" ;
            case 16 : return "Reggae" ;
            case 17 : return "Rock" ;
            case 18 : return "Techno" ;
            case 19 : return "Industrial" ;
            case 20 : return "Alternative" ;
            case 21 : return "Ska" ;
            case 22 : return "Death Metal" ;
            case 23 : return "Pranks" ;
            case 24 : return "Soundtrack" ;
            case 25 : return "Euro-Techno" ;
            case 26 : return "Ambient" ;
            case 27 : return "Trip-Hop" ;
            case 28 : return "Vocal" ;
            case 29 : return "Jazz+Funk" ;
            case 30 : return "Fusion" ;
            case 31 : return "Trance" ;
            case 32 : return "Classical" ;
            case 33 : return "Instrumental" ;
            case 34 : return "Acid" ;
            case 35 : return "House" ;
            case 36 : return "Game" ;
            case 37 : return "Sound Clip" ;
            case 38 : return "Gospel" ;
            case 39 : return "Noise" ;
            case 40 : return "AlternRock" ;
            case 41 : return "Bass" ;
            case 42 : return "Soul" ;
            case 43 : return "Punk" ;
            case 44 : return "Space" ;
            case 45 : return "Meditative" ;
            case 46 : return "Instrumental Pop" ;
            case 47 : return "Instrumental Rock" ;
            case 48 : return "Ethnic" ;
            case 49 : return "Gothic" ;
            case 50 : return "Darkwave" ;
            case 51 : return "Techno-Industrial" ;
            case 52 : return "Electronic" ;
            case 53 : return "Pop-Folk" ;
            case 54 : return "Eurodance" ;
            case 55 : return "Dream" ;
            case 56 : return "Southern Rock" ;
            case 57 : return "Comedy" ;
            case 58 : return "Cult" ;
            case 59 : return "Gangsta" ;
            case 60 : return "Top 40" ;
            case 61 : return "Christian Rap" ;
            case 62 : return "Pop/Funk" ;
            case 63 : return "Jungle" ;
            case 64 : return "Native American" ;
            case 65 : return "Cabaret" ;
            case 66 : return "New Wave" ;
            case 67 : return "Psychadelic" ;
            case 68 : return "Rave" ;
            case 69 : return "Showtunes" ;
            case 70 : return "Trailer" ;
            case 71 : return "Lo-Fi" ;
            case 72 : return "Tribal" ;
            case 73 : return "Acid Punk" ;
            case 74 : return "Acid Jazz" ;
            case 75 : return "Polka" ;
            case 76 : return "Retro" ;
            case 77 : return "Musical" ;
            case 78 : return "Rock & Roll" ;
            case 79 : return "Hard Rock" ;
            case 80 : return "Folk" ;
            case 81 : return "Folk-Rock" ;
            case 82 : return "National Folk" ;
            case 83 : return "Swing" ;
            case 84 : return "Fast Fusion" ;
            case 85 : return "Bebob" ;
            case 86 : return "Latin" ;
            case 87 : return "Revival" ;
            case 88 : return "Celtic" ;
            case 89 : return "Bluegrass" ;
            case 90 : return "Avantgarde" ;
            case 91 : return "Gothic Rock" ;
            case 92 : return "Progressive Rock" ;
            case 93 : return "Psychedelic Rock" ;
            case 94 : return "Symphonic Rock" ;
            case 95 : return "Slow Rock" ;
            case 96 : return "Big Band" ;
            case 97 : return "Chorus" ;
            case 98 : return "Easy Listening" ;
            case 99 : return "Acoustic" ;
            case 100 : return "Humour" ;
            case 101 : return "Speech" ;
            case 102 : return "Chanson" ;
            case 103 : return "Opera" ;
            case 104 : return "Chamber Music" ;
            case 105 : return "Sonata" ;
            case 106 : return "Symphony" ;
            case 107 : return "Booty Bass" ;
            case 108 : return "Primus" ;
            case 109 : return "Porn Groove" ;
            case 110 : return "Satire" ;
            case 111 : return "Slow Jam" ;
            case 112 : return "Club" ;
            case 113 : return "Tango" ;
            case 114 : return "Samba" ;
            case 115 : return "Folklore" ;
            case 116 : return "Ballad" ;
            case 117 : return "Power Ballad" ;
            case 118 : return "Rhythmic Soul" ;
            case 119 : return "Freestyle" ;
            case 120 : return "Duet" ;
            case 121 : return "Punk Rock" ;
            case 122 : return "Drum Solo" ;
            case 123 : return "Acapella" ;
            case 124 : return "Euro-House" ;
            case 125 : return "Dance Hall" ;
            case 255 : return "None" ;
            default : return genre;
        }*/
    }
    
    public Mp3(java.net.URL url){
        super(url);
        format = super.MP3;
        
        if (tag.getAlbumName().trim().length()==0){
            tag.setAlbumName(url.getHost());
        }
        if (tag.getArtistName().trim().length()==0){
            tag.setArtistName(url.getHost());
        }
        if (tag.getTitleName().length()==0){
            tag.setTitleName(url.getFile());
        }
    }
    
    public byte getMpegVersion(){
        return mpegVersion;
    }
    public byte getLayer(){
        return layer;
    }
    
    /**
     *Write the ID3Tag in a Mp3 object from a Tag object.
     * 
     * @param newTag the tag to be written in the title.
     */
    @Override
    public void writeTag(Tag newTag) {
        try {
            // Instance a new mp3 to write the ID3tag
            MP3File mp3file = new MP3File(sourceFile);
            
            // Create a v1 tag
            ID3v1 tagV1 = new ID3v1();
            
            // Fill in the tag
            tagV1.setTitle(newTag.getTrackNumber() + "-" + newTag.getTitleName());
            tagV1.setArtist(newTag.getArtistName());
            tagV1.setAlbum(newTag.getAlbumName());
            tagV1.setYear(new Integer(newTag.getYear()).toString());
            try{
                // Try to find the genre ID corresponding to the genre name
                tagV1.setGenre(genreStringToByte(newTag.getGenre()));
            }
            catch(Exception e){
                // In case of problem, put default (255=none)
                tagV1.setGenre((byte)255);
            }
            
            // Attach the v1 tag
            mp3file.setID3v1Tag(tagV1);
            
            // Save modification to the tag
            mp3file.save();
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }

        // Save the tag for the Mp3 object in case of future use
        tag = newTag;
    }


}
