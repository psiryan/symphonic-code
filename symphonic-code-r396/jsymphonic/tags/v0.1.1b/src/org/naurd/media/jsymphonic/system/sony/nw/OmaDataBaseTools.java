/*
 * OmaDataBaseTools.java
 *
 * Created on 24 juillet 2007, 13:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.system.sony.nw;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.HashMap;
import java.io.RandomAccessFile;
import org.naurd.media.jsymphonic.title.Title;

/**
 *
 * @author skiron
 */
public class OmaDataBaseTools {
    
    /** Creates a new instance of OmaDataBaseTools */
    public OmaDataBaseTools() {
    }
    
    public static void WriteTableHeader(RandomAccessFile raf, String tableName, int numberOfClasses) throws java.io.IOException {
        byte[] bytesTableName;
        byte[] constant = {1,1,0,0};
        
        if( tableName.length() != 4 ) { //Control the name of the table (must be 4 characters long)
            System.out.println("Invalid table name while writing config files. Exiting the program.");
            System.exit(-1);
        }

        if( numberOfClasses != 1 && numberOfClasses != 2 ) { //Control the number of classes (must be 1 or 2)
            System.out.println("Invalid number of classes while writing config files. Exiting the program.");
            System.exit(-1);
        }
        
        bytesTableName = tableName.getBytes();
        raf.write(bytesTableName);

        raf.write(constant);
        
        raf.write(numberOfClasses);
        
        OmaDataBaseTools.WriteZeros(raf, 7);
    }

    public static void WriteClassDescription(RandomAccessFile raf, String className, int startAdress, int length) throws java.io.IOException {
        byte[] bytesClassName;
        byte[] bytesLength;
        byte[] bytesStartAdress;
        
        if( className.length() != 4 ) { //Control the name of the class (must be 4 characters long)
            System.out.println("Invalid table name while writing config files. Exiting the program.");
            System.exit(-1);
        }
        
        bytesClassName = className.getBytes();
        raf.write(bytesClassName);
        
        bytesStartAdress = OmaDataBaseTools.int2bytes(startAdress, 4);
        raf.write(bytesStartAdress);
        
        bytesLength = OmaDataBaseTools.int2bytes(length, 4);
        raf.write(bytesLength);
        
        OmaDataBaseTools.WriteZeros(raf, 4);
    }
    
    public static void WriteClassHeader(RandomAccessFile raf, String className, int numberOfElement, int lengthOfOneElement) throws java.io.IOException {
        byte[] bytesClassName;
        byte[] bytesNumberOfElement;
        byte[] bytesLengthOfOneElement;
        
        if( className.length() != 4 ) { //Control the name of the class (must be 4 characters long)
            System.out.println("Invalid table name while writing config files. Exiting the program.");
            System.exit(-1);
        }
        
        bytesClassName = className.getBytes();
        raf.write(bytesClassName);

        bytesNumberOfElement = OmaDataBaseTools.int2bytes(numberOfElement, 2);
        raf.write(bytesNumberOfElement);

        bytesLengthOfOneElement = OmaDataBaseTools.int2bytes(lengthOfOneElement, 2);
        raf.write(bytesLengthOfOneElement);
        
        OmaDataBaseTools.WriteZeros(raf, 8);
    }

    public static void WriteClassHeader(RandomAccessFile raf, String className, int numberOfElement, int lengthOfOneElement, int classHeaderComplement1, int classHeaderComplement2) throws java.io.IOException {
        if( className.length() != 4 ) { //Control the name of the class (must be 4 characters long)
            System.out.println("Invalid table name while writing config files. Exiting the program.");
            System.exit(-1);
        }
        
        raf.write(className.getBytes());
        raf.write(OmaDataBaseTools.int2bytes(numberOfElement, 2));
        raf.write(OmaDataBaseTools.int2bytes(lengthOfOneElement, 2));
        raf.write(OmaDataBaseTools.int2bytes(classHeaderComplement1, 4));
        raf.write(OmaDataBaseTools.int2bytes(classHeaderComplement2, 4));
    }
    
