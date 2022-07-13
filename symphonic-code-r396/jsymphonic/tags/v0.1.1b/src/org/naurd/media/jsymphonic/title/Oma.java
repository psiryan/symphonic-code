/*
 * Oma.java
 *
 * Created on October 2, 2006, 10:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.title;
import java.io.*;


/**
 *
 * @author Pat
 */
public class Oma  extends Title{
    
    public Oma(java.io.File omaFile) {
        super(omaFile);
        
        //"sourceURL" default value (null) is OK
        //"Status" default value (TitleStatus.NONE) is OK
                
        //"frequency" //How get it ? (default value is 44100)
        //"nbChannels" //How get it ? (default value is 2)
        bitRate = 128; //How get it ? could be calculated from fileSize and lenght... but how to consider variable bitrate ?

        extention=".oma";
        fileSize = omaFile.length();
        
        //Read EA3tag
        Ea3Tag tag = new Ea3Tag(omaFile);
        
        albumName = tag.getAlbumName();
        artistName = tag.getArtistName();
        titleName = tag.getTitleName();
        genre = tag.getGenre();
        year = tag.getYear();
        titleNumber = tag.getTrackNumber();
        length = tag.getLength();
        
        //Empty values are not checked here
    }
}

