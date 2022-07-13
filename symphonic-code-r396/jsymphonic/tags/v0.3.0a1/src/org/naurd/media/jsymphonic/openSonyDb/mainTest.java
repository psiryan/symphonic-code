/*
 * mainTest.java
 *
 * Created on 1 avril 2008, 12:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.openSonyDb;


/**
 *
 * @author neub
 */
public class mainTest {
    
    public static void main(String args[]) {
        loadPlaylist();
    }
    
    
    public static void loadPlaylist() {
// SETTINGS have changed in version 0.3.0a      Settings settings = new Settings();
// SETTINGS have changed in version 0.3.0a       String omgaudioPath = settings.getValue("OMGAUDIOpath","omgaudio");
        Playlist pl = new Playlist();
//comment to be able to compile        pl.read(omgaudioPath);
        
    }
    
    
}