    public static void WriteString16(RandomAccessFile raf, String string) throws java.io.IOException {
        if( string.isEmpty() ){
            return;
        }
        
        raf.write(string.getBytes("UTF-16"), 2, string.getBytes("UTF-16").length - 2);
    }

    public static void WriteZeros(RandomAccessFile raf, int numberOfZeros) throws java.io.IOException {
        for( int i = 0; i < numberOfZeros; i++) {
            raf.write(0);
        }
    }

    public static void WriteGTLBelement(RandomAccessFile raf, int fileRef, int unknown1, int numberOfTag, String tag1, String tag2, byte[] unknown2) throws java.io.IOException {
        byte[] bytesFileRef;
        byte[] bytesUnknow1;
        byte[] bytesNumberOfTag;
        byte[] bytesTag1;
        byte[] bytesTag2;

        bytesFileRef = OmaDataBaseTools.int2bytes(fileRef, 2);
        raf.write(bytesFileRef);

        bytesUnknow1 = OmaDataBaseTools.int2bytes(unknown1, 2);
        raf.write(bytesUnknow1);
       
        OmaDataBaseTools.WriteZeros(raf, 12);
        
        bytesNumberOfTag = OmaDataBaseTools.int2bytes(numberOfTag, 2);
        raf.write(bytesNumberOfTag);

        OmaDataBaseTools.WriteZeros(raf, 2);

        if( numberOfTag > 0) {
            bytesTag1 = tag1.getBytes();
            raf.write(bytesTag1);
            
            // Control that the right number of bytes have been written
            if( tag1.length() < 4 ){
                OmaDataBaseTools.WriteZeros(raf, 4 - tag1.length());
            }
        }
        else {
            OmaDataBaseTools.WriteZeros(raf, 4);
        }

        if( numberOfTag > 1 ) {
            bytesTag2 = tag2.getBytes();
            raf.write(bytesTag2);
            
            // Control that the right number of bytes have been written
            if( tag2.length() < 4 ){
                OmaDataBaseTools.WriteZeros(raf, 4 - tag2.length());
            }
        }
        else {
            OmaDataBaseTools.WriteZeros(raf, 4);
        }
        
        OmaDataBaseTools.WriteZeros(raf, 20);

        raf.write(unknown2);
        
        //Complete the end of the element with zeros
        OmaDataBaseTools.WriteZeros(raf, 0x50 - 2*2 - 12 - 2*2 - 4*2 - 20 - unknown2.length); //Complete element with zeros
    }
    
    public static void WriteGPLBelement(RandomAccessFile raf, int itemIdIn03GINFXX, int titleIdInTPLBlist) throws java.io.IOException {
        byte[] constant = {1,0};

        raf.write(OmaDataBaseTools.int2bytes(itemIdIn03GINFXX, 2));
        raf.write(constant);
        raf.write(OmaDataBaseTools.int2bytes(titleIdInTPLBlist, 2));
        OmaDataBaseTools.WriteZeros(raf, 2);
    }
    
    public static void WriteGPLBelement2(RandomAccessFile raf, int itemIdIn03GINFXX, int titleIdInTPLBlist) throws java.io.IOException {
        byte[] constant = {2,0};

        raf.write(OmaDataBaseTools.int2bytes(itemIdIn03GINFXX, 2));
        raf.write(constant);
        raf.write(OmaDataBaseTools.int2bytes(titleIdInTPLBlist, 2));
        OmaDataBaseTools.WriteZeros(raf, 2);
    }

