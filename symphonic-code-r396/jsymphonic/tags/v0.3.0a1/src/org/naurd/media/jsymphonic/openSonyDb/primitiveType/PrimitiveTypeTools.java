/*
 * PrimitiveTypeTools.java
 *
 * Created on 30 mars 2008, 18:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.openSonyDb.primitiveType;

/**
 *
 * @author neub
 */
public class PrimitiveTypeTools {
    
    public static int fourBytes2int(byte[] bytes) {
        int ret=-1;
        if(bytes.length != 4) return ret;

        //Example to code shifting values <<
        
        return ret;
    }
    
     /*Converts the number represented by some value in an array of bytes to an int. For example, if the array is : ["1";"2";"3";"4"]=[49;50;51;52], the corresponding int will be "1234".
     *
     *@param bytes The array of bytes to convert
     *
     *@return The converted number.
     *
     *@author nicolas_cardoso
     */
    public static int charBytes2int(byte[] bytes){
        int i = bytes.length - 1;
        int ret = 0;
        byte val;
        int pow = 0;
        
        while(i >= 0){
            val = (byte)(bytes[i]-(byte)48);
            if(val < 0 || val > 9) {
                val = -1;
                return 0;
            }

            ret += val*Math.pow(10, pow);
            pow++;
            i--;
        }
        return ret;
    }
    
}
