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
                it = mp3.getID3v2Tag().getFrame("TIT2").getBody().getObjectListIterator();
                encoding = ((ObjectNumberHashMap)it.next()).toString();
                titleName = new String((((ObjectStringSizeTerminated)it.next()).toString()).getBytes(encoding));
                // Album name
                it = mp3.getID3v2Tag().getFrame("TALB").getBody().getObjectListIterator();
                encoding = ((ObjectNumberHashMap)it.next()).toString();
                albumName = new String((((ObjectStringSizeTerminated)it.next()).toString()).getBytes(encoding));
                // Artist name
                it = mp3.getID3v2Tag().getFrame("TPE1").getBody().getObjectListIterator();
                encoding = ((ObjectNumberHashMap)it.next()).toString();
                artistName = new String((((ObjectStringSizeTerminated)it.next()).toString()).getBytes(encoding));
                // Genre
                it = mp3.getID3v2Tag().getFrame("TCON").getBody().getObjectListIterator();
                encoding = ((ObjectNumberHashMap)it.next()).toString();
                genre = new String((((ObjectStringSizeTerminated)it.next()).toString()).getBytes(encoding));
                
                // Track number
                it = mp3.getID3v2Tag().getFrame("TRCK").getBody().getObjectListIterator();
                encoding = ((ObjectNumberHashMap)it.next()).toString();
                String titleNumberString = new String((((ObjectStringSizeTerminated)it.next()).toString()).getBytes(encoding)).replaceAll("(.*)/.*","$1");
                titleNumber = Integer.parseInt(titleNumberString);
                
                // Year
                String yearFrame = null;
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
            else if (mp3.hasID3v1Tag()){
                titleName = mp3.getID3v1Tag().getTitle();
                albumName = mp3.getID3v1Tag().getAlbum();
                artistName = mp3.getID3v1Tag().getArtist();
                genre = org.farng.mp3.TagConstant.genreIdToString.get(mp3.getID3v1Tag().getGenre()).toString();
                ID3v1_1 tagId3v1_1 = new ID3v1_1(new RandomAccessFile(f, "ro"));
                titleNumber = tagId3v1_1.getTrack();
                year = Integer.parseInt(mp3.getID3v1Tag().getYear());
            }
            
            bitRate = mp3.getBitRate();
            fileSize = f.length();
            frequency = mp3.getFrequency();
        
        } catch(Exception e){
            System.out.println("Exception loading " + f.getName());
            //e.printStackTrace();
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
