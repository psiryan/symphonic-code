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
        format = Title.WMA;
    }
        
    public Wma(java.net.URL url){
        super(url);
    }
    
}
