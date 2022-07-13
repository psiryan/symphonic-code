/*
 * Copyright (C) 2007, 2008, 2009 Patrick Balleux, Nicolas Cardoso De Castro
 * (nicolas_cardoso@users.sourceforge.net), Daniel Žalar (danizmax@gmail.com)
 *
 * This file is part of JSymphonic program.
 *
 * JSymphonic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSymphonic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSymphonic. If not, see <http://www.gnu.org/licenses/>.
 *
 *****
 *
 * NWEsys.java
 *
 * Created on 15 mai 2009, 14:31:51
 *
 */

package org.naurd.media.jsymphonic.device.sony.nw;

import java.io.File;
import java.util.logging.Logger;
import org.naurd.media.jsymphonic.title.Oma;
import org.naurd.media.jsymphonic.title.Title;
import org.naurd.media.jsymphonic.title.Wmmp;
import org.naurd.media.jsymphonic.toolBox.DataBaseOmgaudioToolBox;
import org.naurd.media.jsymphonic.toolBox.Java6ToolBox;

/**
 * This class describe a Network Walkman with a database file based on a "ESYS" folder (generation 2).
 * It extends the NW generic class.
 *
 * @author skiron
 */
public class NWEsys extends NWGeneric{
/* FIELDS */
    //Other
    private static Logger logger = Logger.getLogger("org.naurd.media.jsymphonic.system.sony.nw.NWEsys");
    private NWEsys device2;

/* CONSTRUCTORS */
    /**
     * Allows to create an instance of NWOmgaudio from an existing device.
     *
     * TODO to update
     * @param sourceName The name of the source, i.e. the name of the device.
     * @param sourceDesc The description of the source.
     * @param sourceIcon The icon of the source.
     * @param listener an object that implements NWGenericListener or null for no listener
     */
    public NWEsys(String devicePath, String sourceName, String sourceDesc, javax.swing.ImageIcon sourceIcon, int generation, NWGenericListener listener, String exportPath){
        // Call the super contructor
        super(sourceName, sourceDesc, sourceIcon, listener, exportPath);

        // Set up the device and database folders
        this.devicePath = devicePath;

        // Set the generation
        this.generation = generation;
        
        if( !initSourcePath() ) {
            logger.severe("Invalid OMGAUDIO directory.\nExiting program.");
            System.exit(-1);
        }

        // TODO KEY !?
		this.gotkey = true; // Gen1 & 2 devices always have a key !!

        // Update space
        Java6ToolBox.FileSpaceInfo spaceInfo = Java6ToolBox.getFileSpaceInfo(source);
        usableSpace = spaceInfo.getUsableSpace();
        totalSpace = spaceInfo.getTotalSpace();

        // Set up the database
        dataBase = new DataBaseEsys(source);

        /* TODO key ?
        // Load the key
        if(loadKey(sourceDir.getParent()) < 0){
            // If the DvID.dat file is not found, we should display a warning message
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.NoKeyFound"), java.util.ResourceBundle.getBundle("localization/misc").getString("global.Error"), JOptionPane.ERROR_MESSAGE);

            // Since no keys have been found, all we can do is consider than the walkman doesn't need it...
            this.gotkey = false;
        }*/

        // Fill in the title's list
        loadTitlesFromDevice();
    }
    
