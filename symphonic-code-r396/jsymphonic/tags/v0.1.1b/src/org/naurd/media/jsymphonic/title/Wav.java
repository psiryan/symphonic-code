/*
 * Wav.java
 *
 * Created on October 2, 2006, 10:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.title;

/**
 *
 * @author Pat
 */
public class Wav extends Title {
    
    /** Creates a new instance of Wav */
    public Wav(java.io.File f) {
        super(f);
        extention=".wav";
        if (albumName.trim().length()==0){
            albumName=f.getParentFile().getName();
        }
        if (artistName.trim().length()==0){
            if (f.getParentFile().getParentFile()!=null){
                artistName = f.getParentFile().getParentFile().getName();
            }
            else{
                artistName="???";
            }
        }
        if (titleName.length()==0){
            titleName=f.getName();
        }

    }
    public Wav(java.net.URL url){
        super(url);
    }
    
}
