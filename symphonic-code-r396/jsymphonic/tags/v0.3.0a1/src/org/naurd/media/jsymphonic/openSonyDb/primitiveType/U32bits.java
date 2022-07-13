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
public class U32bits extends UBytes{
    
    /** Creates a new instance of U32bits */
    public U32bits() {
        super(4); //In 32 bits we have 4 bytes
    }
    
    
    /**
     * This method transform the 4 bytes writed in big endians to an unsigned int (what we can call a long)
     */
    public long getLong() {
        int tmp=0;
        if(vec.length != 4) return -1;
        tmp += ((int)(vec[3] << 0 ) & 0x000000FF);
        tmp += ((int)(vec[2] << 8 ) & 0x0000FF00);
        tmp += ((int)(vec[1] << 16) & 0x00FF0000);
        tmp += ((int)(vec[0] << 24) & 0xFF000000);
        return (long)(tmp & 0xFFFFFFFF);
    }
    
    public String toString() {
        return ""+getLong();
    }
    
}
