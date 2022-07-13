/*
 * U32bits.java
 *
 * Created on 1 avril 2008, 15:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.openSonyDb.primitiveType;

/**
 *
 * @author neub
 */
public class U16bits extends UBytes{
    
    /** Creates a new instance of U32bits */
    public U16bits() {
        super(2); //In 16 bits we have 2 bytes (2x8=16)
    }
        
    /**
     * This method transform the 4 bytes writed in big endians to an unsigned int (what we can call a long)
     */
    public long getLong() {
        int tmp=0;
        if(vec.length != 2) return -1;
        tmp += ((int)(vec[1] << 0) & 0x00FF);
        tmp += ((int)(vec[0] << 8) & 0xFF00);
        return (long)(tmp & 0xFFFFFFFF);
    }
    
    public String toString() {
        return ""+getLong();
    }
    
}
