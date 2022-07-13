/*
 * Debug.java
 *
 * Created on 1 avril 2008, 12:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.openSonyDb;

/**
 *
 * @author neub
 */
public class Debug {
    /*
     * Comment by nicolas_cardoso:
     * I don't know for what you had create this "Debug" class, but the "debug" point of view used in v2.1a is not used anymore. We are now using the "Logger" class. Please contact me if you need more info. 
     */
    
    public static void debug(boolean test) {
        if(!test) 
            System.err.println("JOpenSonyDb Error");
        
    }
    
    public static void perr(boolean test, String msg) {
        if(!test)
            System.err.println("JOpenSonyDb Error > "+msg);
    }
    
}
