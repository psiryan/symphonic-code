/*
 * GenericNW.java
 *
 * Created on 6 juin 2007, 21:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.system.sony.nw;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.io.*;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.*;
import org.farng.mp3.id3.ID3v2_4;
import org.farng.mp3.id3.ID3v2_4Frame;
import org.naurd.media.jsymphonic.manager.JSymphonic;
import org.naurd.media.jsymphonic.system.SystemListener;
import org.naurd.media.jsymphonic.system.sony.nw.OmaDataBase;
import org.naurd.media.jsymphonic.title.Ea3Tag;
import org.naurd.media.jsymphonic.title.Oma;
import org.naurd.media.jsymphonic.title.Title;

/**
 *An instance of the GenericNW class describes a Sony devices, including a reference to the real device, his name, his description, the list of tracks, his Sony DataBase, his icon and a listener.
 *
 *This class includes methods for viewing, adding, deleting tracks from the device.
 *
 *@version 06.06.2007
 *@author Nicolas Cardoso
 */
public class GenericNW implements org.naurd.media.jsymphonic.system.SystemFile {
    private File sourceDir = null;
    private String name = "default";
    private String description = "";
    private OmaDataBase dataBase;
    private Vector<Title> titlesToAdd = new Vector<Title>();
    private Vector<Title> titlesToRemove = new Vector<Title>();
    private Vector<Title> titlesToExport = new Vector<Title>();
    private SystemListener listener = null;
    private javax.swing.ImageIcon icon;
    private long usableSpace = 0; //in octet
    private long totalSpace = 0; //in octet
    private long titleToAddSpace = 0; //in octet
    private long titleToRemoveSpace = 0; //in octet
    private JSymphonic jsymphonic; //Instance of the GUI
    private byte[] key = {0,0,0,0}; //Key of the player
    
    /* Constructors */
    /**
    *Allows to create an instance of GenericNW from an existing device.
    *
    *@param omgaudioDir The directory OMGAUDIO from the device.
    *@param sourceName The name of the source, i.e. the name of the device.
    *@param sourceDesc The description of the source.
    *@param sourceGeneration The generation of the source (value can be 1, 2, 3 or 4 but only 4 is supported for the moment).
    *@param sourceIcon The icon of the source.
    */
    public GenericNW(File omgaudioDir, String sourceName, String sourceDesc, int sourceGeneration, javax.swing.ImageIcon sourceIcon, JSymphonic jsymphonic){
        name = sourceName;
        description = sourceDesc;
        if( !omgaudioDir.exists() ) {
            System.err.println("Invalid OMGAUDIO directory.\nExiting program.");
            System.exit(-1);
        }
        sourceDir = omgaudioDir;
        this.icon = sourceIcon;
        this.jsymphonic = jsymphonic;
        
        // Create the database (for the good generation)
        switch(sourceGeneration) {
//            case 1 :
//                database = new OmaDataBaseGen1(omgaudioDir);
//                break;
//            case 2 :
//                database = new OmaDataBaseGen2(omgaudioDir);
//                break;
//            case 3 :
//                database = new OmaDataBaseGen3(omgaudioDir);
//                break;
            case 4 :
                dataBase = new OmaDataBaseGen4(omgaudioDir);
                break;
            default :
                throw new IllegalArgumentException("Invalid generation");
        }
        
        // Load the key if the player is protected
        loadKey(omgaudioDir.getParent());
        
        // Fill in the title's list
        loadTitlesFromDevice(jsymphonic);
        
        // Update space
        usableSpace = sourceDir.getUsableSpace();
        totalSpace = sourceDir.getTotalSpace();
    }
    
     /*
     *This constructor is  needed to be able to instanciate the class
     *from a call Class.forName()
     */
    public GenericNW() {
        
    }
    
 
    
    /* Methods */
    public String getSpaceLeftInText() {
        long tmpTotalSpace = totalSpace;
        long usedSpace = totalSpace - usableSpace + titleToAddSpace - titleToRemoveSpace; // Calculate used space
        int n=0;
        String text;
        
        
        while((usedSpace/(1024) > 1024)) { // Convert used space in a suitable unit
            usedSpace = usedSpace/1024;
            n++;
        }
        
        if(n==0) {
            text = usedSpace/1024 + " Ko" ; // Memorised the used space
        }
        else {
            text = usedSpace/1024 + "."+ (usedSpace%1024)/10 + " " ; // Memorised the used space
            if(n == 1) {text += "Mo";} // Put the right unit
            if(n == 2) {text += "Go";}
            if(n == 3) {text += "To";}
        }
        text += " / " ;
        
        n=0;
        while((tmpTotalSpace/(1024) > 1024)) { // Convert used space in a suitable unit
            tmpTotalSpace = tmpTotalSpace/1024;
            n++;
        }
        
        if(n==0) {
            text += tmpTotalSpace/1024 + " Ko" ; // Memorised the used space
        }
        else {
            text += tmpTotalSpace/1024 + "."+ (tmpTotalSpace%1024)/10 + " " ; // Memorised the used space
            if(n == 1) {text += "Mo";} // Put the right unit
            if(n == 2) {text += "Go";}
            if(n == 3) {text += "To";}
        }
        
        return text;
    }    