    /**
     * Instance an object for NWEsys class, a Sony Walkman from second generation. Second generation is quite particular because the device is composed by two separated flash memories which should be addressed separately. It is thus necessary to have two device.
     * 
     * @param devicePath Path of the first flash memory.
     * @param devicePath2 Path of the second flash memory.
     * @param sourceName Name of the device.
     * @param sourceDesc Description of the device.
     * @param sourceIcon Icon of the device.
     * @param listener Listener.
     * @param exportPath Path of the export folder.
     */
    public NWEsys(String devicePath, String devicePath2, String sourceName, String sourceDesc, javax.swing.ImageIcon sourceIcon, NWGenericListener listener, String exportPath){
        // Instance first device, from second generation
        this(devicePath, sourceName, sourceDesc, sourceIcon, GENERATION_2, listener, exportPath);
        // Instance second device, a basic one from first generation
        device2 = new NWEsys(devicePath2, sourceName, sourceDesc, sourceIcon, GENERATION_1, listener, exportPath);
    }
/* Overwritten Methods */
/*    @Override
    public int scheduleImport(File[] files, boolean init) {
        if(generation == GENERATION_2){
            // If device is form gen2
            // Schedule import in first device
            if(scheduleImport(files, init) < 0){
                // If scheduleImport failled
            }
        }
    }*/

/* Abstract Methods implementation */
    @Override
    protected void applyDeletion() {
        int titlesDeleted = 0; // Number of titles deleted
        int titlesNotDeleted = 0; // Number of titles not deleted
        int incorrectSource = 0; // Number of titles not deleted because the source is not an WMMP file
        int transferStopped = 0; // Number of titles not deleted because transfer has been stopped
        Title titleToRemove;

        if(titleVectorsMonitor.size(TitleVectorsMonitor.DELETE_VECTOR) > 0 && !stopTransfer){
            // Inform GUI
            sendTransferStepStarted(NWGenericListener.DELETING);
        }
        else{
            // Else, there is nothing to do
            return;
        }

        while(titleVectorsMonitor.size(TitleVectorsMonitor.DELETE_VECTOR) > 0 && !stopTransfer) { //For each title
            // Get the title
            titleToRemove = titleVectorsMonitor.firstElement(TitleVectorsMonitor.DELETE_VECTOR);

            // Inform GUI
            sendFileChanged(NWGenericListener.DELETING, titleToRemove.toString());
            sendFileProgressChanged(NWGenericListener.DELETING, 50, 0) ;

            // Update dataBase
            dataBase.removeTitle(titleToRemove);

            // Check the title to be an .dat
            if( !titleToRemove.getSourceFile().getPath().toLowerCase().endsWith(".dat") ) {
                logger.severe("This error occurs to prevent a file to be erased: "+titleToRemove.getSourceFile().getPath()+". If you see this message, please contact the developers in the forum 'https://sourceforge.net/forum/forum.php?forum_id=747001'.");
                titlesNotDeleted++;
                incorrectSource++;
                titleVectorsMonitor.remove(TitleVectorsMonitor.DELETE_VECTOR, titleToRemove);
                continue;
            }

            // Delete file form device
            logger.fine("Files removed :"+titleToRemove.getSourceFile().getPath() );
            titleToRemove.getSourceFile().delete();

            // Count number of deleted titles
            titlesDeleted ++;
            // Update database
            dataBase.delete(titleToRemove);
            // Title has been treated, remove it from the vector
            titleVectorsMonitor.remove(TitleVectorsMonitor.DELETE_VECTOR, titleToRemove);

            // Update progress bar in GUI
            sendFileProgressChanged(NWGenericListener.DELETING, 100, 0) ;
        }

        // If transfer has been stopped, count the number of file not deleted
        if(!stopTransfer) {
            titlesNotDeleted += titleVectorsMonitor.size(TitleVectorsMonitor.DELETE_VECTOR);
            transferStopped = titleVectorsMonitor.size(TitleVectorsMonitor.DELETE_VECTOR);
        }

        // Deletion is over, even if errors occured, the list should be cleared.
        titleVectorsMonitor.clear(TitleVectorsMonitor.DELETE_VECTOR);

        if(titlesNotDeleted > 0){
            // If some titles haven't been deleted
            // Write error message
            String errorMessage = "<html>" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.Errors_during_deletion") + ":<br>";
            if(incorrectSource > 0){
                errorMessage += incorrectSource + " " + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_delete_incorrect_source") + "<br>";
            }
            if(transferStopped > 0){
                errorMessage += transferStopped + " " + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_delete_transfer_stopped") + "<br>";
            }
            errorMessage += java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.consult_log") + ".";

            // Inform the GUI
            sendTransferStepFinished(NWGenericListener.DELETING, errorMessage); // Inform GUI
        }
        else {
            // Else, inform the GUI that all went right
            sendTransferStepFinished(NWGenericListener.DELETING, ""); // Inform GUI
        }

        logger.info("Deleting files finished.");
        return;
    }

