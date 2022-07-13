/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *****
 * 
 * NWGeneric.java
 *
 * Created on 6 juin 2007, 21:17
 *
 */


package org.naurd.media.jsymphonic.system.sony.nw;

import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.farng.mp3.MP3File;
import org.kc7bfi.jflac.apps.Decoder;
import org.naurd.media.jsymphonic.system.SystemListener;
import org.naurd.media.jsymphonic.title.Ea3Tag;
import org.naurd.media.jsymphonic.title.Mp3;
import org.naurd.media.jsymphonic.title.Oma;
import org.naurd.media.jsymphonic.title.Tag;
import org.naurd.media.jsymphonic.title.Title;
import org.naurd.media.jsymphonic.toolBox.FFMpegToolBox;
import org.naurd.media.jsymphonic.toolBox.Java6ToolBox;
import org.naurd.media.jsymphonic.toolBox.JSymphonicMap;


    
    /**
 * An instance of the NWGeneric class describes a Sony devices, including a reference to the real device, his name, his description, the list of tracks, his Sony DataBase, his icon and a listener.
 * 
 * This class includes methods for viewing, adding, deleting tracks from the device.
 * 
 * @author nicolas_cardoso
 * @author Daniel Žalar - added events to ensure GUI independancy and logging support
 * @author Pedro Velasco - bugfix in export feature for protected players
 * @version 06.06.2007
 */
public abstract class NWGeneric implements org.naurd.media.jsymphonic.system.SystemFile {
/* FIELDS */
    // Device information
    protected File sourceDir = null;      // the root folder of the walkman (its mounting point)
    protected String name = "Walkman";    // name of the device
    protected String description = "";    // description of the device
    protected javax.swing.ImageIcon icon; // icon of the device
    protected OmaDataBase dataBase;       // database of the device
    protected long usableSpace = 0;       // left space on the device in octet
    protected long totalSpace = 0;        // used space on the device in octet
    protected int generation = 0;         // generation of the device
    
    // Configuration information
    private int TranscodeBitrate = 128; // default is 128kbps
    private boolean AlwaysTranscode = false;
    private String devicePath = null;
    private String tempPath = null;
    private String localPath = null;
    private String exportPath = null;
    
    // Titles on the device
    // titles to add are handeled in the corresponding monitor (titlesToAddMonitor) protected Vector<Title> titlesToAdd = new Vector<Title>();    // list of title to be added in the next transfer operation
    protected Vector<Title> titlesToRemove = new Vector<Title>(); // list of title to be removed in the next transfer operation
    protected Vector<Title> titlesToExport = new Vector<Title>(); // list of title to be exported in the next transfer operation
    protected long titleToAddSpace = 0;       //space of the titles to be added in octet
    protected long titleToRemoveSpace = 0;    //space left by the titles to be deleted in octet
    
    // Other
    private static Logger logger = Logger.getLogger("org.naurd.media.jsymphonic.system.sony.nw.NWGeneric");
    
    // Shared variables for the encode, decode, transfer thread are stored in monitors
    protected ThreadsStateMonitor threadsStateMonitor;    // monitor to handle the status of the threads
    protected TitlesToAddMonitor titlesToAddMonitor;      // monitor to hanlde the titles passing from a thread to another
    
    // TODO to check why there is a listener and a list of listeners
    protected SystemListener listener = null; // listener on the device
    protected ArrayList listeners;
    // TODO key should only be consider in protected player, these fiel should be removed
    protected long uintKey;//The key as an unsigned integer
    protected boolean gotkey = false; //Do we have a key for the player?
   
    
    
/* CONSTRUCTORS */
    /**
     * Allows to create an instance of NWGeneric from an existing device.
     * 
     * TODO to update
     * @param sourceName The name of the source, i.e. the name of the device.
     * @param sourceDesc The description of the source.
     * @param sourceIcon The icon of the source.
     * @param listener an object that implements NWGenericListener or null for no listener
     */ 
    public NWGeneric(String sourceName, String sourceDesc, javax.swing.ImageIcon sourceIcon, NWGenericListener listener, String exportPath){
        // Save the information
        name = sourceName; // the name of the device
        description = sourceDesc; // the description of the device
        icon = sourceIcon; // the icon of the device
        this.exportPath = exportPath;
        // Set up the listeners
        listeners = new ArrayList();
        if(listener != null){
            addGenericNWListener(listener);
        }
        
        // Instance monitor inner classes to handle shared variables
        threadsStateMonitor = new ThreadsStateMonitor();
        titlesToAddMonitor = new TitlesToAddMonitor();

        // Clean the vector for the first transfer
        titlesToAddMonitor.clearDecodeVector();
        titlesToAddMonitor.clearEncodeVector();
        titlesToAddMonitor.clearTransferVector();
    }
    
     /*
     *This constructor is  needed to be able to instanciate the class
     *from a call Class.forName()
     */
    public NWGeneric() {
        // Create the listener list
        listeners = new ArrayList();
    }
    
    
       public int getTranscodeBitrate() {
        return TranscodeBitrate;
    }

    public void setTranscodeBitrate(int TranscodeBitrate) {
        this.TranscodeBitrate = TranscodeBitrate;
    }

    public String getDevicePath() {
        return devicePath;
    }