    public int getSpaceLeftInRatio() {
        long usedSpace = totalSpace - usableSpace + titleToAddSpace - titleToRemoveSpace; // Calculate used space
        return (int)((usedSpace)*1000.0/totalSpace);
    }
    
    /**
    *Obtains the tracklist into the device.
    *
    *@return The tracklist.
    */
    public Title[] getTitles(){
        //Get the titles in a JSymphonic Map
        SymphonicMap titles = dataBase.getTitles();
        
        //Get the number of titles
        int count = titles.size();
        //Create the vector to return
        Title[] titlesToReturn = new org.naurd.media.jsymphonic.title.Title[count];
        
        // Get all the title IDs from titles (= all the titles)
        Set tmpTitles = titles.keySet();
        
        //Convert the set to an array
        Iterator it = tmpTitles.iterator();
        int i = 0;
        while( it.hasNext() ) {
            titlesToReturn[i] = (Title)it.next();
            i++;
        }

        return titlesToReturn;    
    }
    
    
    public SymphonicMap getTitlesInMap(){
        // Get the titles from the actual database
        SymphonicMap titles = dataBase.getTitles();
        
        // Remove deleted titles
        Iterator itRemovedTitles = titlesToRemove.iterator();
        while(itRemovedTitles.hasNext()) {
            titles.remove(itRemovedTitles.next());
        }
        
        // Adding new titles
        Iterator itAddeddTitles = titlesToAdd.iterator();
        int indexValue;
        if( titles.size() > 0) {
            indexValue = titles.maxValue(); // To keep SymphonicMap structure, we must add an index value associated with each titles, as these value won't be used anymore, we just add values from the highest to be sur to not have twice the same
        }
        else {
            indexValue = 1;
        }
        while(itAddeddTitles.hasNext()) {
            titles.put(itAddeddTitles.next(),++indexValue);
        }
        
        return titles;
    }
    
    /**
    *Applies tracklist changes from the GUI to the device. It transfers new tracks and deletes old tracks.
    */
    public void writeTitles(JSymphonic jsymphonic){
        this.jsymphonic = jsymphonic;
        
        Thread t = new Thread(){
            public void run(){
                try{
                    writeTitlesInTread();
                } catch(Exception e){}
            }
        };
        t.setPriority(t.MIN_PRIORITY);
        t.start();
        
        t = null;
    }
   
    /**
    *Applies tracklist changes from the GUI to the device. It transfers new tracks and deletes old tracks.
    */
    public void writeTitles(){
        // This method should not be used, use "writeTitles(JSymphonic jsymphonic)" instead
/*        Thread t = new Thread(){
            public void run(){
                try{
                    writeTitlesInTread();
                } catch(Exception e){}
            }
        };
        t.setPriority(t.MIN_PRIORITY);
        t.start();
        
        
        // Wait for transfert to be done
        try {
            wait();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
        //Once completed, reload everything...
        refreshTitles();
        t = null;*/
    }
    
    /**
    *Creates a new thread to apply changes to the devices.
    */
    private void writeTitlesInTread() {
        int titlesNumber;
        // Initialize components
        jsymphonic.initializeTransfer();

        // Calculate the increment for the progress bar un the transfertDialog
        int exportWeight = 20;
        int rmWeight = 2;
        int addWeight = 20;
        int dbWeight = 1;
        
        Double n = 100.0/(titlesToExport.size()*exportWeight + titlesToRemove.size()*rmWeight + titlesToAdd.size()*addWeight + 10*dbWeight); // 10 stands for the number of config files in the database which must be written
        
        //Export files fisrt
        if(JSymphonic.debug) {System.out.println("Exportation started.");}
        jsymphonic.setTransfertStatus(1, 0);
        titlesNumber = copyMp3FromToDevice(titlesToExport, n*exportWeight);
        jsymphonic.setTransfertStatus(2, titlesNumber);
        
        //Delete files
        if(JSymphonic.debug) {System.out.println("Deleting files started.");}
        jsymphonic.setTransfertStatus(3, 0);
        titlesNumber = deleteFilesFromDevice(titlesToRemove, n*rmWeight);
        jsymphonic.setTransfertStatus(4, titlesNumber);
        
        //Add new titles
        if(JSymphonic.debug) {System.out.println("Adding files stated.");}
        jsymphonic.setTransfertStatus(5, 0);
        titlesNumber = addFilesToDevice(titlesToAdd, n*addWeight);
        jsymphonic.setTransfertStatus(6, titlesNumber);

        //Update database
        if(JSymphonic.debug) {System.out.println("Updating database started.");}
        jsymphonic.setTransfertStatus(7, 0);
        dataBase.update(); 
        
        //Write the database to the config file
        if(JSymphonic.debug) {System.out.println("Updating database started (2).");}
        dataBase.write(jsymphonic, n*dbWeight);
        jsymphonic.setTransfertStatus(8, 0);
        
        //The transfert is completed
        refreshTitles(); //reload everything...
        jsymphonic.setTransfertStatus(9, 0);
    }
        
