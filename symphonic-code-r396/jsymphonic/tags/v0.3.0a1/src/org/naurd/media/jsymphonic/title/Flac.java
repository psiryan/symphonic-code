/*
 * Flac.java
 *
 * Created on 21 mars 2008, 18:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.title;

/**
 *
 * @author skiron
 */
public class Flac extends Title{
    
    /** Creates a new instance of Flac */
    public Flac(java.net.URL url) {
        super(url);
    }
    
    public Flac(java.io.File f){
        super(f);
        format = Title.FLAC;
    }
    
}