    public void setDevicePath(String devicePath) {
        this.devicePath = devicePath;
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getExportPath() {
        return exportPath;
    }

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }
    
    
/* INNER MONITOR CLASSES TO MANAGE SHARED MEMORY */
    /**
     * Describes the state of the decode and encode threads. As they are waiting for each other, their status are stored in a monitor to avoid dead-locks.
     *
     *@author nicolas_cardoso
     */
    class ThreadsStateMonitor {
        private boolean decodeThreadIsRunning = true;
        private boolean encodeThreadIsRunning = true;
        
        
	/**
         *Initialized monitor (in case of several action done on the device (several apply))
         *
         *@author nicolas_cardoso
         */
	public synchronized void initialize() {
            // Put the stat of the thread in the default value
            decodeThreadIsRunning = true;
            encodeThreadIsRunning = true;
	}
        
        
	/**
         * Wait that decode thread has decode a new track or has finished
         *
         *@return true if decode thread is still runing (and still has work to do) or false if decode thread is over (and the thread calling this method shouldn't wait this thread anymore)
         *
         *@author nicolas_cardoso
         */
	public synchronized boolean waitForDecodeThread() {
            if(decodeThreadIsRunning){
                // If decode thread is still running, we should wait
                try {
                    wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                
                return true;
            }
            else{
                return false;
            }
	}

	/**
         * Wait that encode thread has encode a new track or has finished
         *
         *@return true if encode thread is still runing (and still has work to do) or false if encode thread is over (and the thread calling this method shouldn't wait this thread anymore)
         *
         *@author nicolas_cardoso
         */
	public synchronized boolean waitForEncodeThread() {
            if(encodeThreadIsRunning){
                // If decode thread is still running, we should wait
                try {
                    wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                
                // encode thread is still running, return true
                return true;
            }
            else {
                return false;
            }
            
	}
        
        /**
         * When decode thread has decode a title or when it has finished, it should notify encode thread through this method
         *
         *@param hasFinished true if decodeThread has finished or false if it has just decode one title
         *
         *@author nicolas_cardoso
         */
	public synchronized void notifyEncodeThread(boolean hasFinished) {
            if(hasFinished){
                decodeThreadIsRunning = false;
            }
            
            // Notify all the treads that this thread's state has changed
            notifyAll();
	}
        
        /**
         * When encode thread has encode a title or when it has finished, it should notify transfer thread through this method
         *
         *@param hasFinished true if encodeThread has finished or false if it has just encode one title
         *
         *@author nicolas_cardoso
         */
	public synchronized void notifyTransferThread(boolean hasFinished) {
            if(hasFinished){
                encodeThreadIsRunning = false;
            }
            
            // Notify all the treads that this thread's state has changed
            notifyAll();
	}
    }
    
    /**
     * Describes the lists of titles passing through the different threads. It's shared memory, they are stored in a monitor to avoid trouble.
     *
     *@author nicolas_cardoso
     */
    class TitlesToAddMonitor {
        private Vector<Title> titlesToDecode = new Vector<Title>();
        private Vector<Title> titlesToEncode = new Vector<Title>();
        private Vector<Title> titlesToTransfer = new Vector<Title>();
	 
	/**
         * Add title to the titles to decode
         *
         *@param title the title to add
         *
         *@author nicolas_cardoso
         */
	public synchronized void addToDecodeVector(Title title) {
	    titlesToDecode.add(title);
	}
        
	/**
         * Remove title from the titles to decode
         *
         *@param title the title to remove
         *
         *@author nicolas_cardoso
         */
	public synchronized void removeFromDecodeVector(Title title) {
	    titlesToDecode.remove(title);
	}
        
	/**
         * Clear the vector of titles to decode
         *
         *@author nicolas_cardoso
         */
	public synchronized void clearDecodeVector() {
	    titlesToDecode.clear();
	}

	/**
         * Get first element from the vector of titles to decode
         *
         *@return the first element of the vector
         *
         *@author nicolas_cardoso
         */
	public synchronized Title firstElementOfDecodeVector() {
	    return titlesToDecode.firstElement();
	}
        
	/**
         * Tell if the vector of titles to decode is empty
         *
         *@return true is empty, false otherwise
         *
         *@author nicolas_cardoso
         */
	public synchronized boolean isEmptyDecodeVector() {
	    return titlesToDecode.isEmpty();
	}

    	/**
         * Give the number of element in the vector
         *
         *@return the number of element
         *
         *@author nicolas_cardoso
         */
	public synchronized int sizeOfDecodeVector() {
	    return titlesToDecode.size();
	}

        /**
         * Add title to the titles to encode
         *
         *@param title the title to add
         *
         *@author nicolas_cardoso
         */
	public synchronized void addToEncodeVector(Title title) {
	    titlesToEncode.add(title);
	}
        
	/**
         * Remove title from the titles to encode
         *
         *@param title the title to remove
         *
         *@author nicolas_cardoso
         */
	public synchronized void removeFromEncodeVector(Title title) {
	    titlesToEncode.remove(title);
	}
        
	/**
         * Clear the vector of titles to encode
         *
         *@author nicolas_cardoso
         */
	public synchronized void clearEncodeVector() {
	    titlesToEncode.clear();
	}

	/**
         * Get first element from the vector of titles to encode
         *
         *@return the first element of the vector
         *
         *@author nicolas_cardoso
         */
	public synchronized Title firstElementOfEncodeVector() {
	    return titlesToEncode.firstElement();
	}
        
	/**
         * Tell if the vector of titles to encode is empty
         *
         *@return true is empty, false otherwise
         *
         *@author nicolas_cardoso
         */
	public synchronized boolean isEmptyEncodeVector() {
	    return titlesToEncode.isEmpty();
	}

    	/**
         * Give the number of element in the vector
         *
         *@return the number of element
         *
         *@author nicolas_cardoso
         */
	public synchronized int sizeOfEncodeVector() {
	    return titlesToEncode.size();
	}

        /**
         * Add title to the titles to transfer
         *
         *@param title the title to add
         *
         *@author nicolas_cardoso
         */
	public synchronized void addToTransferVector(Title title) {
	    titlesToTransfer.add(title);
	}
        
	/**
         * Remove title from the titles to transfer
         *
         *@param title the title to remove
         *
         *@author nicolas_cardoso
         */
	public synchronized void removeFromTransferVector(Title title) {
	    titlesToTransfer.remove(title);
	}
        
	/**
         * Clear the vector of titles to transfer
         *
         *@author nicolas_cardoso
         */
	public synchronized void clearTransferVector() {
	    titlesToTransfer.clear();
	}

	/**
         * Get first element from the vector of titles to transfer
         *
         *@return the first element of the vector
         *
         *@author nicolas_cardoso
         */
	public synchronized Title firstElementOfTransferVector() {
	    return titlesToTransfer.firstElement();
	}
        
	/**
         * Tell if the vector of titles to transfer is empty
         *
         *@return true is empty, false otherwise
         *
         *@author nicolas_cardoso
         */
	public synchronized boolean isEmptyTransferVector() {
	    return titlesToTransfer.isEmpty();
	}

    	/**
         * Give the number of element in the vector
         *
         *@return the number of element
         *
         *@author nicolas_cardoso
         */
	public synchronized int sizeOfTransferVector() {
	    return titlesToTransfer.size();
	}

    	/**
         * Tell if one of the vector in the monitor contains a given title.
         *
         *@param t the title which we want to know if it is contained in the monitor's vectors.
         *@return true if the title exists in one of the vectors, false otherwise
         *
         *@author nicolas_cardoso
         */
    private boolean contains(Title t) {
        if(titlesToDecode.contains(t)) return true;
        if(titlesToEncode.contains(t)) return true;
        if(titlesToTransfer.contains(t)) return true;
        return false;
    }

    	/**
         * Remove a title from the vector in the monitor which contains it.
         *
         *@param t the title to remove
         *
         *@author nicolas_cardoso
         */
    private void remove(Title t) {
        if(titlesToDecode.contains(t)) titlesToDecode.remove(t);
        if(titlesToEncode.contains(t)) titlesToEncode.remove(t);
        if(titlesToTransfer.contains(t)) titlesToTransfer.remove(t);
    }

	/**
         * Give the number of titles in the monitor (titles to decode + titles to encode + title to transfer)
         *
         *@return number of title in the monitor
         *
         *@author nicolas_cardoso
         */
        private synchronized int size() {
            return titlesToDecode.size() + titlesToEncode.size() + titlesToTransfer.size();
        }

    }

/* ABSTRACT METHODS */

    
/* METHODS */
    /**
     * Obtains the space left on the device on a human readable text form.
     *
     *@return A string gining the space left on the device.
     *
     *@author nicolas_cardoso
     */
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
            text = usedSpace/1024 + " KB" ; // Memorised the used space
        }
        else {
            text = usedSpace/1024 + "."+ (usedSpace%1024)/10 + " " ; // Memorised the used space
            if(n == 1) {text += "MB";} // Put the right unit
            if(n == 2) {text += "GB";}
            if(n == 3) {text += "TB";}
        }
        text += " / " ;
        
        n=0;
        while((tmpTotalSpace/(1024) > 1024)) { // Convert used space in a suitable unit
            tmpTotalSpace = tmpTotalSpace/1024;
            n++;
        }
        
        if(n==0) {
            text += tmpTotalSpace/1024 + " KB" ; // Memorised the used space
        }
        else {
            text += tmpTotalSpace/1024 + "."+ (tmpTotalSpace%1024)/10 + " " ; // Memorised the used space
            if(n == 1) {text += "MB";} // Put the right unit
            if(n == 2) {text += "GB";}
            if(n == 3) {text += "TB";}
        }
        
        return text;
    }    