    /**
    *Deletes track from the device. Changes are only applied when "writeTitles()" is called.
    *
    *@param t The title to remove.
    */
    public void removeTitles(Title t){
        if(titlesToAdd.contains(t)) {
            titlesToAdd.remove(t);
            titleToAddSpace -= t.size();
        }
        else {
            t.Status = t.Status.TOREMOVE;
            titlesToRemove.add(t);
            titleToRemoveSpace += t.size();
        }
    }
    
    /**
    *Adds track to the device. Changes are only applied when "writeTitles()" is called.
    *
    *@param t The title to add.
    */
    public int addTitle(Title t){
        long titleSpace = t.size();
        if(titleToAddSpace+titleSpace-titleToRemoveSpace >= usableSpace) { 
            // Disk full
            return -1;
        }
        else {
            t.Status = t.Status.TOADD;
        //        if(!isCompatible(t)){
        //            t = makeCompatible(t);
        //        }
            titlesToAdd.add(t);
            titleToAddSpace += titleSpace;
            
            return 0;
        }
    }

    public void exportTitle(Title t){
        titlesToExport.add(t);
    }
    
    /**
    *Replaces track in the device. Changes are only applied when "writeTitles()" is called.
    *
    *@param t The title to replace.
    */
    public void replaceTitle(Title oldTitle,Title newTitle){
        removeTitles(oldTitle);
        addTitle(newTitle);
    }
    
    /**
    *Obtains the name of the source.
    *
    *@return The name of the source.
    */    
    public String getSourceName(){
        return name;
    }
    
    /**
    *Sets the name of the source.
    *
    *@param n The name of the source.
    */    
    public void setSourceName(String n){
        name=n;
    }
    
    /**
    *Obtains the description of the source.
    *
    *@return The description of the source.
    */
    public String getSourceDescription(){
        return description;
    }
    
    /**
    *Sets the description of the source.
    *
    *@param d The description of the source.
    */    
    public void setSourceDescription(String d){
        description = d;
    }
    
    /**
    *Obtains the source (in URL).
    *
    *@return The source.
    */    
    public java.net.URL getSourceURL(){
        try{
            return sourceDir.toURI().toURL();
        } catch(Exception e){
            return null;
        }
    }
    
    /**
    *Obtains the source (in String).
    *
    *@return The source.
    */    
    public File getSource(){
        return sourceDir;
    }
    
    /**
    *Sets the source.
    *
    *@param source The source.
    */    
    public void setSource(String source){
        sourceDir = new File(source);
    }
    
    /**
    *Obtains the icon related to the device.
    *
    *@return The icon.
    */
    public javax.swing.ImageIcon getIcon(){
        return icon;
    }


    /**
    *Ignores tracks changes in the GUI and refreshes the GUI with the content of the device.
    */ 
    public void refreshTitles(){
        titlesToAdd.clear(); //Empty list of title to add
        titlesToRemove.clear(); //Empty list of title to remove
        dataBase.clear();
        
        // Update space
        usableSpace = sourceDir.getUsableSpace();
        totalSpace = sourceDir.getTotalSpace();
        titleToRemoveSpace = 0;
        titleToAddSpace = 0;
        
        loadTitlesFromDevice(jsymphonic); // Fill in the title's list
    }
    
    /**
    *Sets the listener.
    *
    *@param l The listener.
    */    
    public void setListener(SystemListener l){
        listener = l;
    }
    

    /**
    *Makes a title compatible with the device.
    *
    *@param t The title to be transcode.
    *
    *@return The title transcoded to be compatible with the device.
    */
    private Title makeCompatible(Title t) {
//TODO        
        return null;
    }

    /**
    *Tells if a title is compatible with the device.
    *
    *@param t The title which must be tested.
    *
    *@return True if the title is compatible and false overwise.
    */
    private boolean isCompatible(Title t) {
        String extention = t.getExtention().toLowerCase();
        if(extention != ".mp3" || extention != ".wma"){
                return false;
        }
        
        switch (t.getBitRate()) { 
            case 48:
            case 96:
            case 128:
            case 160:
            case 192:
            case 256:
            case 312:
                return true;
            default:
                return false;
        }
    }
    