    public static void WriteGPFBelement(RandomAccessFile raf, int albumKey, String albumName, String artistName, String genre) throws java.io.IOException {
        byte[] constant1 = {0,6,0,-128}; //-128 as a signed byte equals 128 as an unsigned byte equal 0x80 in hex
        byte[] constant2 = {0,2};
        String tag1 = "TIT2";
        String tag2 = "TPE1";
        String tag3 = "TCON";
        String tag4 = "TSOP";
        String tag5 = "PICP";
        String tag6 = "PIC0";
        
        //Element header
        OmaDataBaseTools.WriteZeros(raf, 8);
        raf.write(OmaDataBaseTools.int2bytes(albumKey, 4));
        raf.write(constant1);
        
        //Fist sub-element
        raf.write(tag1.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteString16(raf, albumName);
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2 - (albumName.length()*2) );

        //Second sub-element
        raf.write(tag2.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteString16(raf, artistName);
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2 - (artistName.length()*2) );
        
        //Third sub-element
        raf.write(tag3.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteString16(raf, genre);
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2 - (genre.length()*2) );
        
        //Fourth sub-element
        raf.write(tag4.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2 );

        //Fifth sub-element
        raf.write(tag5.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2 );

        //Sixth sub-element
        raf.write(tag6.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2 );
    }
    
    public static void WriteGPFBelement(RandomAccessFile raf, int albumKey, String artistName) throws java.io.IOException {
        byte[] constant1 = {0,1,0,-128}; //-128 as a signed byte equals 128 as an unsigned byte equal 0x80 in hex
        byte[] constant2 = {0,2};
        String tag1 = "TIT2";
        
        //Element header
        OmaDataBaseTools.WriteZeros(raf, 8);
        raf.write(OmaDataBaseTools.int2bytes(albumKey, 4));
        raf.write(constant1);
        
        //Fist sub-element
        raf.write(tag1.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteString16(raf, artistName);
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2 - (artistName.length()*2) );
    }
    
    public static void WriteGPFBelement(RandomAccessFile raf, int albumKey, String elementName1, String elementName2) throws java.io.IOException {
        byte[] constant1 = {0,2,0,-128}; //-128 as a signed byte equals 128 as an unsigned byte equal 0x80 in hex
        byte[] constant2 = {0,2};
        String tag1 = "TIT2";
        String tag2 = "XSOT";
        
        //Element header
        OmaDataBaseTools.WriteZeros(raf, 8);
        raf.write(OmaDataBaseTools.int2bytes(albumKey, 4));
        raf.write(constant1);
        
        //Fist sub-element
        raf.write(tag1.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteString16(raf, elementName1);
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2 - (elementName1.length()*2) );
        
        //Second sub-element
        raf.write(tag2.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteString16(raf, elementName2);
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2 - (elementName2.length()*2) );
    }
    
    public static void WriteGTFBelement(RandomAccessFile raf, int key, String unknown) throws java.io.IOException {
        byte[] constant1 = {0,1,0,-128}; //-128 as a signed byte equals 128 as an unsigned byte equal 0x80 in hex
        byte[] constant2 = {0,2};
        String tag = "TIT2";
        
        OmaDataBaseTools.WriteZeros(raf, 8);
        raf.write(OmaDataBaseTools.int2bytes(key, 4));
        raf.write(constant1);
        raf.write(tag.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteString16(raf, unknown);
        
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2 - (unknown.length()*2) );
    }

    public static void WriteCNFBelement(RandomAccessFile raf, int magicKey, int titleKey, String titleName, String artistName, String albumName, String genreName) throws java.io.IOException {
        byte[] constant1 = {0,5,0,-128}; //-128 as a signed byte equals 128 as an unsigned byte equal 0x80 in hex
        byte[] constant2 = {0,2};
        byte[] constant3 = {0,0,-1,-1}; //-1 as a signed byte equals 256 as an unsigned byte equal 0xFF in hex
        String tag1 = "TIT2";
        String tag2 = "TPE1";
        String tag3 = "TALB";
        String tag4 = "TCON";
        String tag5 = "TSOP";
        
        //Element header
        raf.write(constant3);
        raf.write(OmaDataBaseTools.int2bytes(magicKey, 4));
        raf.write(OmaDataBaseTools.int2bytes(titleKey, 4));
        raf.write(constant1);
        
        //Fist sub-element
        raf.write(tag1.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteString16(raf, titleName);
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2 - (titleName.length()*2) );
        
        //Second sub-element
        raf.write(tag2.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteString16(raf, artistName);
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2 - (artistName.length()*2) );

        //Third sub-element
        raf.write(tag3.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteString16(raf, albumName);
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2 - (albumName.length()*2) );
        
        //Fourth sub-element
        raf.write(tag4.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteString16(raf, genreName);
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2 - (genreName.length()*2) );
        
        //Fifth sub-element
        raf.write(tag5.getBytes());
        raf.write(constant2);
        OmaDataBaseTools.WriteZeros(raf, 0x80 - 4 - 2);
    }
    
