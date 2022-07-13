/*
 * SymphonicFileFilter.java
 *
 * Created on 29 septembre 2007, 00:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.toolBox;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author skiron
 */
public class JSymphonicIoFileFilter implements FileFilter{
    private String description;
    
    /** Creates a new instance of SymphonicFileFilter */
    public JSymphonicIoFileFilter() {
        description = "FileFilter use in Symphonic";
    }

    public boolean accept(File file) {
        // Use function defined in JSymphonicFileFilter
        return JSymphonicFileFilter.acceptFile(file);
    }   
}
