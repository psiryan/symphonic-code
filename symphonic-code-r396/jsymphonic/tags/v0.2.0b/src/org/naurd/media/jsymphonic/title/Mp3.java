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
    
    public Mp3(java.io.File f){
        super(f);
        extention=".mp3";
        fileSize = f.length();
        
        try{
            org.farng.mp3.MP3File mp3 = new org.farng.mp3.MP3File(f);
            if (mp3.hasID3v2Tag()){
                Iterator it;
                String encoding;
                
                // Title name
                if(mp3.getID3v2Tag().hasFrame("TIT2")) {
                    it = mp3.getID3v2Tag().getFrame("TIT2").getBody().getObjectListIterator();
                    encoding = ((ObjectNumberHashMap)it.next()).toString();
                    titleName = new String((((ObjectStringSizeTerminated)it.next()).toString()).getBytes(encoding));
                }
                
                // Album name
                if(mp3.getID3v2Tag().hasFrame("TALB")) {
                    it = mp3.getID3v2Tag().getFrame("TALB").getBody().getObjectListIterator();
                    encoding = ((ObjectNumberHashMap)it.next()).toString();
                    albumName = new String((((ObjectStringSizeTerminated)it.next()).toString()).getBytes(encoding));
                }
                
                // Artist name
                if(mp3.getID3v2Tag().hasFrame("TPE1")) {
                    it = mp3.getID3v2Tag().getFrame("TPE1").getBody().getObjectListIterator();
                    encoding = ((ObjectNumberHashMap)it.next()).toString();
                    artistName = new String((((ObjectStringSizeTerminated)it.next()).toString()).getBytes(encoding));
                }
                
                // Genre
                if(mp3.getID3v2Tag().hasFrame("TCON")) {
                    it = mp3.getID3v2Tag().getFrame("TCON").getBody().getObjectListIterator();
                    encoding = ((ObjectNumberHashMap)it.next()).toString();
                    genre = new String((((ObjectStringSizeTerminated)it.next()).toString()).getBytes(encoding));
                    genre = genreNumberToString(genre); // Convert the number into the corresponding string
                }
                
                // Length
                if(mp3.getID3v2Tag().hasFrame("TLEN")) {
                    it = mp3.getID3v2Tag().getFrame("TLEN").getBody().getObjectListIterator();
                    encoding = ((ObjectNumberHashMap)it.next()).toString();
                    String lengthString = new String((((ObjectStringSizeTerminated)it.next()).toString()).getBytes(encoding)).replaceAll("(.*)/.*","$1");
                    length = Integer.parseInt(lengthString);
                }
                
                // Track number
                if(mp3.getID3v2Tag().hasFrame("TRCK")) {
                    it = mp3.getID3v2Tag().getFrame("TRCK").getBody().getObjectListIterator();
                    encoding = ((ObjectNumberHashMap)it.next()).toString();
                    String titleNumberString = new String((((ObjectStringSizeTerminated)it.next()).toString()).getBytes(encoding)).replaceAll("(.*)/.*","$1");
                    titleNumber = Integer.parseInt(titleNumberString);
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
                if(!yearFrame.isEmpty()) { // If a year has been found, read it
                    it = mp3.getID3v2Tag().getFrame(yearFrame).getBody().getObjectListIterator();
                    encoding = ((ObjectNumberHashMap)it.next()).toString();
                    String yearString = new String((((ObjectStringSizeTerminated)it.next()).toString()).getBytes(encoding));
                    year = Integer.parseInt(yearString);
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