    /**
    *Scans OMGAUDIO folder and fill in the title's list with all titles found.
    */
    private void loadTitlesFromDevice(JSymphonic jsymphonic){
        int titleId = 0;
        int numberOfTitles = 0 ;
        Set totalTitlesList = new HashSet();
        //Create vector of directories in OMGAUDIO
        java.io.File[] dirList = sourceDir.listFiles();
        
        //First, search all the directories contening oma files
        for (int i = 0; i < dirList.length; i++){
            //Is it's a directory starting with "10F", it contains titles
            if (dirList[i].isDirectory() && (dirList[i].getName().toLowerCase().startsWith("10f"))){
                java.io.File[] titlesList = dirList[i].listFiles(); //Create vector of files in the directory

                numberOfTitles += titlesList.length;
                totalTitlesList.add(titlesList);
            }
        }
        
        // Calculate the increment to use in loading progress bar in GUI
        if(numberOfTitles == 0) { //Avoid division by 0
            numberOfTitles = 100;
        }
        double increment = 100.0/numberOfTitles;
        double progressBarValue = 0;
        // Add the titles to the database
        Iterator it = totalTitlesList.iterator();
        while (it.hasNext()) {
            java.io.File[] titlesList = (File[])it.next();
            
            //For each file
            for(int j = 0; j < titlesList.length; j++){
                //Create a title from the file
                Title t = new Oma(titlesList[j]);
                //Determine the title ID to use in the database
                titleId = Integer.parseInt(titlesList[j].getName().toLowerCase().replaceAll("10*(.*)\\.oma","$1"), 16);

                //Add the title and the titleId in the database if not null
                if ( t != null && titleId != 0 ){
                    dataBase.addTitleWithTitleId(t, titleId);
                    
                    // Update loading progress bar
                    progressBarValue += increment;
                    jsymphonic.displayLoadingProgress((int)progressBarValue);
                }
            }
        }
        
        // Update loading progress bar
        jsymphonic.displayLoadingProgress(0);
        
    }

    /**
    *Scans OMGAUDIO folder and fill in the title's list with all titles found. This method DON'T modify config files.
    *
    *@param titlesToRemove The list of the title to remove from the device.
    */
    private int deleteFilesFromDevice(java.util.Vector<Title> titlesToRemove, Double increment) {
        int titleId, dirNumber, titlesDeleted = 0;
        String dirName, fileName;
        Title titleToRemove;
        File fileToRemove;
        Iterator it = titlesToRemove.iterator();
        
        while( it.hasNext()) { //For each title
            titleToRemove = (Title)it.next();
            
            /* OLD VERSION NOT WORKING DUE TO IMPOSSIBLITY TO GET INDEX FROM SYMPHONICMAP TITLES IN THE DATABASE
            // Get the title ID of the title from the database
            titleId = dataBase.getTitleId(titleToRemove);
            
            // To know in which directory the title is stored, need to do some calculus and treatment
            dirNumber = (titleId / 255) + 1;
            dirName = Integer.toString(dirNumber);
            if( dirNumber < 10 ) { //If dirNumber is less than 10, a zero must be added to the name of the directory
                dirName = "0" + dirName;
            }
            dirName = "10f" + dirName;
            
            // Create the name of the file to delete
            fileName = Integer.toHexString(titleId); //First convert to hex
            while( fileName.length() < 7 ) {
                fileName = "0" + fileName; //Add zeros to have 7 characters
            }
            fileName = "1" + fileName + ".oma"; //Add prefix and suffix
            
            // Delete file form device
            fileToRemove = new File(sourceDir.getPath() + "/" + dirName + "/" + fileName) ;
            fileToRemove.delete();*/
            
            // Update dataBase
            dataBase.removeTitle(titleToRemove);
            
            // Check the title to be an .oma
            if((!titleToRemove.getSourceFile().getPath().endsWith("oma")) && (!titleToRemove.getSourceFile().getPath().endsWith("OMA")) ) {
                System.err.println("This error occurs to prevent a file to be erased. If you see this message, please contact the developers in the forum 'https://sourceforge.net/forum/forum.php?forum_id=747001'.");
                return titlesDeleted;
            }
            
            // Delete file form device
            if(JSymphonic.debug) {System.out.println("Files removed :"+titleToRemove.getSourceFile().getPath() );}
            titleToRemove.getSourceFile().delete();

            // Update progress bar in GUI
            jsymphonic.increaseTransfertProgressBar(increment);
            // Count number of exported titles
            titlesDeleted ++;
        }
        
        return titlesDeleted;
    }
    