    /**
     *Obtain the space left in the device in per cent.
     *
     *@return the space left in per cent.
     *
     *@author nicolas_cardoso
     */
    public int getSpaceLeftInRatio() {
        long usedSpace = totalSpace - usableSpace + titleToAddSpace - titleToRemoveSpace; // Calculate used space
        return (int)((usedSpace)*100.0/totalSpace);
    }
    
    
    /**
     *Obtain the generation of the device.
     *
     *@return the generation.
     *
     *@author nicolas_cardoso
     */
    public int getGeneration() {
        return generation;
    }
    
    
    /**
     *Obtains the tracklist into the device (more exactly, the titles currently registered in the database).
     *
     *@return The tracklist.
     *
     *@author nicolas_cardoso
     */
    public Title[] getTitles(){
        //Get the titles in a JSymphonic Map
        JSymphonicMap titles = dataBase.getTitles();
        
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
    
    /**
     *Obtains the tracklist into the device (more exactly, the titles currently registered in the database).
     *
     *@return The tracklist.
     *
     *@author nicolas_cardoso
     */
    public JSymphonicMap getTitlesInMap(){
        // Get the titles from the actual database
        JSymphonicMap titles = dataBase.getTitles();
        
        // Remove deleted titles
       /* Iterator itRemovedTitles = titlesToRemove.iterator();
        while(itRemovedTitles.hasNext()) {
            titles.remove(itRemovedTitles.next());
        }*/
        
        // Adding new titles - titles to decode
        Iterator itDecodedTitles = titlesToAddMonitor.titlesToDecode.iterator();
        int indexValue;
        if( titles.size() > 0) {
            indexValue = titles.maxValue(); // To keep JSymphonicMap structure, we must add an index value associated with each titles, as these value won't be used anymore, we just add values from the highest to be sur to not have twice the same
        }
        else {
            indexValue = 1;
        }
        while(itDecodedTitles.hasNext()) {
            titles.put(itDecodedTitles.next(),++indexValue);
        }

        // Adding new titles - titles to encode
        Iterator itEncodedTitles = titlesToAddMonitor.titlesToEncode.iterator();
        if( titles.size() > 0) {
            indexValue = titles.maxValue(); // To keep JSymphonicMap structure, we must add an index value associated with each titles, as these value won't be used anymore, we just add values from the highest to be sur to not have twice the same
        }
        else {
            indexValue = 1;
        }
        while(itEncodedTitles.hasNext()) {
            titles.put(itEncodedTitles.next(),++indexValue);
        }
        
        // Adding new titles - titles to transfer
        Iterator itTransferedTitles = titlesToAddMonitor.titlesToTransfer.iterator();
        if( titles.size() > 0) {
            indexValue = titles.maxValue(); // To keep JSymphonicMap structure, we must add an index value associated with each titles, as these value won't be used anymore, we just add values from the highest to be sur to not have twice the same
        }
        else {
            indexValue = 1;
        }
        while(itTransferedTitles.hasNext()) {
            titles.put(itTransferedTitles.next(),++indexValue);
        }

        return titles;
    }
    
    /**
     *Applies tracklist changes from the GUI to the device (export, delete, transfer tracks and update database).
     *This method should be used to start a new thread.
     */
    public void writeTitles(){
        // This method is just used to create a new thread. Action are done in the "writeTitlesInTread" method
        // Create a new thread to execute "writeTitlesInTread" method
        Thread t = new Thread(){
            @Override
            public void run(){
                try{
                    writeTitlesInTread();
                } catch(Exception e){
                    logger.warning(e.getMessage());
                }
            }
        };
        
        // Set the lowest priority to the thread
        t.setPriority(Thread.MIN_PRIORITY);
        
        // Start the thread
        t.start();
        
        // Erase the pointer
        t = null;
    }
   
    /**
     *Applies tracklist changes from the GUI to the device (export, delete, transfer tracks and update database).
     *This method really apply the changes as it is run from a new thread created by "writeTitles" method.
     */
    private void writeTitlesInTread() {
        try{
        // Initialize components
        sendTransferInitialization(titlesToExport.size(), titlesToRemove.size(), titlesToAddMonitor.sizeOfDecodeVector(), titlesToAddMonitor.sizeOfEncodeVector(), titlesToAddMonitor.sizeOfTransferVector(), dataBase.getNumberOfFiles());
        threadsStateMonitor.initialize(); // threads monitor

/*        // Calculate the increment for the progress bar in the GUI
        // Assign weight to each action
        int exportWeight = 20;
        int rmWeight = 2;
        final int addWeight = 20;
        final int dbWeight = 1;
        // Compute the increment
        final Double n = 100.0/(titlesToExport.size()*exportWeight + titlesToRemove.size()*rmWeight + titlesToAddMonitor.size()*addWeight + 10*dbWeight); // 10 stands for the number of config files in the database which must be written
 * */
        
        //Export files fisrt
        logger.info("Exportation started.");
        exportTitlesFromDevice(titlesToExport);
        
        //Delete files
        logger.info("Deleting files started.");
        deleteFilesFromDevice(titlesToRemove);
      
        
        //Add files
        logger.info("Importation started.");
        addFilesToDevice();
      
        }catch(Exception e){
            logger.warning(e.getMessage().toString());
        }
    }
    
    /**
     *Scans OMGAUDIO folder and fill in the title's list with all titles found. This method DON'T modify config files.
     * @param n
     * @param addWeight
     * @param dbWeight
     */
    private void addFilesToDevice() {
        // Start decode thread (which is running "decodeTitlesInThread" method)
        Thread decodeThread = new Thread(){
            @Override
            public void run(){
                try{
                    decodeTitlesInThread();
                } catch(Exception e){}
            }
        };
        decodeThread.setPriority(Thread.NORM_PRIORITY);
        logger.info("decodeThread has started");
        decodeThread.start();
        decodeThread = null;        
        
        
        
        // Start encode thread (which is running "encodeTitlesInThread" method)
        Thread encodeThread = new Thread(){
            @Override
            public void run(){
                try{
                    encodeTitlesInThread();
                } catch(Exception e){}
            }
        };
        encodeThread.setPriority(Thread.NORM_PRIORITY);
        encodeThread.start();
        logger.info("encodeThread has started");
        encodeThread = null;
        
        
        
        // Start transfer thread (which is running "transferTitlesInThread" method)
        Thread transferThread = new Thread(){
            @Override
            public void run(){
                try{
                    transferTitlesInThread();
                } catch(Exception e){}
            }
        };
        transferThread.setPriority(Thread.NORM_PRIORITY);
        transferThread.start();
        logger.info("transferThread has started ");
        transferThread = null;
        
        // Once all these 3 threads are launched, this thread can finish. Database update will be done in transfer thread, as it will be the last to finish its task.
    }
            
    
    /**
     * This method should be call in a new thread, dealing just with the decoding of titles in WAV. This method read the title to be decoded in the variable titlesToDecode. Once decoded, this method put the titles in the variable titlesToEncode.
     *
     *@author nicolas_cardoso
     */
    private void decodeTitlesInThread() {
        Title titleToDecode; // the title which is currently decoded
        int format; // format of the current title
        int temporaryCounter = 0; // a counter for the temporary decoded files (to create unique filename)
        int titlesDecoded = 0, titlesNotDecoded = 0; // Count the number of titles scanned
        
        if(titlesToAddMonitor.sizeOfDecodeVector() > 0){
            // Inform GUI
            sendTransferStepStarted(NWGenericListener.DECODING);
        }
        else{
            // Else, there is nothing to do
            logger.info("decodeThread has finished");
            threadsStateMonitor.notifyEncodeThread(true); // true to indicate that thread has finished
            return;
        }
        
        // Check the files supported for decoding by ffmpeg and give them to the encode thread
        while(!titlesToAddMonitor.isEmptyDecodeVector()) { // While there are title to be decoded
            // get the first element form the list of the title to decode
            titleToDecode = titlesToAddMonitor.firstElementOfDecodeVector();

            // Inform GUI
            sendFileChanged(NWGenericListener.DECODING, titleToDecode.toString());
            
            // Determine the format of the title        
            format = titleToDecode.getFormat();
            if(format == Title.FLAC){
                // Save the tag of the source to apply it to the temporary WAVE title (decoding loose tag info)
                Tag sourceTag = new Tag(titleToDecode);

                // Create a flac decoder object
                Decoder flacDecoder = new Decoder();

                // Create temporary output file
                File temporaryFolder = new File(getTempPath()); // default is the device directoy
                if(!temporaryFolder.exists()) {
                    // If the temporary folders don't exist, create them
                    temporaryFolder.mkdirs();
                }
                File newWAVFile = new File(temporaryFolder + "/JStmpFileDec" + temporaryCounter + ".wav");
                temporaryCounter++;

                // Decode the file
                try {
                    flacDecoder.decode(titleToDecode.getSourceFile().getPath(), newWAVFile.getPath(), this);
                } catch (IOException ex) {
                    logger.severe("ERROR in flac decoder while decoding file:"+titleToDecode.getSourceFile().getPath());
                    ex.printStackTrace();

                    // Saved changes
                    titlesNotDecoded++;
                }

                // Create a new Title object from the decoded title
                Title titleToEncode = Title.getTitleFromFile(newWAVFile);
                // change its status
                titleToEncode.setStatus(Title.TOENCODEANDREMOVEFROMDISK);
                // add the source tag
                titleToEncode.setTag(sourceTag);
                // give the title to encode thread
                titlesToAddMonitor.addToEncodeVector(titleToEncode);

                // Saved changes
                titlesDecoded++;
            }
            else{
                logger.severe("File format non supported for this file:"+titleToDecode.getSourceFile().getPath());

                // Saved changes
                titlesNotDecoded++;
                // Inform GUI
                sendFileProgressChanged(NWGenericListener.DECODING,100, 0) ;
            }
            
            // remove it from the decode list                
            titlesToAddMonitor.removeFromDecodeVector(titleToDecode);
        }
        
        
        // At the end of the thread, we update the state of the thread in the monitor and we wake up other threads
        logger.info("decodeThread has finished");
        threadsStateMonitor.notifyEncodeThread(true); // true to indicate that thread has finished
        
        if(titlesNotDecoded > 0){
            // If some titles haven't been deleted, inform the GUI
            sendTransferStepFinished(NWGenericListener.DECODING, NWGenericListener.DECODING_ERROR); // Inform GUI
        }
        else {
            // Else, inform the GUI that all went right
            sendTransferStepFinished(NWGenericListener.DECODING, NWGenericListener.NO_ERROR); // Inform GUI
        }
    }

    /**
     * This method should be call in a new thread, dealing just with the encoding of titles in MP3. This method read the title to be encoded in the variable titlesToEncode. Once encoded, this method put the titles in the variable titlesToTransfer.
     *
     *@author nicolas_cardoso
     */
    private void encodeTitlesInThread() {
        Title titleToEncode;
        boolean weShouldRun = true;
        int titlesEncoded = 0, titlesNotEncoded = 0; // Count the number of titles scanned
        int temporaryCounter = 0; // a counter to determine different name for all temporary files
        // An instance of ffmpeg is needed
        FFMpegToolBox ffMpegToolBox = new FFMpegToolBox();
        
        // Check that ffmpeg is available
        if(!FFMpegToolBox.isFFMpegPresent()){
            // if ffmpeg is not present
            // Display en error
            logger.warning("ERROR: ffmpeg can't be found, titles that need to be transcoded can't be transfered, please check you installation of ffmpeg.");
            
            // Empty the list of title to encode
            titlesToAddMonitor.clearEncodeVector();
            return;
        }

        // Check if there is anything to encode
        if((titlesToAddMonitor.sizeOfEncodeVector() > 0) || (threadsStateMonitor.waitForDecodeThread())) {
            // Inform GUI
            sendTransferStepStarted(NWGenericListener.ENCODING);
        }
        else{
            // Else, there is nothing to do
            logger.info("encodeThread has finished");
            threadsStateMonitor.notifyTransferThread(true); // true because this thread is over
            return;
        }
        
        while(weShouldRun) {
            if(titlesToAddMonitor.isEmptyEncodeVector()) {
                // There is no title in the vector, so either decodage is finished (decode thread is over) or we should wait
                // Wait is done in threadsStateMonitor
                weShouldRun = threadsStateMonitor.waitForDecodeThread();
                // Force another loop to re-test the state if of the encode vector or to leave the loop if all the work is done
                continue;
            }
            else {
                // There are titles to encode, we take the first one
                titleToEncode = titlesToAddMonitor.firstElementOfEncodeVector();
                
                // Inform GUI
                sendFileChanged(NWGenericListener.ENCODING, titleToEncode.toString());

                // Create temporary output file
                File temporaryFolder = new File(getTempPath()); // default is the device directoy
                if(!temporaryFolder.exists()) {
                    // If the temporary folders don't exist, create them
                    temporaryFolder.mkdirs();
                }
                File newMP3File = new File(temporaryFolder + "/JStmpFileEnc" + temporaryCounter + ".mp3");
                temporaryCounter++; // Increment the counter

                // Create a new Title instance for the conversion
                Mp3 titleEncoded = new Mp3(newMP3File.getAbsolutePath());
                
                try {
                    // We transcode it
                    logger.info("FFMPEG is called.");
                    titleEncoded = ffMpegToolBox.convertToMp3(titleToEncode, titleEncoded, getTranscodeBitrate(), this);
                    
                    // Save the number of file encoded
                    titlesEncoded++;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    
                    // Save the number of file not encoded
                    titlesNotEncoded++;
                }

                // If the title has been decoded, the temporary wave file sould be erased
                if(titleToEncode.getStatus() == Title.TOENCODEANDREMOVEFROMDISK) {
                    // Check that the filename has been generated by JSymphonic, to not deleted a non temporary file
                    if(titleToEncode.getSourceFile().getName().contains("JStmpFile")){
                        logger.fine("Temporary WAV file to delete:'" + titleToEncode.getSourceFile().getPath());
                        titleToEncode.getSourceFile().delete();
                    }
                    else{
                        logger.severe("A non temporary file has been avoided to be deleted because declared as a WAV temporary file !!! This is not normal, please report the bug to the developpers!!" +titleToEncode.getSourceFile().getPath() );
                    }
                }

                // Tell to the transfer thread to erase the title once it's transfered (this encoded file is temporary)
                titleEncoded.setStatus(Title.TOADDANDREMOVEFROMDISK);
                
                // Add the encoded title to the list of title to transfer
                titlesToAddMonitor.addToTransferVector(titleEncoded);
                
                // Remove the original title from the encode vector
                titlesToAddMonitor.removeFromEncodeVector(titleToEncode);
            }
        }
        
        if(titlesNotEncoded > 0){
            // If some titles haven't been deleted, inform the GUI
            sendTransferStepFinished(NWGenericListener.ENCODING, NWGenericListener.ENCODING_ERROR); // Inform GUI
        }
        else {
            // Else, inform the GUI that all went right
            sendTransferStepFinished(NWGenericListener.ENCODING, NWGenericListener.NO_ERROR); // Inform GUI
        }
        
        // At the end of the thread, we update the state of the thread in the monitor and we wake up other threads
        logger.info("encodeThread has finished");
        threadsStateMonitor.notifyTransferThread(true); // true because this thread is over
    }

    /**
     * This method should be call in a new thread, dealing just with the transfer of titles (already in MP3). This method read the title to be transfered in the variable titlesToTransfer.
     *
     *@author nicolas_cardoso
     */
    private void transferTitlesInThread() {
        //Transfer titles
        logger.info("Import files started.");
        importFilesToDevice();
        
        // Once all the title has been tranfered, end the transfer by writing the database and updating the GUI
        //Update database
        logger.info("Updating database started.");
        dataBase.update(); 
        logger.info("Updating database finished.");
        
        //Write the database to the config file
        logger.info("Writing database started.");
        dataBase.write(this);
        sendTransferStepFinished(NWGenericListener.UPDATING, NWGenericListener.NO_ERROR);
        logger.info("Writing database finished.");
        
        //The transfert is completed
        refreshTitles(); //reload everything...
        sendTransferTermination();
        logger.info("transferThread has finished");

        // Clean the vector for next transfer
        titlesToAddMonitor.clearDecodeVector();
        titlesToAddMonitor.clearEncodeVector();
        titlesToAddMonitor.clearTransferVector();
    }
    
    /**
    *Deletes track from the device. Changes are only applied when "writeTitles()" is called.
    *
    *@param t The title to remove.
    */
    public void removeTitles(Title t){
        // If the title is in the list of title to be added on the device (and not yet on the device), it can be simple removed from the list of title to add
        if(titlesToAddMonitor.contains(t)) {
            titlesToAddMonitor.remove(t);
            titleToAddSpace -= t.size(); // Update space TODO this will not work for transcoded titles
        }
        else {
            // If the title is on the device, its status should be changed to "TOREMOVE" and it should be added to the list of title to remove
            t.setStatus(Title.TODELETE);
            titlesToRemove.add(t);
            titleToRemoveSpace += t.size(); // Update space TODO this will not work for transcoded titles
        }
    }
    
    /**
     *Adds title to the list of title to add to the device. Changes are only applied when "writeTitles()" is called.
     *
     *@param t The title to add.
     *@param transcodeAllFiles should be true is the configuration is set to "always transcode", false otherwise
     *@param transcodeBitrate indicates the bitrate set in the configuration to be used to transcode.
     *
     *@return 0 if all is OK, -1 if the device is full
     */
    public int addTitle(Title t, Boolean transcodeAllFiles){
        double titleSpace;
        
        // Determine size of the title when it will be on the device TODO this doesn't seem to work well...
        if(t.isCompatible(transcodeAllFiles, getTranscodeBitrate())) {
           // if file is simply copied, we just have to get its size
            titleSpace = t.size();
        }
        else {
            // if file must be trancoded, its size should be computed from its lenght and bitrate
            titleSpace = (t.getLength()/1000.0)*(getTranscodeBitrate()*(1000.0/8.0)) + 2000.0; // title length is given in milliseconds, divide it by 1000 to have it in seconds; bitrate is in kpps, multiply it by (1000/8) to have it in octet per second, and the +2000 is for the lenght of the tag
            logger.fine("Computed title size (in Mo):"+titleSpace/(1024*1024));
        }
        
        // Before adding the title, space left should be checked
        if(titleToAddSpace+titleSpace-titleToRemoveSpace >= usableSpace) { 
            // If the device is full, return -1
            return -1;
        }
        else {
            // Else, determine if the file has to be DECODED, ENCODED or just IMPORTED and put it in the correct vector
            if(t.isCompatible(AlwaysTranscode, TranscodeBitrate)){
                // File is compatible, it can be transfered without been transcoded
                t.setStatus(Title.TOIMPORT); // Change status file
                titlesToAddMonitor.addToTransferVector(t); // Add the file to the vector
            }
            else if(t.isEncodable()) {
                // File is compatible with FFMPEG and only need to be encoded
                t.setStatus(Title.TOENCODE); // Change status file
                titlesToAddMonitor.addToEncodeVector(t); // Add the file to the vector
            }
            else{
                // File need to be decoded before been encoded
                t.setStatus(Title.TODECODE); // Change status file
                titlesToAddMonitor.addToDecodeVector(t); // Add the file to the vector
            }
            
            // Update space
            titleToAddSpace += titleSpace;
            
            return 0;
        }
    }

    /**
     * Add a title to the list of title to be exported from the device to the computer. Changes are only applied when "writeTitles()" is called.
     *
     *@param t The title to export.
     */
    public void exportTitle(Title t){
        // Add the title to the list of title to be exported
        t.setStatus(Title.TOEXPORT);
        titlesToExport.add(t);
    }
    
    /**
     * Replaces a title in the device. Changes are only applied when "writeTitles()" is called.
     * @param oldTitle
     * @param newTitle
     * this method is not used
    public void replaceTitle(Title oldTitle,Title newTitle){
        // Remove the title
        removeTitles(oldTitle);
        // Add the title
        addTitle(newTitle);
    }*/
    
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
        //Empty list of title to add from the monitor
        titlesToAddMonitor.clearDecodeVector();
        titlesToAddMonitor.clearEncodeVector();
        titlesToAddMonitor.clearTransferVector();
        //Empty list of title to remove
        titlesToRemove.clear();
        // Clear the database
        dataBase.clear();
        
        // Update space
        Java6ToolBox.FileSpaceInfo spaceInfo = Java6ToolBox.getFileSpaceInfo(sourceDir);
        usableSpace = spaceInfo.getUsableSpace();
        totalSpace = spaceInfo.getTotalSpace();
        titleToRemoveSpace = 0;
        titleToAddSpace = 0;
        
        loadTitlesFromDevice(); // Fill in the title's list
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
    *Tells if a title is compatible with the device. TODO this method should be updated and used !!
    *
    *@param t The title which must be tested.
    *
    *@return True if the title is compatible and false overwise.
    */
    private boolean isCompatible(Title t) {
        int format = t.getFormat();
        if(format != Title.MP3 || format != Title.WMA){
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
    protected void loadTitlesFromDevice(){
        int titleId = 0;
        int numberOfTitles = 0 ;
        Set totalTitlesList = new HashSet();
        //Create vector of directories in OMGAUDIO
        java.io.File[] dirList = sourceDir.listFiles();
        
        //First, search all the directories contening oma files
        for (int i = 0; i < dirList.length; i++){
            //Is it's a directory starting with "10F", it contains titles
            if (dirList[i].isDirectory() && (dirList[i].getName().toLowerCase().startsWith("10f") || dirList[i].getName().toLowerCase().startsWith("10F"))){
                java.io.File[] titlesList = dirList[i].listFiles(); //Create vector of files in the directory

                numberOfTitles += titlesList.length;
                totalTitlesList.add(titlesList);
            }
        }
        
        // Calculate the increment to use in loading progress bar in GUI
        double progressBarValue = 0;
        loadingInitialization(numberOfTitles);

        // Add the titles to the database
        Iterator it = totalTitlesList.iterator();
        while (it.hasNext()) {
            java.io.File[] titlesList = (File[])it.next();
            
            //For each file
            for(int j = 0; j < titlesList.length; j++){
                //Create a title from the file
                
                Title t = new Oma(titlesList[j]);
                
                //Determine the title ID to use in the database
                try{// An error may occur if the name of the title is not as expected.
                    titleId = Integer.parseInt(titlesList[j].getName().toLowerCase().replaceAll("10*(.*)\\.oma","$1"), 16);
                }
                catch(Exception e){
                    logger.warning("ERROR: A file in the 10FXX folder is not named as expected, file is skipped:"+titlesList[j].getPath());
                    continue;
                }

                //Add the title and the titleId in the database if not null
                if ( t != null && titleId != 0 ){
                    dataBase.addTitleWithTitleId(t, titleId);
                    
                    // Update loading progress bar
                    progressBarValue++;
                    sendLoadingProgresChange(progressBarValue);
                }
            }
        }
        
        // Update loading progress bar
        sendLoadingProgresChange(0);
        
    }

    /**
    *Scans OMGAUDIO folder and fill in the title's list with all titles found. This method DON'T modify config files.
    *
    *@param titlesToRemove The list of the title to remove from the device.
    */
    private void deleteFilesFromDevice(java.util.Vector<Title> titlesToRemove) {
        int titlesDeleted = 0, titlesNotDeleted = 0;
        Title titleToRemove;
        Iterator it = titlesToRemove.iterator();
        
        if(it.hasNext()){
            // Inform GUI
            sendTransferStepStarted(NWGenericListener.DELETING);
       }
                else{
            // Else, there is nothing to do
            return;
        }
        
        while( it.hasNext()) { //For each title
            // Get the title
            titleToRemove = (Title)it.next();
            
            // Inform GUI
            sendFileChanged(NWGenericListener.DELETING, titleToRemove.toString());
            sendFileProgressChanged(NWGenericListener.DELETING, 50, 0) ;
            
            // Update dataBase
            dataBase.removeTitle(titleToRemove);
            
            // Check the title to be an .oma
            if((!titleToRemove.getSourceFile().getPath().endsWith("oma")) && (!titleToRemove.getSourceFile().getPath().endsWith("OMA")) ) {
                logger.severe("This error occurs to prevent a file to be erased. If you see this message, please contact the developers in the forum 'https://sourceforge.net/forum/forum.php?forum_id=747001'.");
                titlesNotDeleted++;
                continue;
            }
            
            // Delete file form device
            logger.fine("Files removed :"+titleToRemove.getSourceFile().getPath() );
            titleToRemove.getSourceFile().delete();
            
            // Count number of deleted titles
            titlesDeleted ++;
            
            // Update progress bar in GUI
            sendFileProgressChanged(NWGenericListener.DELETING, 100, 0) ;
        }

        if(titlesNotDeleted > 0){
            // If some titles haven't been deleted, inform the GUI
            sendTransferStepFinished(NWGenericListener.DELETING, NWGenericListener.DELETING_ERROR); // Inform GUI
        }
        else {
            // Else, inform the GUI that all went right
            sendTransferStepFinished(NWGenericListener.DELETING, NWGenericListener.NO_ERROR); // Inform GUI
        }
        
        logger.info("Deleting files finished.");
        return;
    }
    
    /**
     * Add the list of files to the device. This method "turn" the titles to ".OMA" files. This method DON'T modify database files.
     */

    private void importFilesToDevice() {
        int freeTitleId; // titleID used to make a new file with a non-existing name on the device
        String fileName; // name of the new file on the device
        int dirNumber; // number of directory on the device used to make the new title
        String dirName; // directory name on the device used to make the new title
        File omaFile, directory; // new file and its directory
        Title titleToTransfer = new Oma(); // title to be transfered
        boolean weShouldRun = true; // allow to stop the transfer
        int titlesTransfered = 0, titlesNotTransfered = 0; // Count the number of titles scanned

        // Check if there is anything to transfer
        if((titlesToAddMonitor.sizeOfTransferVector() > 0) || (threadsStateMonitor.waitForEncodeThread())){
            // Inform GUI
            sendTransferStepStarted(NWGenericListener.IMPORTING);
        }
        else{
            // Else, there is nothing to do
            return;
        }
        
        while(weShouldRun) {
            if(titlesToAddMonitor.isEmptyTransferVector()) {
                // There is no title in the vector, so either encodage is finished (encode thread is over) or we should wait
                // Wait is done in threadsStateMonitor
                weShouldRun = threadsStateMonitor.waitForEncodeThread();
                // Force another loop to re-test the state if of the transfer vector or to leave the loop if all the work is done
                logger.info("Transfer thread is waiting for encode thread");
                continue;
            }
            // There are titles to transfer, we take the first one, and remove it from the vector
            titleToTransfer = titlesToAddMonitor.firstElementOfTransferVector();
            titlesToAddMonitor.removeFromTransferVector(titleToTransfer);
            
            // Inform GUI
            sendFileChanged(NWGenericListener.IMPORTING, titleToTransfer.toString());
                
            // Create the name of the file on the device and the folder in which it will be stored
            freeTitleId = dataBase.getFreeTitleId(); // Get a free title ID
            
            dirNumber = (freeTitleId / 255); // Build the name of the directory
            dirName = Integer.toString(dirNumber);
            if( dirNumber < 10 ) { //If dirNumber is less than 10, a zero must be added to the name of the directory
                dirName = "0" + dirName;
            }
            dirName = "10F" + dirName;
            
            // create the directory
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

            // Create the file
            fileName = Integer.toHexString(freeTitleId).toUpperCase(); // Build the name of the file
            while( fileName.length() < 7 ) {
                fileName = "0" + fileName; //Add zeros to have 7 characters
            }
            String index = "1" + fileName; // Save the index
            fileName = index + ".OMA"; //Add prefix and suffix
            
            // New path built mustn't represent an existing file
            omaFile = new File(sourceDir + "/" + dirName + "/" + fileName);
            if( omaFile.exists() ) {
                logger.warning("Meet an non-free file while adding oma files. Skip this file.");
                titlesNotTransfered++;
                continue;
            }

            // The destination file should have a EA3 tag. But depending on the source format, there are different way to create this tag
            Tag ea3tag;
            switch(titleToTransfer.getFormat()) {
                case Title.MP3:
                    ea3tag = new Ea3Tag((Mp3) titleToTransfer);
                    break;
                case Title.OMA:
                    ea3tag = new Ea3Tag(titleToTransfer.getSourceFile());
                    break;
                default:
                    logger.severe("The format " + titleToTransfer.getFormat() + " is not supported for importation for title "+titleToTransfer.getArtist() + " - " + titleToTransfer.getAlbum() +" - " + titleToTransfer.getTitle() +".");
                    titlesNotTransfered++;
                    continue;
            }

            // Create the destination file
            File newOmaFile = new File(sourceDir + "/" + dirName + "/" + fileName);

            // Copy the data
            try {
                binaryCopy(NWGenericListener.IMPORTING, titleToTransfer.getSourceFile(), newOmaFile, titleToTransfer.getFormat(), Title.OMA, ea3tag, freeTitleId);
                logger.info("Add file, source:'" + titleToTransfer.getSourceFile().getPath() + "', destination:'"+sourceDir + "/" + dirName + "/" + fileName+"'.");
                titlesTransfered++;
            } catch (Exception ex) {
                Logger.getLogger(NWGen5.class.getName()).log(Level.SEVERE, "Error while copying data", ex);
                ex.printStackTrace();
                titlesNotTransfered++;
            }
            
            // If the title was a temporary title, it should be deleted
            if(titleToTransfer.getStatus() == Title.TOADDANDREMOVEFROMDISK || titleToTransfer.getStatus() == Title.TOENCODEANDREMOVEFROMDISK) {
                // Check that the filename has been generated by JSymphonic, to not deleted a non temporary file
                if(titleToTransfer.getSourceFile().getName().contains("JStmpFile")){
                    logger.fine("Temporary file to delete:'" + titleToTransfer.getSourceFile().getPath());
                    titleToTransfer.getSourceFile().delete();
                }
                else{
                    logger.severe("A non temporary file has been avoided to be deleted because declared as a temporary file !!! This is not normal, please report the bug to the developpers!!" +titleToTransfer.getSourceFile().getPath() );
                }
            }

            // Update dataBase
            dataBase.addTitleWithTitleId(titleToTransfer, freeTitleId);
        }
        
        if(titlesNotTransfered > 0){
            // If some titles haven't been deleted, inform the GUI
            sendTransferStepFinished(NWGenericListener.IMPORTING, NWGenericListener.IMPORTING_ERROR); // Inform GUI
        }
        else {
            // Else, inform the GUI that all went right
            sendTransferStepFinished(NWGenericListener.IMPORTING, NWGenericListener.NO_ERROR); // Inform GUI
        }
        logger.info("Import files finished.");
        return;
    }

    /**
     *Export a list of titles from the device to the computer.
     *
     *@param titlesToCopy list of the titles to be exported.
     */
    private void exportTitlesFromDevice(java.util.Vector<Title> titlesToExport) {
        String artist, album, titleName, exportedFilePath, extention; // Title info
        int trackNumber, titleFormat, titleId; // Title info
        Tag destinationTag; // Tag of the exportedFile
        int titlesExported = 0; // Number of titles correctly exported
        int titlesNotExported = 0; // Number of titles not exported because of some trouble
        File directoryToExport, exportedFile; // The exported file is the new file
        Oma titleToExport; // The title to export is the existing file we want to copy localy
        Iterator it = titlesToExport.iterator();

        // Only start step if there are titles
        if(it.hasNext()){
            // Inform GUI
            sendTransferStepStarted(NWGenericListener.EXPORTING);
       }
        else{// Else, there is nothing to do
            return;
        }
        
        // If the export path is not valid, return
        if( !((new File(getExportPath())).exists()) ) {
                // TODO display a dialog window to tell that the path is not valid
            logger.warning("Export  path is not valid.");
            sendTransferStepFinished(NWGenericListener.EXPORTING, NWGenericListener.EXPORT_PATH_ERROR); // Inform GUI
            return;
        }
        
        while( it.hasNext() ) { //for each title
            // Get the title to add
            titleToExport = (Oma)it.next();
            
            //// Create the file name and his path
            // First, get title information
            artist = titleToExport.getArtist().replace("/","-").replace("?"," ");
            album = titleToExport.getAlbum().replace("/","-").replace("?"," ");
            titleName = titleToExport.getTitle().replace("/","-").replace("?"," ");
            trackNumber = titleToExport.getTitleNumber();
                    
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
            directoryToExport = new File (getExportPath() + "/" + artist);
            
            // Create directory to copy is it's not existing
            if( !directoryToExport.exists() ) {
                directoryToExport.mkdir(); // If the directory don't exist, create it (default is uppercase)
            }
 
            // Build directory to copy name (artist/album)
            directoryToExport = new File (getExportPath() + "/" + artist + "/" + album);
            
            // Create directory to copy is it's not existing
            if( !directoryToExport.exists() ) {
                directoryToExport.mkdir(); // If the directory don't exist, create it (default is uppercase)
            }
            
            // Build file name
            if(trackNumber != 0) {
                if(trackNumber < 10) {
                    exportedFilePath = artist + "/" + album + "/0" + trackNumber + "-" + titleName;
                }
                else {
                    exportedFilePath = artist + "/" + album + "/" + trackNumber + "-" + titleName;
                }
            }
            else {
                exportedFilePath = artist + "/" + album + "/" + titleName;
            }

            // Search the format of the file to know the extention
            // Also determine the tag of the exported file which depends on the format
            titleFormat = Ea3Tag.getFormat(titleToExport.getSourceFile()); // We are not using the "getFormat" method from the Title class since it will gice an OMA file... We are looking for the format of the file wrapped into this OMA, could be OMA, MP3, ...
            switch(titleFormat){
                case Title.OMA:
                    extention = ".oma";
                    destinationTag = (Ea3Tag)titleToExport.getTag(); // Should be an EA3Tag
                    break;
                case Title.MP3:
                    extention = ".mp3";
                    destinationTag = new Tag(titleToExport);
                    break;
                case Title.WMA:
                    extention = ".wma";
                    destinationTag = null; // There is no tag in a WAVE file
                    break;
                case Title.WAV:
                    extention = ".wav";
                    destinationTag = null; // There is no tag in a WAVE file
                    break;
                default:
                    // Title format is not as expected
                    logger.severe("Title format is of file "+titleToExport.getSourceFile().getPath()+" not as expected. File cannot be exported.");
                    titlesNotExported++;
                    continue;
            }

            // Determine the title ID of the title, it corresponds to the name of the OMA file
            String titleIdString = titleToExport.getSourceFile().getName().toLowerCase();
            titleIdString = titleIdString.replace(".oma",""); // remove the extension
            titleIdString = titleIdString.replaceFirst("1",""); // remove the 1 begining all the .oma file
            titleId = Integer.parseInt(titleIdString, 16); // parse integer from a text in HexaDecimal

            // Create exported file
            exportedFile = new File (getExportPath() + "/" + exportedFilePath + extention);

            // Inform GUI
            sendFileChanged(NWGenericListener.EXPORTING, exportedFilePath);
            
            // Check if title doesn't already exist at this adress
            if(exportedFile.exists()) {
                // If file exist, it won't be erase, inform the logger
                titlesNotExported++; 
                logger.log(Level.WARNING,"Can't export the MP3 file '" + exportedFile.getName() + "', it already exist at the adress '" + exportedFile.getAbsolutePath() + "'." );
                continue;
            }
            else {
                // Else, file can be created
                try {
                    exportedFile.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            try {
                // Data copy
                binaryCopy(NWGenericListener.EXPORTING, titleToExport.getSourceFile(), exportedFile, Title.OMA, titleFormat, destinationTag, titleId);
            } catch (Exception ex) {
                Logger.getLogger(NWGeneric.class.getName()).log(Level.SEVERE, "Error while copying data in the exportation of title "+titleToExport.getSourceFile().getPath(), ex);
                ex.printStackTrace();
            }
            titlesExported++; // File has been exported
        }

        // Exportation is over, even if errors occured, the list should be cleared.
        titlesToExport.clear();
        
        if(titlesNotExported > 0){
            // If some titles haven't been exported, inform the GUI
            sendTransferStepFinished(NWGenericListener.EXPORTING, NWGenericListener.EXPORTATION_ERROR); // Inform GUI
        }
        else {
            // Else, inform the GUI that all went right
            sendTransferStepFinished(NWGenericListener.EXPORTING, NWGenericListener.NO_ERROR); // Inform GUI
        }
        logger.info("Exportation finished.");
    }
    
    /**
     *Get the capacity of the device.
     *
     *@return the capacity.
     */
    public long getTotalSpace(){
        Java6ToolBox.FileSpaceInfo spaceInfo = Java6ToolBox.getFileSpaceInfo(sourceDir);
        return spaceInfo.getTotalSpace();
    }
    
    /**
     *Get the available space on the device.
     *
     *@return the available space.
     */
    public long getUsableSpace(){
        Java6ToolBox.FileSpaceInfo spaceInfo = Java6ToolBox.getFileSpaceInfo(sourceDir);
        return spaceInfo.getUsableSpace();
    }
    
    /**
     *Set the value of the config parameter "always transcode".
     *
     *@param AlwaysTranscode true if the "always transcode" option is set, false otherwise.
     */
    public void setAlwaysTranscode(boolean AlwaysTranscode) {
        this.AlwaysTranscode = AlwaysTranscode;
    }
    
//TODO: (PEDRO)
//-CHANGE DEBUG TO LOGGERS
//-CHANGE THOOSE "File format not as expected" EXCEPTIONS
//-CLEAN binaryCopyMp3FromDevice
//-FIX THE LOADKEY STUFF (TO SOMETHING MORE CLEAN THAN JUST TRYING UPP/LOWERCASE COMBINATIONS)


	/**
	 * Do the binary copy from the device to the computer
	 * More cleaning should be carried here
	 */
	private void binaryCopyMp3FromDevice(File newMp3, Title titleToCopy) throws Exception {
        byte [] buffer = new byte[4096]; // The buffer used to copy data

		if (this.gotkey) { // If a key is set, MP3 data should be scrambled 
                        int headerSize;

			// To compute the keyOma, i.e. the key for this precise oma file
			// first, we need to know the titleId used to crypt the file, it correspond to the number of the oma file
			//BIG BUG WAS HERE: THE INDEX IN THE NAME IS IN HEX!!!
			String titleIdString = titleToCopy.getSourceFile().getName().toUpperCase();
			titleIdString = titleIdString.replace(".OMA",""); // remove the extension
			titleIdString = titleIdString.replaceFirst("1",""); // remove the 1 begining all the .oma file
			int titleID = Integer.parseInt(titleIdString, 16); //TODO BUGFIX HERE! (radix 16)

			// Create streams for the copy
			FileInputStream source = new FileInputStream(titleToCopy.getSourceFile());
			FileOutputStream dest = new FileOutputStream(newMp3);

			// Copy the ID3 tag data (first EA3 header)
			// ANOTHER BUG HERE, ITS SIZE ISN'T STATIC!!):
			if (source.read(buffer, 0, 10) != 10)
				throw new Exception("File format not as expected");
			buffer[0] = 'I';
			buffer[1] = 'D';
			int id3size = (int)buffer[9] & 0x07F;
			id3size += 0x80 * ((int)buffer[8] & 0x07F);
			id3size += 0x4000 * ((int)buffer[7] & 0x07F);
			id3size += 0x200000 * ((int)buffer[6] & 0x07F);
			dest.write(buffer, 0, 10);

			byte [] buffer2 = new byte[id3size];	//This isn't too clean...
			if (source.read(buffer2) != id3size)
				throw new Exception("File format not as expected");
			dest.write(buffer2);
			

			//Skip the next EA3 (useless)header
			if (source.read(buffer, 0, 6) != 6)
				throw new Exception("File format not as expected");
			headerSize = 0xFF * (0x0FF & (int)buffer[4]);
			headerSize += (0x0FF & (int)buffer[5]);
			source.skip(headerSize - 6);

			this.binaryScrambleMp3DataV2(source, dest, titleID);
		}
		else {
                    /* OLD WAY TO COPY
                    FileChannel in = null; // channel used by the file copy
                    FileChannel out = null; // channel used by the file copy

                    logger.fine("File copy, source:'" + titleToCopy.getSourceFile().getPath() + "', destination:'"+newMp3.getPath()+"'.");

                    // Create channel to copy data
                    in = new FileInputStream(titleToCopy.getSourceFile()).getChannel();
                    out = new FileOutputStream(newMp3).getChannel();

                    // Copy from "in" to "out" avoiding EA3 tag
                    // Do we have to deal with the other (there are two of them) EA3 tag?
                    in.transferTo(3072, in.size() - 3072, out); //EA3 tag is fix : 3072 (TODO when there is no cover !!!!)
                    */
                    
                    // Create file streaml to copy data
                    FileInputStream in = new FileInputStream(titleToCopy.getSourceFile());
                    FileOutputStream out = new FileOutputStream(newMp3);
                    
                    int countIn = 0; // Count the number of bytes read from in when filling the buffer
                    int totalIn = 0; // Count the number of bytes read from in from the beginning
                    int currentIn = 0; // Count the number of bytes read from last speed computed
                    long startTime = 0; // The time the last speed computation was started (initialized to zero to force the speed computation at the first iteration of the copy)
                    float speed = 0;
                    long totalSize = titleToCopy.getSourceFile().length(); // Compute the total size of the file

                    while (countIn!=-1){ // While input file has bytes
                        countIn = in.read(buffer); // Read a amount of bytes
                        currentIn+=countIn; // Count the amount of bytes read since the last speed computation
                        
                        if (System.currentTimeMillis() - startTime>10){ // If 0.1 second passed
                            speed = currentIn / ((System.currentTimeMillis() - startTime)/100f) / 1024f; // Compute the speed in ko/s
                            currentIn = 0; // Initialize the number of bytes read for the next speed computation
                            startTime=System.currentTimeMillis(); // Initialize the time for next speed computation
                        }
                        
                        if (countIn > 0){ // If bytes have been read
                            totalIn+=countIn; // Count the number of bytes read since the beginning 
                            if (totalSize!=0){ // Check that the read size is not null
                                sendFileProgressChanged(NWGenericListener.EXPORTING, (double)((totalIn*100)/totalSize), (double)speed); // Inform GUI
                            }
                            
                            out.write(buffer,0,countIn); // Write data to the destination
                        }
                    }
		}
	}

	/*
	 * This little lady scrambles/unscrambles plain binary MP3 data from source to dest for V2 devices.
	 * 
	 * @param fromStream The source stream positioned at the binary data.
	 * @param toStream The destination stream positioned at the location to start writing the data.
	 * @param titleID The ID used to compute the key of the OMA file.
	 */
	private void binaryScrambleMp3DataV2(FileInputStream fromStream, FileOutputStream toStream, int titleID)
		throws IOException
	{
		short [] ucharKey = new short[4];
		byte [] buffer = new byte[4096];
		int nbRead;
		int scrambledCount;

		//Compute the key for this OMA stream (based on the index of the file)
		uintKey = (0x2465 + titleID * 0x5296E435L) ^ uintKey;

		ucharKey[3] = (byte)(uintKey & 0x0FFL);
		ucharKey[2] = (byte)((uintKey >> 8) & 0x0FFL);
		ucharKey[1] = (byte)((uintKey >> 16) & 0x0FFL);
		ucharKey[0] = (byte)((uintKey >> 24) & 0x0FFL);

		

		//Unscramble and copy the data
		while ((nbRead = fromStream.read(buffer)) > 0)
		{
			scrambledCount = nbRead - (nbRead % 8);
			for (int i = 0 ; i < scrambledCount; i++)
			{
				buffer[i] ^= ucharKey[i%4];
			}
			toStream.write(buffer, 0, nbRead);
		}
	}
/**
     *This methods allows classes to register for your events
     */
    public void addGenericNWListener(NWGenericListener listener) {
        listeners.add(listener);
    }

    /**
     *This methods allows classes to unregister for you events
     */
    public void removeGenericNWListener(NWGenericListener listener) {
      listeners.remove(listener);
    }

    protected void sendTransferInitialization(int numberOfExportFiles, int numberOfDeleteFiles, int numberOfDecodeFiles, int numberOfEncodeFiles, int numberOfTransferFiles, int numberOfDbFiles){
        for ( int j = 0; j < listeners.size(); j++ ) {
            NWGenericListener ev = (NWGenericListener) listeners.get(j);
            if ( ev != null ) {
                ev.transferInitialization(numberOfExportFiles, numberOfDeleteFiles, numberOfDecodeFiles, numberOfEncodeFiles, numberOfTransferFiles, numberOfDbFiles);
            }
        }
    }
    
    protected void sendTransferTermination(){
        for ( int j = 0; j < listeners.size(); j++ ) {
            NWGenericListener ev = (NWGenericListener) listeners.get(j);
            if ( ev != null ) {
                ev.transferTermination();
            }
        }
    }
    
    protected void sendTransferStepStarted(int step){
        for ( int j = 0; j < listeners.size(); j++ ) {
            NWGenericListener ev = (NWGenericListener) listeners.get(j);
            if ( ev != null ) {
                ev.transferStepStarted(step);
            }
        }
    }
    
    protected void sendTransferStepFinished(int step, int success){
        for ( int j = 0; j < listeners.size(); j++ ) {
            NWGenericListener ev = (NWGenericListener) listeners.get(j);
            if ( ev != null ) {
                ev.transferStepFinished(step, success);
            }
        }
    }        
        
    protected void sendFileChanged(int step, String name){
        for ( int j = 0; j < listeners.size(); j++ ) {
            NWGenericListener ev = (NWGenericListener) listeners.get(j);
            if ( ev != null ) {
                ev.fileChanged(step, name);
            }
        }
    }
        
    public void sendFileProgressChanged(int step, double value, double speed) {
        for ( int j = 0; j < listeners.size(); j++ ) {
            NWGenericListener ev = (NWGenericListener) listeners.get(j);
            if ( ev != null ) {
                ev.fileProgressChanged(step, value, speed);
            }
        }
    }

    protected void loadingInitialization(int i) {
        for ( int j = 0; j < listeners.size(); j++ ) {
            NWGenericListener ev = (NWGenericListener) listeners.get(j);
            if ( ev != null ) {
                ev.loadingInitialization(i);
            }
        }
    }

    protected void sendLoadingProgresChange(double i) {
        for ( int j = 0; j < listeners.size(); j++ ) {
            NWGenericListener ev = (NWGenericListener) listeners.get(j);
            if ( ev != null ) {
                ev.loadingProgresChanged(i);
            }
        }
    }
    
    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger aLogger) {
        logger = aLogger;
    }
    
    public static void setParentLogger(Logger aLogger) {
        logger.setParent(aLogger);
    }

    /**
     * This method copy audio file from a given source to a destination. The tag from the source can be skip (i.e. not copied to the destination). A given tag can be written in the destination.
     *
     * @param step The step the copy occurs in to inform GUI.
     * @param source The source file to copy from.
     * @param destination The destination to copy to.
     * @param sourceFormat The format of the source. This is used to skip the tag in the begining of the source file. This should be a number as given in class "Title", for example, for an MP3 file, use: "Title.MP3". If you don't want the tag from the source to be skip, put a negative number.
     * @param destinationFormat The format of the destination. This is used to write the tag in destination file. This should be a number as given in class "Title", for example, for an MP3 file, use: "Title.MP3". If you don't want the tag from the source to be written, put a negative number or simply use "binaryCopy(int step, File source, File destination, int sourceFormat)" method.
     * @param destinationTag The tag to be written in the destination file.
     * @param titleId The ID of the file currently copied. This is only used when data is encrypted, i.e. for protected players, generation 3 and before.
     * @throws java.lang.Exception
     */
	protected void binaryCopy(int step, File source, File destination, int sourceFormat, int destinationFormat, Tag destinationTag, int titleId) throws Exception {
        long sourceOffset; // The offset to be skipped in the source file.
        short[] ucharOmaKey = {0,0,0,0}; // Used to store the OmaKey for encryption

		// Create file streaml to copy data
        FileInputStream in = new FileInputStream(source);
        FileOutputStream out = new FileOutputStream(destination);

        // Search the offset to be skipped in the source file.
        switch(sourceFormat){
            case Title.OMA:
                sourceOffset = Ea3Tag.getMusicStartByte(source); // Search the position of the first audio data
                break;
            case Title.MP3:
                MP3File mp3File = new MP3File(source.getPath()); // Create and MP3File objet
                sourceOffset = mp3File.getMp3StartByte(source); // Search the position of the first audio data
                break;
            case Title.AAC:
                sourceOffset = 0; // TODO
                break;
            case Title.WAV:
                sourceOffset = 0; // TODO
                break;
            default:
                sourceOffset = 0;
        }

        // Skip the amount of bytes given in "sourceOffset" if positive
        if(sourceOffset > 0) {
            in.skip(sourceOffset);
        }


        // Before beginning the copy, write a tag to the destination, is asked
        // It depends from the destination format
        switch(destinationFormat){
            case Title.OMA:
                Ea3Tag.write(out, (Ea3Tag)destinationTag, gotkey, sourceFormat);
                break;
            case Title.MP3:
                break;
            case Title.AAC:
                break;
            case Title.WAV:
                break;
            default:
                // Just do nothing
        }

        if(gotkey) {
            // Protected players encrypt the files. Compute the decryption key corresponding to the current file index
            long omaKey; // Key for the current OMA file
            ucharOmaKey = new short[4]; // Same key in a char

            // Compute the key from Sony's secret recipe, uses the Key of the player, the ID of the OMA file and some numbers from nowhere...
            omaKey = (0x2465 + titleId * 0x5296E435L) ^ uintKey;

            // Save the omaKey into the char
            ucharOmaKey[3] = (byte)(omaKey & 0x0FFL);
            ucharOmaKey[2] = (byte)((omaKey >> 8) & 0x0FFL);
            ucharOmaKey[1] = (byte)((omaKey >> 16) & 0x0FFL);
            ucharOmaKey[0] = (byte)((omaKey >> 24) & 0x0FFL);
        }


        // Actual copy of audio data from source to destination
        byte [] buffer = new byte[4096]; // The buffer used to copy data
        long countIn = 0; // Count the number of bytes read from in when filling the buffer
        long totalIn = 0; // Count the number of bytes read from in from the beginning, initialized to the offset already skipped
        int currentIn = 0; // Count the number of bytes read from last speed computed
        long startTime = 0; // The time the last speed computation was started (initialized to zero to force the speed computation at the first iteration of the copy)
        float speed = 0; // Used to tell the speed to the GUI
        long totalSize = source.length(); // Compute the total size of the file
        int bytesCounter = 0; // Only used for encryption/decryption, count byte per byte to know how to encrypt/decrypt

        while (countIn!=-1){ // While input file has bytes
            countIn = in.read(buffer); // Read a amount of bytes
            currentIn+=countIn; // Count the amount of bytes read since the last speed computation

            if (System.currentTimeMillis() - startTime>10){ // If 0.1 second passed
                speed = currentIn / ((System.currentTimeMillis() - startTime)/100f) / 1024f; // Compute the speed in ko/s
                currentIn = 0; // Initialize the number of bytes read for the next speed computation
                startTime=System.currentTimeMillis(); // Initialize the time for next speed computation
            }

            if (countIn > 0){ // If bytes have been read
                totalIn+=countIn; // Count the number of bytes read since the beginning

                if(gotkey) {
                    // If the player is protected, data should be encrypted/decrypted:
                    for (int i = 0 ; i < countIn; i++) {
                        buffer[i] ^= ucharOmaKey[bytesCounter%4];
                        bytesCounter++;
                    }
                }

                // Write data to the destination
                out.write(buffer,0,(int)countIn);

                // Inform the GUI if the step is valid and the read size is not null
                if((totalSize!=0 && step >= 0) && (step > 0)){
                    sendFileProgressChanged(step, (double)(((totalIn+sourceOffset)*100)/totalSize), (double)speed); // Inform GUI
                }
            }
        }
	}
}
