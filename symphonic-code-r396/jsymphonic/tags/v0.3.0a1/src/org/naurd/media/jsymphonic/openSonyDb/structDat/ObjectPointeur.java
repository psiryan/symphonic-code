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
import org.naurd.media.jsymphonic.openSonyDb.primitiveType.U32bits;
import org.naurd.media.jsymphonic.openSonyDb.primitiveType.UBytes;

/**
 * This class contains in total 16 bytes
 * @author neub
 */
public class ObjectPointeur extends NativeStructure {
    
    private byte[] buff; /* This class contains in total 16 bytes */
    
    private UBytes  magicID;      /* 4 bytes : "magicID file descriptor" */
    private U32bits offset;       /* 4 bytes : offset of the object (from the beginning)*/
    private U32bits length;       /* 4 bytes : size of (ObjectHeader + Object) in bytes */
    private UBytes padding;       /* 4 bytes : padding (filled with zeros) */
    
    private boolean isFilled=false;

    
    /** Creates a new instance of ObjectPointeur */
    public ObjectPointeur() {
        buff= new byte[16];    /* We first read the 16 bytes in an array of char */

        magicID = new UBytes(4);     
	offset  = new U32bits();     
	length  = new U32bits();      
	padding = new UBytes(4);    
        
        isFilled = false;
    }

    public void read(FileInputStream f) {
                 
        //Read the total file Header from the fileReader
        try {
            //Read the magicID description
            f.read(buff);    
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        //Initiate the position of each element in the fileHeader
        int pos=0;
        pos = magicID.set(buff,pos);
        pos = offset.set(buff,pos);
        pos = length.set(buff,pos);
        pos = padding.set(buff,pos);
        
        isFilled=true;
   
        
    }

   
    public void write(FileInputStream f) {
    }
    
    
    public long getPosition() {
       return offset.getLong();
    }
    
    public long getNextPosition() {
        return offset.getLong()+length.getLong();
    }
    
    public String toString() {
        return "ObjectPointeur: magicID="+magicID+", offset="+offset+", length="+length;
    }
    
}
