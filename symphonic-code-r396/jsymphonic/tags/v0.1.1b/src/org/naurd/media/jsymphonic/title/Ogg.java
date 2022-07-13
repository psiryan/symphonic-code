/*
 * Ogg.java
 *
 * Created on October 15, 2006, 11:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.title;

/**
 *
 * @author Pat
 */
public class Ogg extends Title{
    
    /** Creates a new instance of Ogg */
    public Ogg(java.net.URL url) {
        super(url);
    }
    public Ogg(java.io.File f){
        super(f);
        extention=".ogg";
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
    
}