    @Override
    protected void applyExport() {
        //throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    protected void applyImport() {
        int titleId; // titleID used to make a new file with a non-existing filename on the device
        String fileName; // name of the new file on the device
        File wmmpFile, nw_mp3; // new file and the folder containing files
        Title titleToImport; // title to be transfered
        boolean weShouldRun = true; // allow to stop the transfer
        int titlesImported = 0; // Number of titles correctly imported
        int titlesNotImported = 0; // Number of titles not imported
        int databaseError = 0; // Number of titles not transfered because of a database error
        int copyError = 0; // Number of titles not transfered because of a copy error
        int noSpaceLeft = 0; // Number of titles not transfered because of the device is full
        int unsupportedFormat = 0; // Number of titles not transfered because format is not supported
        int transferStopped = 0; // Number of titles not transfered because transfer has been stopped
        int noLsiDrmKeyFiles = 0; // Number of titles not transfered because LSI DRM key files are missing.


        // Check if there is anything to transfer
        if((titleVectorsMonitor.size(TitleVectorsMonitor.IMPORT_VECTOR) > 0) || (threadsStateMonitor.waitForEncodeThread())){
            // Inform GUI
            sendTransferStepStarted(NWGenericListener.IMPORTING);
        }
        else{
            // Else, there is nothing to do
            return;
        }

        // Title are stored in the NW-MP3 folder, try to find this folder
        nw_mp3 = new File(source.getAbsolutePath() + File.separatorChar + "NW-MP3");
        if(!nw_mp3.exists()){
            // If folder doesn't exist, try with a different case
            nw_mp3 = new File(source.getAbsolutePath() + File.separatorChar + "nw-mp3");
        }
        if(!nw_mp3.exists()){
            // If folder doesn't exist, make it
            nw_mp3 = new File(source.getAbsolutePath() + File.separatorChar + "NW-MP3");
            nw_mp3.mkdir();
        }

        while(weShouldRun && !stopTransfer) {
            if(titleVectorsMonitor.isEmpty(TitleVectorsMonitor.IMPORT_VECTOR)) {
                // There is no title in the vector, so either encodage is finished (encode thread is over) or we should wait
                // Wait is done in threadsStateMonitor
                weShouldRun = threadsStateMonitor.waitForEncodeThread();
                // Force another loop to re-test the state if of the transfer vector or to leave the loop if all the work is done
                logger.info("Transfer thread is waiting for encode thread");
                continue;
            }
            // There are titles to transfer, we take the first one, and remove it from the vector
            titleToImport = titleVectorsMonitor.firstElement(TitleVectorsMonitor.IMPORT_VECTOR);
            titleVectorsMonitor.remove(TitleVectorsMonitor.IMPORT_VECTOR, titleToImport);

            // Inform GUI
            sendFileChanged(NWGenericListener.IMPORTING, titleToImport.toString());

            // Check format
            if(titleToImport instanceof Oma){
                // TODO not supported yet
                titlesNotImported++;
                unsupportedFormat++;
                // Update monitor
                titleVectorsMonitor.remove(TitleVectorsMonitor.IMPORT_VECTOR, titleToImport);
                continue;
            }

            // Create the name of the file on the device and the folder in which it will be stored
            titleId = dataBase.getTitleId(titleToImport); // Get a free title ID

            // Create the file
            String titleIdString;
            if(titleId < 0x10 ) { titleIdString = "000" + Integer.toHexString(titleId).toUpperCase();}
            else if(titleId < 0x100 ) { titleIdString = "00" + Integer.toHexString(titleId).toUpperCase();}
            else if(titleId < 0x1000 ) { titleIdString = "0" + Integer.toHexString(titleId).toUpperCase();}
            else { titleIdString = Integer.toHexString(titleId).toUpperCase();}
            fileName = nw_mp3.getAbsolutePath() + File.separatorChar + "MP" + titleIdString + ".DAT";

            // New path built mustn't represent an existing file
            wmmpFile = new File(fileName);
            if( wmmpFile.exists() ) {
                logger.warning("Meet an non-free file while adding wmmp files. Skip this file.");
                titlesNotImported++;
                databaseError++;
                // Update monitor
                titleVectorsMonitor.remove(TitleVectorsMonitor.IMPORT_VECTOR, titleToImport);
                continue;
            }

            // Create the destination file
            Title newWmmp = new Wmmp(wmmpFile, titleToImport);

            // Copy the data
            try {
                binaryCopy(NWGenericListener.IMPORTING, titleToImport, newWmmp, titleId);
                logger.info("Add file, source:'" + titleToImport.getSourceFile().getPath() + "', destination:'"+wmmpFile.getAbsolutePath()+"'.");
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.severe("Error while copying data: "+ex.getMessage());
                titlesNotImported++;
                if(ex.getMessage().toLowerCase().contains("no space left")){
                    noSpaceLeft++; // A copy error may be caused if device is full, if said so in the exception message, update the "noSpaceLeft" variable
                }
                else {
                    copyError++; // Else, the error is not specificly known
                }
                // Update monitor
                titleVectorsMonitor.remove(TitleVectorsMonitor.IMPORT_VECTOR, titleToImport);
                continue;
            }

            // If the title was a temporary title, it should be deleted
            if(titleToImport.getStatus() == Title.TO_IMPORT_AND_DELETE || titleToImport.getStatus() == Title.TO_ENCODE_AND_DELETE) {
                // Check that the filename has been generated by JSymphonic, to not deleted a non temporary file
                if(titleToImport.getSourceFile().getName().contains("JStmpFile")){
                    logger.fine("Temporary file to delete:'" + titleToImport.getSourceFile().getPath());
                    titleToImport.getSourceFile().delete();
                }
                else{
                    logger.severe("A non temporary file has been avoided to be deleted because declared as a temporary file !!! This is not normal, please report the bug to the developpers!!" +titleToImport.getSourceFile().getPath() );
                }
            }

            // Count number of imported titles
            titlesImported++;
            // Update database
            dataBase.updateStatus(titleToImport, Title.ON_DEVICE);
            // Title has been treated, remove it from the vector
            titleVectorsMonitor.remove(TitleVectorsMonitor.IMPORT_VECTOR, titleToImport);
        }

        // If transfer has been stopped, count the number of file not transfered
        if(!stopTransfer) {
            titlesNotImported += titleVectorsMonitor.size(TitleVectorsMonitor.IMPORT_VECTOR);
            transferStopped = titleVectorsMonitor.size(TitleVectorsMonitor.IMPORT_VECTOR);
        }

        // Task is over, even if errors occured, the list should be cleared.
        titleVectorsMonitor.clear(TitleVectorsMonitor.IMPORT_VECTOR);

        if(titlesNotImported > 0){
            // If some titles haven't been encoded
            // Write error message
            String errorMessage = "<html>" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.Errors_during_transfer") + ":<br>";
            if(databaseError > 0){
                errorMessage += databaseError + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_transfer_db_error") + "<br>";
            }
            if(copyError > 0){
                errorMessage += copyError + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_transfer_copy_error") + "<br>";
            }
            if(noSpaceLeft > 0){
                errorMessage += noSpaceLeft + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_transfer_no_space_left") + "<br>";
            }
            if(unsupportedFormat > 0){
                errorMessage += unsupportedFormat + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_transfer_unsupported_format") + "<br>";
            }
            if(transferStopped > 0){
                errorMessage += transferStopped + " " + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_transfer_transfer_stopped") + "<br>";
            }
            errorMessage += java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.consult_log") + ".";
            if(noLsiDrmKeyFiles > 0){
                errorMessage += noLsiDrmKeyFiles + " " + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_transfer_no_drmkeyfiles_found") + "<br>";
            }
            errorMessage += java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.consult_log") + ".";

            // Inform the GUI
            sendTransferStepFinished(NWGenericListener.IMPORTING, errorMessage); // Inform GUI
        }
        else {
            // Else, inform the GUI that all went right
            sendTransferStepFinished(NWGenericListener.IMPORTING, ""); // Inform GUI
        }
        logger.info("Import files finished.");
        return;
    }

