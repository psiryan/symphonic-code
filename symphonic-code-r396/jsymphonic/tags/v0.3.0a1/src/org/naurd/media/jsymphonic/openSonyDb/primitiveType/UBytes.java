/*
 * UBytes.java
 *
 * Created on 1 avril 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.openSonyDb.primitiveType;

import java.util.ArrayList;
import javax.lang.model.type.PrimitiveType;

/**
 *
 * @author neub
 */
public class UBytes {
    
    byte[] vec;
    /** Creates a new instance of UBytes */
    public UBytes(int length) {
        vec = new byte[length];
    }  
    
    public int set(byte[] buff, int pos) {
         //Check if the buffer is not to small
        int nextPos = pos+vec.length;
        if(nextPos > buff.length) return -1;
        
        int tmp=0;
        for(int i=pos;i<nextPos;i++) {
            vec[tmp++]=buff[i];
        }
        return nextPos;
    }
    
    
    public int length() {
        return vec.length;
    }
    
    public char[] toChar() {
        char[] ret = new char[vec.length];
        for(int i=0;i<ret.length;i++) {
            ret[i]=(char)(vec[i] & 0xFF);
        }
        return ret;
    }
    
    
    public String toString() {
        return new String(toChar());
    }
    
    public void debug() {
        /*
         * Comment by nicolas_cardoso:
         * same comment as in the Debug class, you should now use logger...
         */
        for(int i=0;i<vec.length;i++) {
            System.out.println(vec[i]);
        }
    }
    
}
