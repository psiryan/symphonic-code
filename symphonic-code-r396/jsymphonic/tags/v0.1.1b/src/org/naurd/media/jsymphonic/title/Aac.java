/*
 * Aac.java
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
public class Aac extends Title {
    
    /** Creates a new instance of Aac */
    public Aac(java.io.File f) {
        super(f);
        extention=".aac";
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
    public Aac(java.net.URL url){
        super(url);
    }
    
}
