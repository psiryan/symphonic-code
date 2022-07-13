/*
 * JSymphonicFileFilter.java
 *
 * Created on 29 septembre 2007, 00:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.manager;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author skiron
 */
public class JSymphonicFileFilter extends FileFilter{
    private String description;
    
    /**
     * Creates a new instance of JSymphonicFileFilter
     */
    public JSymphonicFileFilter() {
        description = ".mp3";
    }

    public boolean accept(File file) {
        String fileName = file.getName();
        if( fileName.startsWith(".")) {
            return false;
        }
        
        if( file.isDirectory() ) {
            return true;
        }
        
        if( fileName.endsWith(".mp3")) {
            return true;
        }
        
        if( fileName.endsWith(".MP3")) {
            return true;
        }
        
        return false;
    }

    public String getDescription() {
        return description;
    }
    
}
