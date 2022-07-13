/*
 * U8bits.java
 *
 * Created on 1 avril 2008, 12:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.openSonyDb.primitiveType;

/**
 * This class let manage the unsigned type (8bits).
 * @author neub
 */
public class U8bits {
    
    byte realbyte;
    
    /**
     * Creates a new instance of U8bits
     */
    public U8bits() {
        realbyte = 0;
    }
    
    public U8bits(int val) {
        if(0 <= val && val < 256) {
            realbyte=(byte)val;
        }     
    }
    
    /** Set with int value */
    public void set(int val) {
        if(0 <= val && val < 256) {
            realbyte=(byte)val;
        }  
    }
    
     /** Set with byte[] value */
    public int set(byte[] vals, int pos) {
        realbyte=vals[pos];
        return pos+1;
    }
    
    
    
    public int getInt() {
        return (int)(realbyte & 0xff);
    }
    
    public long getLong() {
        return (long)(realbyte & 0xFF);
    }
    
    public String toString() {
        return ""+getInt();
    }
    
    
}