    @Override
    protected void loadTitlesFromDevice() {
        java.io.InputStream pblist1Stream = null; // The stream used to read the data
        File nw_mp3;
        byte[] buffer2 = new byte[2];
        byte[] buffer4 = new byte[4];
        byte[] bufferF0 = new byte[0xF0];
        byte[] buffer100 = new byte[0x100];
        int numberOfTitles, pathListValid, titleId = 1;
        String album, artist, title;

        // Info are loaded from the PBLIST1 file
        // Try to find the file
        File pblist1 = new File(source.getAbsolutePath() + File.separatorChar + "PBLIST1.DAT");
        if(!pblist1.exists()){
            // If file doesn't exist, try with a different case
            pblist1 = new File(source.getAbsolutePath() + File.separatorChar + "PBLIST1.dat");
        }
        if(!pblist1.exists()){
            // If file doesn't exist, try with a different case
            pblist1 = new File(source.getAbsolutePath() + File.separatorChar + "pblist1.dat");
        }
        if(!pblist1.exists()){
            // If file doesn't exist, assume that the player is empty
            logger.warning("No database found, the device is assumed to be empty.");
            return;
        }

        // Title are stored in the NW-MP3 folder, try to find this folder
        nw_mp3 = new File(source.getAbsolutePath() + File.separatorChar + "NW-MP3");
        if(!nw_mp3.exists()){
            // If folder doesn't exist, try with a different case
            nw_mp3 = new File(source.getAbsolutePath() + File.separatorChar + "nw-mp3");
        }
        if(!nw_mp3.exists()){
            // If folder doesn't exist, assume that the player is empty
            logger.warning("No titles found, the device is assumed to be empty.");
            return;
        }

        // Search the number of audio file
        File[] fileInNw_mp3 = nw_mp3.listFiles();
        double progressBarValue = 0;
        sendLoadingInitialization(fileInNw_mp3.length);

        // Now that GUI has been informed, build the path list in the database
        pathListValid = dataBase.buildPathList();

        try {
            //Open the file in stream mode (must be in try-catch)
            pblist1Stream = pblist1.toURI().toURL().openStream();

            // Skip 0x18 bytes
            pblist1Stream.skip(0x18);

            // Read the number of titles
            pblist1Stream.read(buffer4);
            numberOfTitles = DataBaseOmgaudioToolBox.bytes2int(buffer4); // Convert it to int
            // Skip 0x4 more bytes
            pblist1Stream.skip(0x4);

            // Read info for each title
            while(titleId <= numberOfTitles){
                // Read album value
                pblist1Stream.read(bufferF0);
                album = new String(DataBaseOmgaudioToolBox.rtrimZeros(bufferF0),"UTF16"); // Convert to string
                // Remove extra spaces at the end of the album
                while(album.endsWith(" ")){
                    album = album.substring(0, album.length()-1);
                }
                if(album.length() == 0){album = "unknown album";}

                // Skip 0x10 bytes
                pblist1Stream.skip(0x10);
                // Skip 0x02 bytes
                pblist1Stream.skip(0x02);

                // Read number of title in this album
                pblist1Stream.read(buffer2);
                // Skip 0x0C bytes
                pblist1Stream.skip(0x0C);
                int numberOfTitlesInAlbum = DataBaseOmgaudioToolBox.bytes2int(buffer2); // Convert it to int
                if(numberOfTitlesInAlbum == 0) {numberOfTitlesInAlbum = 1;} // When 0 is read, it means that only one title is present in this album

                for(int j = 1; j <= numberOfTitlesInAlbum; j++){
                    // Skip file name
                    pblist1Stream.skip(0x100);

                    // Read title
                    pblist1Stream.read(buffer100);
                    title = new String(DataBaseOmgaudioToolBox.rtrimZeros(buffer100),"UTF16"); // Convert to string
                    if(title.length() == 0){title = "unknown title";}

                    // Read artist
                    pblist1Stream.read(buffer100);
                    artist = new String(DataBaseOmgaudioToolBox.rtrimZeros(buffer100),"UTF16"); // Convert to string
                    if(artist.length() == 0){artist = "unknown artist";}

                    // Check for file presence
                    String titleIdString;
                    if(titleId < 0x10 ) { titleIdString = "000" + Integer.toHexString(titleId).toUpperCase();}
                    else if(titleId < 0x100 ) { titleIdString = "00" + Integer.toHexString(titleId).toUpperCase();}
                    else if(titleId < 0x1000 ) { titleIdString = "0" + Integer.toHexString(titleId).toUpperCase();}
                    else { titleIdString = Integer.toHexString(titleId).toUpperCase();}
                    File audioFile = new File(nw_mp3.getAbsolutePath() + File.separatorChar + "MP" + titleIdString + ".DAT");

                    if(!audioFile.exists()){
                        // If file is not found, try in lowercase
                        audioFile = new File(nw_mp3.getAbsolutePath() + File.separatorChar + "mp" + titleIdString.toLowerCase() + ".dat");
                    }

                    if(audioFile.exists()){
                        // If file exist, create a new title object and add it to the database
                        // First, delete spaces at the end of the info fields
                        while(title.endsWith(" ")){
                            title = title.substring(0, title.length()-1);
                        }
                        while(artist.endsWith(" ")){
                            artist = artist.substring(0, artist.length()-1);
                        }
                        Title newTitle = new Wmmp(audioFile, title, album, artist);
                        newTitle.setStatus(Title.ON_DEVICE); // Change its status
                        dataBase.addTitle(newTitle, titleId);
                        if(pathListValid < 0){
                            dataBase.addPath(titleId, "unknown");
                        }

                        // Update loading progress bar
                        progressBarValue++;
                        sendLoadingProgresChange(progressBarValue);
                    }

                    // Increase the title ID for next iteration
                    titleId++;
                }
            }

            // Send the total of file, to be sure that device panel is showed, in case of something wrong occured
            sendLoadingProgresChange(fileInNw_mp3.length);

        } catch (Exception ex) {
            logger.severe("Error while loading the content of the player.");
        }
    }
}