    public static void WriteCILBelement(RandomAccessFile raf, int part1, int part2, int part3, int part4, int part5, int part6) throws java.io.IOException {
        raf.write(OmaDataBaseTools.int2bytes(part1, 4));
        raf.write(OmaDataBaseTools.int2bytes(part2, 4));
        raf.write(OmaDataBaseTools.int2bytes(part3, 4));
        raf.write(OmaDataBaseTools.int2bytes(part4, 4));
        raf.write(OmaDataBaseTools.int2bytes(part5, 4));
        raf.write(OmaDataBaseTools.int2bytes(part6, 4));
        OmaDataBaseTools.WriteZeros(raf, 0x30 - 4*6);
    }
    
    /**
    *Tells is an array of bytes is full with zeros.
    *
    *@param bytes The array of bytes to test
    *@return The answer, true for "yes" and false for "no"
    */
    public static boolean isZero(byte[] bytes){
        int i = 0;
        boolean ret = true;
        
        while(i < bytes.length){
            if(bytes[i] == 0){
                ret = ret && true;
            }
            else{
                ret = false;
            }
            i++;
        }
        return ret;
    }
    
    /**
    *Converts an array of bytes to an int. For example, if the array is : [1;2;3;4], the corresponding int will be "1234".
    *
    *@param bytes The array of bytes to convert
    *@return The converted number.
    */
    public static int bytes2int(byte[] bytes){
        int i = bytes.length - 1;
        int ret = 0;
        
        while(i >= 0){
            ret += bytes[i]*Math.pow(10, bytes.length - (i+1));
            i--;
        }
        return ret;
    }
    
    public static byte[] int2bytes(Integer integer){
        int numberOfDigit;
        Integer digit;
        byte[] bytesToReturn;
        
        // Search the number of digit in the integer
        numberOfDigit = (int)((Integer.toBinaryString(integer).length() / 8.0 ) + 0.99); // 0.99 is added to round up the number (can't add 1 beacause in some case, the right number is found, so added 1 make it false althought it's not)
        
        // Create the array of bytes
        bytesToReturn = new byte[numberOfDigit];
        
        // Fill in the array
        for( int i = numberOfDigit - 1; i >= 0; i-- ) {
            digit = integer % 256;
            integer = integer / 256;
            bytesToReturn[i] = (byte) digit.byteValue();
        }
        
        return bytesToReturn;
    }    
    
