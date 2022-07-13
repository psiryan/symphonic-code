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
        bitRate = 128; //How get it ? 
//TODO
        
        format = Title.OMA;
        fileSize = omaFile.length();
        
        //Read EA3tag
        tag = new Ea3Tag(omaFile);
        
        
        //length = tag.getLength();
        
        //Empty values are not checked here
    }
    
    /**
     * Use to create an empty title
     */
    public Oma() {
    }
}
