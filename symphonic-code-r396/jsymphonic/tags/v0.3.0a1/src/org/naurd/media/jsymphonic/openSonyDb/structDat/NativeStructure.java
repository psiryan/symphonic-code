/*
 * NativeStructure.java
 *
 * Created on 30 mars 2008, 18:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.openSonyDb.structDat;

import java.io.FileInputStream;
import org.naurd.media.jsymphonic.openSonyDb.Debug;

/**
 *
 * @author neub
 *  Abstract class that can not be instantiate
 *
 */
abstract public class NativeStructure {
    
    abstract public void read(FileInputStream f);
    abstract public void write(FileInputStream f);
    
    public static int toCharPosition(long hexaPos) {
        Debug.perr((hexaPos % 10 != 0),"Hexa pos is not a multiple of 10" );
        return (int)(16*hexaPos/10);
    }
    
    
}
