/*
 * ObjectPointeur.java
 *
 * Created on 30 mars 2008, 18:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.openSonyDb.structDat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.naurd.media.jsymphonic.openSonyDb.primitiveType.PrimitiveTypeTools;
import org.naurd.media.jsymphonic.openSonyDb.primitiveType.U16bits;
import org.naurd.media.jsymphonic.openSonyDb.primitiveType.U32bits;
import org.naurd.media.jsymphonic.openSonyDb.primitiveType.UBytes;

/**
 * This class contains in total 16 bytes
 * @author neub
 */
public class ObjectHeader extends NativeStructure {
        
    private UBytes  magicID;     /* 4 bytes : "magicID file descriptor (Same than the object)" */
    private U16bits count;       /* 2 bytes : offset of the object (from the beginning)*/
    private U16bits size;        /* 2 bytes : size of (ObjectHeader + Object) in bytes */
    private UBytes padding;      /* 8 bytes : padding (filled with zeros) */
    
    private boolean isFilled=false;

    
    /** Creates a new instance of ObjectPointeur */
    public ObjectHeader() {
      

        magicID = new UBytes(4);     
	count  = new U16bits();     
	size  = new U16bits();      
	padding = new UBytes(8);    
        
        isFilled = false;
    }

    public void read(FileInputStream f) {
        
    }
    
    public void read(FileInputStream f, ObjectPointeur pObj) {
        
        

        byte[] buff= new byte[16];    /* We first read the 16 bytes in an array of char */
        
        //Read the total file Header from the fileReader
        try {           
            //Move to the position given by objectPointeur
            f.skip(pObj.getPosition());
            
            //Then read objectHeader in the buffer
            f.read(buff);    
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        //Initiate the position of the buffer elements
        int pos=0;
        pos = magicID.set(buff,pos);
        pos = count.set(buff,pos);
        pos = size.set(buff,pos);
        pos = padding.set(buff,pos);
        
        isFilled=true;
   
        
    }

   
    public void write(FileInputStream f) {
    }
    
    
    public long getCount() {
       return count.getLong();
    }
    
    public long getSize() {
        return size.getLong();
    }
    
    public String toString() {
        return "ObjectHeader: magicID="+magicID+", count="+count+", length="+size;
    }
    
}
