/*
 * Mp3.java
 *
 * Created on October 2, 2006, 10:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.title;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import org.farng.mp3.AbstractMP3FragmentBody;
import org.farng.mp3.MP3File;


import org.farng.mp3.id3.ID3v1_1;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringSizeTerminated;

/**
 *
 * @author Pat
 */
public class Mp3 extends Title {
    private String getID3FrameString(org.farng.mp3.MP3File mp3, String frameId) {
        if(!mp3.getID3v2Tag().hasFrame(frameId)) {
            return "";
        }
        
        Iterator it = mp3.getID3v2Tag().getFrame(frameId).getBody().getObjectListIterator();
        
        String encoding = ((ObjectNumberHashMap)it.next()).toString();
        
        // Because ObjectStringSizeTerminated stores string incorrectly, we use raw data to get actual string.
        byte[] bytesRawData = ((ObjectStringSizeTerminated)it.next()).getRawData();
        
        String frameString = "";
        try {
            // The first byte of the string data is encoding information.
            frameString = new String(bytesRawData, 1, bytesRawData.length -1, encoding);
        } catch(Exception e) {
            frameString = "";
        }
        
        return frameString;
    }

    public Mp3(java.io.File f){
        super(f);
        extention=".mp3";
        fileSize = f.length();
        
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
String charsetEUC_KR = "EUC-KR";

System.out.println("essai   encoding:" + new String(temp.toString().getBytes(), encoding));
System.out.println("essai      UTF_8:" + new String(temp.toString().getBytes(), charsetUTF_8));
System.out.println("essai     UTF_16:" + new String(temp.toString().getBytes(), charsetUTF_16));
System.out.println("essai   UTF_16BE:" + new String(temp.toString().getBytes(), charsetUTF_16BE));
System.out.println("essai   UTF_16LE:" + new String(temp.toString().getBytes(), charsetUTF_16LE));
System.out.println("essai ISO_8859_1:" + new String(temp.toString().getBytes(), charsetISO_8859_1));
System.out.println("essai5    EUC_KR:" + new String(temp.toString().getBytes(), charsetEUC_KR));

titleName = new String(temp.toString().getBytes(), encoding);

//Charset charset = Charset.forName("ISO-8859-1");
//CharsetDecoder decoder = charset.newDecoder();
//CharsetEncoder encoder = charset.newEncoder();
//
//Charset charsetUTF8 = Charset.forName("UTF8");
//CharsetDecoder decoderUTF8 = charsetUTF8.newDecoder();
////ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(temp));
//ByteBuffer bbuf = ByteBuffer.wrap(temp.getBytes());
//CharBuffer cbuf = decoderUTF8.decode(bbuf);
//System.out.println("essai3:" + cbuf);
//
//ByteBuffer testBB = charsetUTF8.encode(temp);
//CharBuffer testCB = decoderUTF8.decode(testBB);
//
//
//System.out.println("essai4:" +testCB);*/
                String numericString;
                titleName = getID3FrameString(mp3, "TIT2");
                albumName = getID3FrameString(mp3, "TALB");
                artistName = getID3FrameString(mp3, "TPE1");
                genre = genreNumberToString(getID3FrameString(mp3, "TCON"));
                
                numericString = getID3FrameString(mp3, "TLEN");
                if (numericString.trim().length() != 0) {
                    length = Integer.parseInt(numericString);
                }
                
                numericString = getID3FrameString(mp3, "TRCK");
                String[] splitenString = numericString.split("/"); // Track can be on the form "01/15" or  just "01" for track number 1 over 15 track in the album
                numericString = splitenString[0]; // Only the first value is interesting, the track number
                if (numericString.trim().length() != 0) {
                    titleNumber = Integer.parseInt(numericString);
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
                    year = Integer.parseInt(numericString);
                }
            }
            if (mp3.hasID3v1Tag()){
                if (titleName.trim().length()==0){
                    titleName = mp3.getID3v1Tag().getTitle();
                }
                if (albumName.trim().length()==0){
                    albumName = mp3.getID3v1Tag().getAlbum();
                }
                if (artistName.trim().length()==0){
                    artistName = mp3.getID3v1Tag().getArtist();
                }
                if (genre.trim().length()==0){
                    genre = genreNumberToString(genre); // Convert the number into the corresponding string
                }
                if (titleNumber <= 0){
                    ID3v1_1 tagId3v1_1 = new ID3v1_1(new RandomAccessFile(f, "r"));
                    titleNumber = tagId3v1_1.getTrack();
                }
                if (year <= 0){
                    year = Integer.parseInt(mp3.getID3v1Tag().getYear());
                }
            }
            
            bitRate = mp3.getBitRate();
            fileSize = f.length();
            frequency = (int)mp3.getFrequency();
            vbr = mp3.isVariableBitRate();
            mpegVersion = mp3.getMpegVersion();
            layer = mp3.getLayer();
        
        } catch(Exception e){
            System.out.println("Exception loading " + f.getName());
            e.printStackTrace();
        }
        if (albumName.trim().length()==0){
            albumName=f.getParentFile().getName();
        }
        if (artistName.trim().length()==0){
            if (f.getParentFile().getParentFile()!=null){
                artistName = f.getParentFile().getParentFile().getName();
            } else{
                artistName="no_artist";
            }
        }
        if (titleName.length()==0){
            String tmp = f.getName();
            tmp = tmp.replace(".mp3", "");
            titleName = tmp;
        }
        if (titleNumber == 0){
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
            titleNumber = tmpInt;
        }
        if (length == 0){
            length = 210000; // Avoid to have no length in the title, it's better to have a wrong value than zeor
        }
    }
    
    private String genreNumberToString(String genre) {
        Integer genreInt;
        try{
            genreInt = Integer.parseInt(genre);
        }
        catch(Exception e){
            return genre;
        }
        
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
        }
    }
    
    public Mp3(java.net.URL url){
        super(url);
        extention=".mp3";
        if (albumName.trim().length()==0){
            albumName=url.getHost();
        }
        if (artistName.trim().length()==0){
            artistName = url.getHost();
        }
        if (titleName.length()==0){
            titleName=url.getFile();
        }
    }
    
    /** Creates a new instance of Mp3 */
    
}