    /**
    *Transfers the list of files to the device. This method transcode files (if needed) and "turn" them to ".OMA" files. This method DON'T modify config files.
    *
    *@param titlesToAdd The list of the title to add to the device.
    */
    private int addFilesToDevice(java.util.Vector<Title> titlesToAdd, Double increment) {
        int freeTitleId, dirNumber, titlesAdded = 0;;
        String dirName, fileName;
        File omaFile, directory;
        Title titleToAdd;
        FileChannel in = null; // channel used by the file copy
        FileChannel ea3Header = null; // channel used by the file copy
        FileChannel out = null; // channel used by the file copy
        Iterator it = titlesToAdd.iterator();
        
        while( it.hasNext() ) { //for each title
            // Get the title to add
            titleToAdd = (Title)it.next();

            // Create the name of the file on the device and the folder in which it will be stored
            freeTitleId = dataBase.getFreeTitleId(); // Get a free title ID
            
            dirNumber = (freeTitleId / 255); // Build the name of the directory
            dirName = Integer.toString(dirNumber);
            if( dirNumber < 10 ) { //If dirNumber is less than 10, a zero must be added to the name of the directory
                dirName = "0" + dirName;
            }
            dirName = "10F" + dirName;
            
            directory = new File (sourceDir + "/" + dirName);
            if( !directory.exists() ) {
                File directoryLower = new File (sourceDir + "/" + dirName.toLowerCase()); // Test directory in lower case for linux
                
                if( !directoryLower.exists() ) {
                    directory.mkdir(); // If the directory don't exist both in lower and upper case, create it (default is uppercase)
                }
                else { // If the directory exist in lower, change the variable dirName to lower
                    dirName = dirName.toLowerCase();
                }
                
            }

            
            fileName = Integer.toHexString(freeTitleId).toUpperCase(); // Build the name of the file
            while( fileName.length() < 7 ) {
                fileName = "0" + fileName; //Add zeros to have 7 characters
            }
            String index = "1" + fileName; // Save the index
            fileName = index + ".OMA"; //Add prefix and suffix
            
            // New path built mustn't represent an existing file
            omaFile = new File(sourceDir + "/" + dirName + "/" + fileName);
            if( omaFile.exists() ) {
                System.out.println("Meet an non-free file while adding oma files. Skip this file.");
                continue;
            }
            
            if( titleToAdd.Status.equals(org.naurd.media.jsymphonic.title.Title.TitleStatus.TOTRANSCODE) ) {
                // The file must be transcode
                //TODO
                
            }
            else { // The file can be simply copied
                // Start by adding the EA3 tag
                if(JSymphonic.debug) {System.out.println("Add file, source:'" + titleToAdd.getSourceFile().getPath() + "', destination:'"+sourceDir + "/" + dirName + "/" + fileName+"'.");}
                File newOmaFile = new File(sourceDir + "/" + dirName + "/" + fileName);
                File tempHeaderEA3 = new File("tempHeaderEA3.tmp");
                Ea3Tag ea3tag = new Ea3Tag(titleToAdd); //Create tag
                
                // If a key is set, EA3 tag is different
                if((key[0] != 0) || (key[1] != 0) || (key[2] != 0) || (key[3] != 0) ){
                    ea3tag.write(tempHeaderEA3, Ea3Tag.KEY); //Write tag (with key) to file
                }
                else {
                    ea3tag.write(tempHeaderEA3, Ea3Tag.NOKEY); //Write tag (without key) to file
                }

                try {
                    // I look for the position where the music data begins (avoiding the MP3 IDE tag)
                    MP3File mp3File = new MP3File(titleToAdd.getSourceURL().getPath());
                    long position = mp3File.getMp3StartByte(titleToAdd.getSourceFile());
                    
                    // If a key is set, MP3 should be crypted
                    if((key[0] != 0) || (key[1] != 0) || (key[2] != 0) || (key[3] != 0) ){
                        // Compute the keyOma, i.e. the key for this precise oma file
                        //CAUTION, the key[i] values can be considered like negative value, to prevent it:
                        int key0 = key[0];
                        int key1 = key[1];
                        int key2 = key[2];
                        int key3 = key[3];
                        if(key0 < 0) { key0 = key0 + 256;}
                        if(key1 < 0) { key1 = key1 + 256;}
                        if(key2 < 0) { key2 = key2 + 256;}
                        if(key3 < 0) { key3 = key3 + 256;}
                        
                        int intKeyDvID = key0*0x1000000 + key1*0x10000 + key2*0x100 + key3; // First I turn the key of the player into a int
                        long intKeyOma = (0x2465 + freeTitleId * 0x5296E435L)^intKeyDvID; // I compute the keyOma (according the the index of the oma file)

                        // Then, I turn the intKeyOma back into Bytes            
                        Long keyOmaByte1 = intKeyOma/(16777216); // Compute byte 1
                        Long keyOmaByte2 = (intKeyOma - keyOmaByte1*16777216)/(65536);// Compute byte 2
                        Long keyOmaByte3 = (intKeyOma - keyOmaByte1*16777216 - keyOmaByte2*65536)/(256);// Compute byte 3
                        Long keyOmaByte4 = (intKeyOma - keyOmaByte1*16777216 - keyOmaByte2*65536 - keyOmaByte3*256);// Compute byte 4
                        // Bytes are representing as integer between -128 and 127
                        keyOmaByte1 = keyOmaByte1 & 0xFF ; //We can have more than one byte
                        if(keyOmaByte1 > 127) { keyOmaByte1 = keyOmaByte1 - 256;} //If the byte is greater than 127, I've got to use a signed integer
                        if(keyOmaByte2 > 127) { keyOmaByte2 = keyOmaByte2 - 256;}
                        if(keyOmaByte3 > 127) { keyOmaByte3 = keyOmaByte3 - 256;}
                        if(keyOmaByte4 > 127) { keyOmaByte4 = keyOmaByte4 - 256;}
                        // Finally I create the keyOma from the four bytes computed before
                        byte[] keyOma = {new Byte(keyOmaByte1.toString()), new Byte(keyOmaByte2.toString()), new Byte(keyOmaByte3.toString()), new Byte(keyOmaByte4.toString())}; //Key of the particular oma title
                        
                        // Create stream for the copy
                        FileInputStream source = new FileInputStream(titleToAdd.getSourceFile());
                        FileOutputStream dest = new FileOutputStream(newOmaFile);
                        FileInputStream ea3Header2 = new FileInputStream(tempHeaderEA3);
                        // Some variables
                        byte[] buffer = new byte[4096];
                        int nbRead;
                        int count = 0;
                        
                          
                        // Skip ID3v2 tag in the MP3 file
                        source.skip(position);
                        
                        // Write the EA3 header
                        while( (nbRead = ea3Header2.read(buffer)) > 0 ) {
                            dest.write(buffer,0,nbRead); 
                        }
                        
                        // Read buffer from the source, crypt and write to the destination
                        while( (nbRead = source.read(buffer)) > 0 ) { // Read from the source
                            for(int i=0; i < nbRead; i++) {
                                dest.write(buffer[i]^keyOma[count % 4]); // Write each bytes after applying the mask (from the key)
                                count++;
                            }
                        }
                    }
                    else {
                        // Create channel to copy data
                        in = new FileInputStream(titleToAdd.getSourceFile()).getChannel();
                        ea3Header = new FileInputStream(tempHeaderEA3).getChannel();
                        out = new FileOutputStream(newOmaFile).getChannel();

                        // Copy the header (EA3 tag)
                        ea3Header.transferTo(0, ea3Header.size(), out);

                        // Else, we can juste copy the file as it is.
                        in.transferTo(position, in.size() - position, out);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                } finally { // Close
                    if(in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {}
                    }
                    if(out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {}
                   }
                    if(ea3Header != null) {
                        try {
                            ea3Header.close();
                        } catch (IOException e) {}
                   }
                   
                    // Delete temporary file
                    try{ //Create new file
                        if( tempHeaderEA3.exists() ) {
                            tempHeaderEA3.delete(); // Delete file if it exist
                        }
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                } // End of copy
            }

            // Update dataBase
            dataBase.addTitleWithTitleId(titleToAdd, freeTitleId);
            // Update progress bar in GUI
            jsymphonic.increaseTransfertProgressBar(increment);
            // Count number of exported titles
            titlesAdded ++;
        }
        
        return titlesAdded;
    }

    private int copyMp3FromToDevice(java.util.Vector<Title> titlesToCopy, Double increment) {
        String artist, album, titleName;
        int trackNumber, titlesExported = 0;
        File directoryToCopy, newMp3;
        Title titleToCopy;
        FileChannel in = null; // channel used by the file copy
        FileChannel out = null; // channel used by the file copy
        Iterator it = titlesToCopy.iterator();
        
        if(jsymphonic.getExportPath().compareTo("invalid path") == 0) {
            System.err.println("Can't export titles because to path is set to indicates where the exported titles should be saved.");
            return 0;
        }
        
        
        while( it.hasNext() ) { //for each title
            // Get the title to add
            titleToCopy = (Title)it.next();
            
            //// Create the file name and his path
            // First, get title information
            artist = titleToCopy.getArtist();
            album = titleToCopy.getAlbum();
            titleName = titleToCopy.getTitle();
            trackNumber = titleToCopy.getTitleNumber();
                    
            // Check validity
            if(artist.length() == 0) {
                artist = "unknown artist";
            }
            if(album.length() == 0) {
                album = "unknown album";
            }
            if(titleName.length() == 0) {
                titleName = "unknown title";
            }
            
            // Build directory to copy name (artist)
            directoryToCopy = new File (jsymphonic.getExportPath() + "/" + artist);
            
            // Create directory to copy is it's not existing
            if( !directoryToCopy.exists() ) {
                directoryToCopy.mkdir(); // If the directory don't exist both in lower and upper case, create it (default is uppercase)
            }

            // Build directory to copy name (artist/album)
            directoryToCopy = new File (jsymphonic.getExportPath() + "/" + artist + "/" + album);
            
            // Create directory to copy is it's not existing
            if( !directoryToCopy.exists() ) {
                directoryToCopy.mkdir(); // If the directory don't exist both in lower and upper case, create it (default is uppercase)
            }
            
            // Build mp3 to copy name
            if(trackNumber != 0) {
                if(trackNumber < 10) {
                    newMp3 = new File (jsymphonic.getExportPath() + "/" + artist + "/" + album + "/0" + trackNumber + "-" + titleName + ".mp3");
                }
                else {
                    newMp3 = new File (jsymphonic.getExportPath() + "/" + artist + "/" + album + "/" + trackNumber + "-" + titleName + ".mp3");
                }
            }
            else {
                newMp3 = new File (jsymphonic.getExportPath() + "/" + artist + "/" + album + "/" + titleName + ".mp3");
            }
            
            // Check if title doesn't already exist at this adress
            if(newMp3.exists()) {
                System.err.println("Can't export the MP3 file '" + newMp3.getName() + "', it already exist at the adress '" + newMp3.getAbsolutePath() + "'.");
                continue;
            }
            else {
                try {
                    newMp3.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            try {
                
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // If a key is set, MP3 should be crypted
                if((key[0] != 0) || (key[1] != 0) || (key[2] != 0) || (key[3] != 0) ){
                    // Compute the keyOma, i.e. the key for this precise oma file
                    // First, we need to know the titleId used to crypt the file, it correspond to the number of the oma file
                    String titleIdString = titleToCopy.getSourceFile().getName();
                    titleIdString = titleIdString.replace(".oma",""); // remove the extension
                    titleIdString = titleIdString.replace(".OMA",""); // remove the extension
                    titleIdString = titleIdString.replaceFirst("1",""); // remove the 1 begining all the .oma file
                    int titleId = Integer.parseInt(titleIdString);
                    
                    
                    //CAUTION, the key[i] values can be considered like negative value, to prevent it:
                    int key0 = key[0];
                    int key1 = key[1];
                    int key2 = key[2];
                    int key3 = key[3];
                    if(key0 < 0) { key0 = key0 + 256;}
                    if(key1 < 0) { key1 = key1 + 256;}
                    if(key2 < 0) { key2 = key2 + 256;}
                    if(key3 < 0) { key3 = key3 + 256;}

                    int intKeyDvID = key0*0x1000000 + key1*0x10000 + key2*0x100 + key3; // First I turn the key of the player into a int
                    long intKeyOma = (0x2465 + titleId * 0x5296E435L)^intKeyDvID; // I compute the keyOma (according the the index of the oma file)

                    // Then, I turn the intKeyOma back into Bytes            
                    Long keyOmaByte1 = intKeyOma/(16777216); // Compute byte 1
                    Long keyOmaByte2 = (intKeyOma - keyOmaByte1*16777216)/(65536);// Compute byte 2
                    Long keyOmaByte3 = (intKeyOma - keyOmaByte1*16777216 - keyOmaByte2*65536)/(256);// Compute byte 3
                    Long keyOmaByte4 = (intKeyOma - keyOmaByte1*16777216 - keyOmaByte2*65536 - keyOmaByte3*256);// Compute byte 4
                    // Bytes are representing as integer between -128 and 127
                    keyOmaByte1 = keyOmaByte1 & 0xFF ; //We can have more than one byte
                    if(keyOmaByte1 > 127) { keyOmaByte1 = keyOmaByte1 - 256;} //If the byte is greater than 127, I've got to use a signed integer
                    if(keyOmaByte2 > 127) { keyOmaByte2 = keyOmaByte2 - 256;}
                    if(keyOmaByte3 > 127) { keyOmaByte3 = keyOmaByte3 - 256;}
                    if(keyOmaByte4 > 127) { keyOmaByte4 = keyOmaByte4 - 256;}
                    // Finally I create the keyOma from the four bytes computed before
                    byte[] keyOma = {new Byte(keyOmaByte1.toString()), new Byte(keyOmaByte2.toString()), new Byte(keyOmaByte3.toString()), new Byte(keyOmaByte4.toString())}; //Key of the particular oma title

                    // Create stream for the copy
                    FileInputStream source = new FileInputStream(titleToCopy.getSourceFile());
                    FileOutputStream dest = new FileOutputStream(newMp3);
                    // Some variables
                    byte[] buffer = new byte[4096];
                    int nbRead;
                    int count = 0;


                    // Skip EA3 tag in the MP3 file
                    source.skip(3072); //EA3 tag is fix : 3072

                    // Read buffer from the source, crypt and write to the destination
                    while( (nbRead = source.read(buffer)) > 0 ) { // Read from the source
                        for(int i=0; i < nbRead; i++) {
                            dest.write(buffer[i]^keyOma[count % 4]); // Write each bytes after applying the mask (from the key)
                            count++;
                        }
                    }
                }
                else {
                    if(JSymphonic.debug) {System.out.println("File copy, source:'" + titleToCopy.getSourceFile().getPath() + "', destination:'"+newMp3.getPath()+"'.");}
                    
                    // Create channel to copy data
                    in = new FileInputStream(titleToCopy.getSourceFile()).getChannel();
                    out = new FileOutputStream(newMp3).getChannel();

                    // Copy from "in" to "out" avoiding EA3 tag
                    in.transferTo(3072, in.size() - 3072, out); //EA3 tag is fix : 3072
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally { // Close
                if(in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {}
                }
                if(out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {}
               }
            } // End of copy

            //// Add an ID3 tag to the new MP3 file
/* apparently, there is no need to add a tag because it's already existing, but it shouldn't, as we have remove it when adding the file to the device...
            // Create the tag :
            ID3v2_4 mp3Tag = new ID3v2_4();
            MP3File mp3File;
            AbstractID3v2Frame frame;
            AbstractID3v2FrameBody frameBody;
            
            // Set title
            frameBody = new FrameBodyTIT2((byte) 0, titleName);
            frame = new ID3v2_4Frame(frameBody);
            mp3Tag.setFrame(frame);

            // Set artist
            frameBody = new FrameBodyTPE1((byte) 0, artist);
            frame = new ID3v2_4Frame(frameBody);
            mp3Tag.setFrame(frame);
            
            // Set album
            frameBody = new FrameBodyTALB((byte) 0, album);
            frame = new ID3v2_4Frame(frameBody);
            mp3Tag.setFrame(frame);
            
            // Set genre
            frameBody = new FrameBodyTCON((byte) 0, titleToCopy.getGenre());
            frame = new ID3v2_4Frame(frameBody);
            mp3Tag.setFrame(frame);
    
            try {
                mp3File = new MP3File(newMp3);
                mp3File.setID3v2Tag(mp3Tag);
            } catch (TagException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }*/
            
            // Update progress bar in GUI
            jsymphonic.increaseTransfertProgressBar(increment);
            // Count number of exported titles
            titlesExported ++;
        }
        
        return titlesExported;
    }
    
    
    public long getTotalSpace(){
        return sourceDir.getTotalSpace();
    }
    
    public long getUsableSpace(){
        return sourceDir.getUsableSpace();
    }

    private void loadKey(String playerPath) {
        File testFile = new File(playerPath + "/DvID.dat");
        String dvid_dat = "";
        
        // Search the file containing the key at the root of the player, then in OMGAUDIO folder then in MP3FM folder
        if(testFile.exists()) {
            dvid_dat = playerPath + "/DvID.dat";
        }
        else {
            testFile = new File(playerPath + "/OMGAUDIO/DvID.dat");
            
            if(testFile.exists()) {
                dvid_dat = playerPath + "/OMGAUDIO/DvID.dat";
            }
            else {
                testFile = new File(playerPath + "/omgaudio/DvID.dat");

                if(testFile.exists()) {
                    dvid_dat = playerPath + "/omgaudio/DvID.dat";
                }
                else {
                    testFile = new File(playerPath + "/MP3FM/DvID.dat");

                    if(testFile.exists()) {
                        dvid_dat = playerPath + "/MP3FM/DvID.dat";
                    }
                    else {
                        testFile = new File(playerPath + "/mp3fm/DvID.dat");

                        if(testFile.exists()) {
                            dvid_dat = playerPath + "/mp3fm/DvID.dat";
                        }
                        else {
                            testFile = new File(playerPath + "/DvID.DAT");

                            if(testFile.exists()) {
                                dvid_dat = playerPath + "/DvID.DAT";
                            }
                            else {
                                testFile = new File(playerPath + "/MP3FM/DvID.DAT");

                                if(testFile.exists()) {
                                    dvid_dat = playerPath + "/MP3FM/DvID.DAT";
                                }
                                else {
                                    testFile = new File(playerPath + "/OMGAUDIO/DvID.DAT");

                                    if(testFile.exists()) {
                                        dvid_dat = playerPath + "/OMGAUDIO/DvID.DAT";
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // If a file has been found, I read the key
        if(!dvid_dat.isEmpty()) {
            try {
                // Open file in stream mode
                InputStream dvid_datStream = new File(dvid_dat).toURI().toURL().openStream();
                
                // Skip the first useless bytes
                dvid_datStream.skip(10);
                
                // Read the key
                dvid_datStream.read(key);
                
                // Close the stream
                dvid_datStream.close();
                
                // Debug info
                if(JSymphonic.debug) {System.out.println("A key has been read: "+ key[0] + " - "+ key[1] + " - "+ key[2] + " - "+ key[3] + " - ");}
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            
        }
    }
}