    public static byte[] int2bytes(int integer, int bytesLength){
        int numberOfDigit;
        Integer digit;
        byte[] bytesToReturn;
        
        // Search the number of digit in the integer
        numberOfDigit = (int)((Integer.toBinaryString(integer).length() / 8.0 ) + 0.99); // 0.99 is added to round up the number (can't add 1 beacause in some case, the right number is found, so added 1 make it false althought it's not)

        // Create the array of bytes
        bytesToReturn = new byte[bytesLength];
        
        if( numberOfDigit > bytesLength ) {
            System.out.println("Impossible to fit the integer in that bytesLength in method 'int2bytes'. Existing program.");
            System.exit(-1);
        }
        
        // Fill in the integer in the array
        for( int i = bytesLength - 1; i >= bytesLength - numberOfDigit; i-- ) {
            digit = integer % 256;
            integer = integer / 256;
            bytesToReturn[i] = digit.byteValue();
        }

        // Fill in the rest of the array with zeros
        for( int i = bytesLength - numberOfDigit - 1; i >= 0; i-- ) {
            bytesToReturn[i] = 0;
        }
        
        return bytesToReturn;
    }   
    
//    public static byte[] double2bytes(double doubleNumber, int bytesLength){
//        int numberOfDigit;
//        Integer digit;
//        Double digitDouble;
//        byte[] bytesToReturn;
//        
//        // Search the number of digit in the integer
//        numberOfDigit = (int)((Double.toHexString(doubleNumber).length() / 2.0 ) + 0.99); // 0.99 is added to round up the number (can't add 1 beacause in some case, the right number is found, so added 1 make it false althought it's not)
//        
//        // Create the array of bytes
//        bytesToReturn = new byte[bytesLength];
//        
//        if( numberOfDigit > bytesLength ) {
//            System.out.println("Impossible to fit the integer in that bytesLength in method 'int2bytes'. Existing program.");
//            System.exit(-1);
//        }
//        
//        // Fill in the integer in the array
//        for( int i = bytesLength - 1; i >= bytesLength - numberOfDigit; i-- ) {
//            digitDouble = doubleNumber % 256;
//            digit = digitDouble.intValue();
//            doubleNumber = doubleNumber / 256;
//            bytesToReturn[i] = digit.byteValue();
//        }
//
//        // Fill in the rest of the array with zeros
//        for( int i = bytesLength - numberOfDigit - 1; i >= 0; i-- ) {
//            bytesToReturn[i] = 0;
//        }
//        
//        return bytesToReturn;
//    }   

    public static List sortByArtistTitle(Map titles){
        List listToReturn = new ArrayList();
        Set titlesSet = titles.keySet();
        int i = 1;
        String a,b;
        int j;
        Object temp;
        
        Iterator it = titlesSet.iterator();
        
        if( it.hasNext() ) {
            listToReturn.add(0, it.next()); //If the set isn't empty, fill in the first element
        }
        
        while( it.hasNext() ) { //For each title
            listToReturn.add(i, it.next()); // Add the current element
            
            //Sort the current List in "artist - title" order
            a = ((Title)listToReturn.get(i-1)).getArtist() + ((Title)listToReturn.get(i-1)).getTitle();
            b = ((Title)listToReturn.get(i)).getArtist() + ((Title)listToReturn.get(i)).getTitle();
            j = i;
            while( a.compareTo(b) > 0 ) {
                temp = listToReturn.get(j);
                listToReturn.remove(j);
                listToReturn.add(j, listToReturn.get(j-1));
                listToReturn.remove(j-1);
                listToReturn.add(j-1, temp);
                
                if( j-2 < 0 ) {
                    break; //If j-2 is negative, the beginnig of the list is reached, there is nothing left to sort
                }
                j--;
                a = ((Title)listToReturn.get(j-1)).getArtist() + ((Title)listToReturn.get(j-1)).getTitle();
                b = ((Title)listToReturn.get(j)).getArtist() + ((Title)listToReturn.get(j)).getTitle();
            }
            
            i++;
        }
        
        return listToReturn;
    }
    
      public static List sortByGenreTitle(Map titles){
        List listToReturn = new ArrayList();
        Set titlesSet = titles.keySet();
        int i = 1;
        String a,b;
        int j;
        Object temp;
        
        Iterator it = titlesSet.iterator();
        
        if( it.hasNext() ) {
            listToReturn.add(0, it.next()); //If the set isn't empty, fill in the first element
        }
        
        while( it.hasNext() ) { //For each title
            listToReturn.add(i, it.next()); // Add the current element
            
            //Sort the current List in "genre - title" order
            a = ((Title)listToReturn.get(i-1)).getGenre() + ((Title)listToReturn.get(i-1)).getTitle();
            b = ((Title)listToReturn.get(i)).getGenre() + ((Title)listToReturn.get(i)).getTitle();
            j = i;
            while( a.compareTo(b) > 0 ) {
                temp = listToReturn.get(j);
                listToReturn.remove(j);
                listToReturn.add(j, listToReturn.get(j-1));
                listToReturn.remove(j-1);
                listToReturn.add(j-1, temp);
                
                if( j-2 < 0 ) {
                    break; //If j-2 is negative, the beginnig of the list is reached, there is nothing left to sort
                }
                j--;
                a = ((Title)listToReturn.get(j-1)).getGenre() + ((Title)listToReturn.get(j-1)).getTitle();
                b = ((Title)listToReturn.get(j)).getGenre() + ((Title)listToReturn.get(j)).getTitle();
            }
            
            i++;
        }
        
        return listToReturn;
    }
      
