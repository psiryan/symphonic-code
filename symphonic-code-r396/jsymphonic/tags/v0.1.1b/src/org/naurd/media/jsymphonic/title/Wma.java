/*
 * Wma.java
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
public class Wma  extends Title {
    
    /** Creates a new instance of Wma */
    public Wma(java.io.File f) {
        super(f);
        extention=".wma";
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
    public Wma(java.net.URL url){
        super(url);
    }
    
}