      public static List sortByGenreArtistAlbumTitle(Map titles){
        List listToReturn = new ArrayList();
        Set titlesSet = titles.keySet();
        int i = 1;
        String a,b;
        int j,tempTitleNumber;
        Object temp;
        
        Iterator it = titlesSet.iterator();
        
        if( it.hasNext() ) {
            listToReturn.add(0, it.next()); //If the set isn't empty, fill in the first element
        }
        
        while( it.hasNext() ) { //For each title
            listToReturn.add(i, it.next()); // Add the current element
            
            //Sort the current List in "genre - artist - album - titleNumber" order
            tempTitleNumber = ((Title)listToReturn.get(i-1)).getTitleNumber();
            if(tempTitleNumber < 10) { //Add a zero to the title number is it's less than 10 to be properly compared as a string
                a = ((Title)listToReturn.get(i-1)).getGenre() + ((Title)listToReturn.get(i-1)).getArtist() + ((Title)listToReturn.get(i-1)).getAlbum() + "0" + tempTitleNumber;
            }
            else{
                a = ((Title)listToReturn.get(i-1)).getGenre() + ((Title)listToReturn.get(i-1)).getArtist() + ((Title)listToReturn.get(i-1)).getAlbum() + tempTitleNumber;
            }
            tempTitleNumber = ((Title)listToReturn.get(i)).getTitleNumber();
            if(tempTitleNumber < 10) {
                b = ((Title)listToReturn.get(i)).getGenre() + ((Title)listToReturn.get(i)).getArtist() + ((Title)listToReturn.get(i)).getAlbum() + "0" + tempTitleNumber;
            }
            else{
                b = ((Title)listToReturn.get(i)).getGenre() + ((Title)listToReturn.get(i)).getArtist() + ((Title)listToReturn.get(i)).getAlbum() + tempTitleNumber;
            }
            j = i;
            while( a.compareTo(b) > 0 ) {
                temp = listToReturn.get(j);
                listToReturn.remove(j);
                listToReturn.add(j, listToReturn.get(j-1));
                listToReturn.remove(j-1);
                listToReturn.add(j-1, temp);
                
                if( j-2 < 0 ) {
                    break; //If j-2 is negative, the beginnig of the list is reached, there is nothing left to sort
                }
                j--;
                tempTitleNumber = ((Title)listToReturn.get(j-1)).getTitleNumber();
                if(tempTitleNumber < 10) { //Add a zero to the title number is it's less than 10 to be properly compared as a string
                    a = ((Title)listToReturn.get(i-1)).getGenre() + ((Title)listToReturn.get(j-1)).getArtist() + ((Title)listToReturn.get(j-1)).getAlbum() + "0" + tempTitleNumber;
                }
                else{
                    a = ((Title)listToReturn.get(i-1)).getGenre() + ((Title)listToReturn.get(j-1)).getArtist() + ((Title)listToReturn.get(j-1)).getAlbum() + tempTitleNumber;
                }
                tempTitleNumber = ((Title)listToReturn.get(j)).getTitleNumber();
                if(tempTitleNumber < 10) {
                    b = ((Title)listToReturn.get(i)).getGenre() + ((Title)listToReturn.get(j)).getArtist() + ((Title)listToReturn.get(j)).getAlbum() + "0" + tempTitleNumber;
                }
                else{
                    b = ((Title)listToReturn.get(i)).getGenre() + ((Title)listToReturn.get(j)).getArtist() + ((Title)listToReturn.get(j)).getAlbum() + tempTitleNumber;
                }
            }
            
            i++;
        }
        return listToReturn;
    }

      public static List sortByAlbumTitleNumber(Map titles){
        List listToReturn = new ArrayList();
        Set titlesSet = titles.keySet();
        int i = 1;
        String a,b;
        int j,tempTitleNumber;
        Object temp;
        
        Iterator it = titlesSet.iterator();
        
        if( it.hasNext() ) {
            listToReturn.add(0, it.next()); //If the set isn't empty, fill in the first element
        }
        
        while( it.hasNext() ) { //For each title
            listToReturn.add(i, it.next()); // Add the current element
            
            //Sort the current List in "album - titleNumber" order
            tempTitleNumber = ((Title)listToReturn.get(i-1)).getTitleNumber();
            if(tempTitleNumber < 10) { //Add a zero to the title number is it's less than 10 to be properly compared as a string
                a = ((Title)listToReturn.get(i-1)).getAlbum() + "0" + tempTitleNumber;
            }
            else{
                a = ((Title)listToReturn.get(i-1)).getAlbum() + tempTitleNumber;
            }
            tempTitleNumber = ((Title)listToReturn.get(i)).getTitleNumber();
            if(tempTitleNumber < 10) {
                b = ((Title)listToReturn.get(i)).getAlbum() + "0" + tempTitleNumber;
            }
            else{
                b = ((Title)listToReturn.get(i)).getAlbum() + tempTitleNumber;
            }
            j = i;
            while( a.compareTo(b) > 0 ) {
                temp = listToReturn.get(j);
                listToReturn.remove(j);
                listToReturn.add(j, listToReturn.get(j-1));
                listToReturn.remove(j-1);
                listToReturn.add(j-1, temp);
                
                if( j-2 < 0 ) {
                    break; //If j-2 is negative, the beginnig of the list is reached, there is nothing left to sort
                }
                j--;
                tempTitleNumber = ((Title)listToReturn.get(j-1)).getTitleNumber();
                if(tempTitleNumber < 10) { //Add a zero to the title number is it's less than 10 to be properly compared as a string
                    a = ((Title)listToReturn.get(j-1)).getAlbum() + "0" + tempTitleNumber;
                }
                else{
                    a = ((Title)listToReturn.get(j-1)).getAlbum() + tempTitleNumber;
                }
                tempTitleNumber = ((Title)listToReturn.get(j)).getTitleNumber();
                if(tempTitleNumber < 10) {
                    b = ((Title)listToReturn.get(j)).getAlbum() + "0" + tempTitleNumber;
                }
                else{
                    b = ((Title)listToReturn.get(j)).getAlbum() + tempTitleNumber;
                }
            }
            
            i++;
        }
        
        return listToReturn;
    }
    
    public static List sortByArtistAlbumTitleNumber(Map titles){
        List listToReturn = new ArrayList();
        Set titlesSet = titles.keySet();
        int i = 1;
        String a,b;
        int j,tempTitleNumber;
        Object temp;
        
        Iterator it = titlesSet.iterator();
        
        if( it.hasNext() ) {
            listToReturn.add(0, it.next()); //If the set isn't empty, fill in the first element
        }
        
        while( it.hasNext() ) { //For each title
            listToReturn.add(i, it.next()); // Add the current element
            
            //Sort the current List in "album - titleNumber" order
            tempTitleNumber = ((Title)listToReturn.get(i-1)).getTitleNumber();
            if(tempTitleNumber < 10) { //Add a zero to the title number is it's less than 10 to be properly compared as a string
                a = ((Title)listToReturn.get(i-1)).getArtist() + ((Title)listToReturn.get(i-1)).getAlbum() + "0" + tempTitleNumber;
            }
            else{
                a = ((Title)listToReturn.get(i-1)).getArtist() + ((Title)listToReturn.get(i-1)).getAlbum() + tempTitleNumber;
            }
            tempTitleNumber = ((Title)listToReturn.get(i)).getTitleNumber();
            if(tempTitleNumber < 10) {
                b = ((Title)listToReturn.get(i)).getArtist() + ((Title)listToReturn.get(i)).getAlbum() + "0" + tempTitleNumber;
            }
            else{
                b = ((Title)listToReturn.get(i)).getArtist() + ((Title)listToReturn.get(i)).getAlbum() + tempTitleNumber;
            }
            j = i;
            while( a.compareTo(b) > 0 ) {
                temp = listToReturn.get(j);
                listToReturn.remove(j);
                listToReturn.add(j, listToReturn.get(j-1));
                listToReturn.remove(j-1);
                listToReturn.add(j-1, temp);
                
                if( j-2 < 0 ) {
                    break; //If j-2 is negative, the beginnig of the list is reached, there is nothing left to sort
                }
                j--;
                tempTitleNumber = ((Title)listToReturn.get(j-1)).getTitleNumber();
                if(tempTitleNumber < 10) { //Add a zero to the title number is it's less than 10 to be properly compared as a string
                    a = ((Title)listToReturn.get(j-1)).getArtist() + ((Title)listToReturn.get(j-1)).getAlbum() + "0" + tempTitleNumber;
                }
                else{
                    a = ((Title)listToReturn.get(j-1)).getArtist() + ((Title)listToReturn.get(j-1)).getAlbum() + tempTitleNumber;
                }
                tempTitleNumber = ((Title)listToReturn.get(j)).getTitleNumber();
                if(tempTitleNumber < 10) {
                    b = ((Title)listToReturn.get(j)).getArtist() + ((Title)listToReturn.get(j)).getAlbum() + "0" + tempTitleNumber;
                }
                else{
                    b = ((Title)listToReturn.get(j)).getArtist() + ((Title)listToReturn.get(j)).getAlbum() + tempTitleNumber;
                }
            }
            
            i++;
        }
        
        return listToReturn;
    }
      
    public static List sortByTitleId(Map titles){
        List listToReturn = new ArrayList();
        Set titlesSet = titles.keySet();
        int i = 1;
        int a,b,j;
        Object temp;
        
        Iterator it = titlesSet.iterator();
        
        if( it.hasNext() ) {
            listToReturn.add(0, it.next()); //If the set isn't empty, fill in the first element
        }
        
        while( it.hasNext() ) { //For each title
            listToReturn.add(i, it.next()); // Add the current element
            
            //Sort the current List
            a = (Integer)titles.get(listToReturn.get(i-1));
            b = (Integer)titles.get(listToReturn.get(i));
            j = i;
            while( a > b ) {
                temp = listToReturn.get(j);
                listToReturn.remove(j);
                listToReturn.add(j, listToReturn.get(j-1));
                listToReturn.remove(j-1);
                listToReturn.add(j-1, temp);
                
                if( j-2 < 0 ) {
                    break; //If j-2 is negative, the beginnig of the list is reached, there is nothing left to sort
                }
                j--;
                a = (Integer)titles.get(listToReturn.get(j-1));
                b = (Integer)titles.get(listToReturn.get(j));
            }
            
            i++;
        }
        
        return listToReturn;
    }
  
    /**
    *Converts the number represented by value in an array of bytes to an int. For example, if the array is : ["1";"2";"3";"4"]=[49;50;51;52], the corresponding int will be "1234".
    *
    *@param bytes The array of bytes to convert
    *@return The converted number.
    */
    public static int charBytes2int(byte[] bytes){
        int i = bytes.length - 1;
        int ret = 0;
        byte val;
        int pow = 0;
        
        while(i >= 0){
            switch(bytes[i]){
                case 48:
                    val = 0;
                    break;
                case 49:
                    val = 1;
                    break;
                case 50:
                    val = 2;
                    break;
                case 51:
                    val = 3;
                    break;
                case 52:
                    val = 4;
                    break;
                case 53:
                    val = 5;
                    break;
                case 54:
                    val = 6;
                    break;
                case 55:
                    val = 7;
                    break;
                case 56:
                    val = 8;
                    break;
                case 57:
                    val = 9;
                    break;
                default:
                    val = -1;
            }

            if(val >= 0){
                ret += val*Math.pow(10, pow);
                pow++;
            }
            i--;
        }
        return ret;
    }
}
