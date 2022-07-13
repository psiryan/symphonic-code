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
 * DataBaseOmgaudio.java
 *
 * Created on 20 mai 2007, 10:12
 *
 */

package org.naurd.media.jsymphonic.device.sony.nw;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.naurd.media.jsymphonic.playlist.Playlist;
import org.naurd.media.jsymphonic.title.Title;
import org.naurd.media.jsymphonic.toolBox.DataBaseOmgaudioToolBox;
import org.naurd.media.jsymphonic.toolBox.ToolBox;

/**
 * An instance of the DataBaseOmgaudio class describes a dataBase on a 3rd generation of Sony devices. This generation has the following particularities:
 * - database is containing in a OMGAUDIO folder
 * - no cover
 * - no intelligent features
 * - protected players
 *
 * Example of 3rd generation players: NW-HD5, NW-E10x,...
 *
 * 
 * 
 * 
 * @author Nicolas Cardoso
 * @author Daniel Žalar - added events to ensure GUI independancy
 * @version 06.06.2007
 */
public class DataBaseOmgaudio extends DataBase{
/* FIELDS */
    private boolean gotKey; // True if generation is 0 to 3, false for generation 4 and above
    private boolean gotPlaylists; // True if device can read playlist
    private boolean gotIntelligentFeatures; // True if device has intelligent features
    private boolean gotCovers; // True if device can display covers
    private boolean gotSportFeatures; // True if device has sport features
    
/* CONSTRUCTORS */
    /**
     * Allows to create an instance of DataBaseOmgaudio from an existing device.
     * The database, once created is empty. Titles list can be filled with the method "addTitle". Before been written to the device, the database should be updated, with "update" method, to fill in the artists, albums,... lists.
     *
     *@param omgaudioDir The directory OMGAUDIO from the device.
     *
     * @author nicolas_cardoso
     */
    public DataBaseOmgaudio(java.io.File omgaudioDir, boolean gotPlaylists, boolean gotIntelligentFeatures, boolean gotCovers, boolean gotSportsFeatures, boolean gotKey) {
        // Call the super contructor
        super();
        
        // Save the base directory
        this.databaseDir = omgaudioDir;
        // Save the info
        this.gotPlaylists = gotPlaylists;
        this.gotIntelligentFeatures = gotIntelligentFeatures;
        this.gotCovers = gotCovers;
        this.gotSportFeatures = gotSportsFeatures;
        this.gotKey = gotKey;
    }
   

/* METHODS */ 
    /**
     * Write the database to the player.
     *
     *@param genNw The instance of the Net walkman.
     *
     *@author nicolas_cardoso
     */
    public void write(NWGeneric genNw) {
        String errorMessage = ""; // the error message, if any

        // Inform GUI
        genNw.sendTransferStepStarted(NWGenericListener.UPDATING);
        
        //Backup the database if it is not already done
        errorMessage = backupDatabase();
        
        // Erase all files in the OMGAUDIO folders (which are database files, and the database is going to be re-written) exepct the key file DvID.DAT, ffmpeg and the backup of the db
        String[] databaseFiles = databaseDir.list(); // Search all the files in the database
        boolean error = false;
        for(int i=0; i<databaseFiles.length; i++) {
            String databaseFileName = databaseFiles[i];
            // If the file is not the key files or the FFMPEG exe, delete it (file starting with "." are ignore)
            if((databaseFileName.toLowerCase().compareTo("dvid.dat") == 0) || (databaseFileName.toLowerCase().compareTo("ffmpeg.exe") == 0) || (databaseFileName.toLowerCase().compareTo("pthreadGC2.dll") == 0) || (databaseFileName.toLowerCase().compareTo("ffmpeg-win32.zip") ==0 ) || (databaseFileName.toLowerCase().compareTo("ffmpeg-win32") == 0) || (databaseFileName.toLowerCase().compareTo("30grct") == 0) || (databaseFileName.toLowerCase().compareTo("0001001d.dat") == 0) || (databaseFileName.toLowerCase().compareTo("00010021.dat") == 0) || (databaseFileName.toLowerCase().contains("srcidlst"))  || (databaseFileName.compareTo("db_backup") == 0)  || (databaseFileName.toLowerCase().contains("10f")) || (databaseFileName.toLowerCase().contains("jsymphonic")) || (databaseFileName.toLowerCase().startsWith(".")) ){
                continue;
            }

            File databaseFile = new File(databaseDir + "/" + databaseFileName);
            try {
                ToolBox.recursifDelete(databaseFile);
            } catch (IOException ex) {
                logger.severe("Error while deleting the old database. File/Folder cannot be deleted: "+databaseFile.getPath());
                error = true;
            }
        }
        
        // Log error for the GUI
        if(error){
            errorMessage +=  java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_delete_old_db") + "<br>";
        }
        // Update titleKey map
        updateTitleKey();
        updatePlaylistsValues();

        // Write base info in the database (titles, artist, album, genre)
        errorMessage += writeBaseInfo(genNw);

        // Write playlists (if concerned)
        if(gotPlaylists){
            errorMessage += writePlaylists(genNw);
        }

        // Write intelligent features (if concerned)
        if(gotIntelligentFeatures){
            errorMessage += writeIntelligentFeatures(genNw);
        }

        // Write covers (if concerned)
        if(gotCovers){
            errorMessage += writeCovers(genNw);
        }

        // Write sport features (if concerned)
        if(gotSportFeatures){
            errorMessage += writeSportFeatures(genNw);
        }

        // Database is up to date, inform GUI
        if(errorMessage.length() > 0){
            errorMessage = "<html>" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.Errors_during_update") + ":<br>" + errorMessage + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.consult_log") + ".";
            genNw.sendTransferStepFinished(NWGenericListener.UPDATING, errorMessage);
        }
        else {
            genNw.sendTransferStepFinished(NWGenericListener.UPDATING, "");
        }
    }

    /**
     * Write base info to the device. These info concern every generations.
     * Base info are "title, artist, album and genre".
     * Concerned files are "00GRTLST, 01TREE01, 01TREE02, 01TREE03, 01TREE04, 01TREE22, 01TREE2D, 02TREINF, 03GINF01, 03GINF02, 03GINF03, 03GINF04, 03GINF22, 03GINF2D, 04CNTINF, 05CIDLST"
     *
     * @param genNw instance of the generic device, used to inform the GUI of the progress of the task
     * @return Error messages written in a String. If no error occured, the string is empty. This String is meant to be displayed in the GUI to warn the user at the end of the task.
     */
    private String writeBaseInfo(NWGeneric genNw){
        String errorMessage = ""; // Store errors that may occured

        // Write 00GRTLST file (info about the database)
        try {
            DataBaseOmgaudioToolBox.write00GRTLST(databaseDir);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 00GRTLST file: " + ex.getMessage());
            errorMessage =  "00GRTLST" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "OOGRTLST");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);

        // Write 01TREE01 and 03GINF01 files (info about the titles)
        try {
            DataBaseOmgaudioToolBox.write01TREE01and03GINF01(databaseDir, titles);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 01TREE01 and 03GINF01 files: " + ex.getMessage());
            errorMessage +=  "01TREE01" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
            errorMessage +=  "03GINF01" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "01TREE01");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        genNw.sendFileChanged(NWGenericListener.UPDATING, "03GINF01");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);


        // Write 01TREE02 and 03GINF02 files (info about the artists)
        try {
            DataBaseOmgaudioToolBox.write01TREE02and03GINF02(databaseDir, titles);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 01TREE02 and 03GINF02 files: " + ex.getMessage());
            errorMessage +=  "01TREE02" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
            errorMessage +=  "03GINF02" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "01TREE02");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        genNw.sendFileChanged(NWGenericListener.UPDATING, "03GINF02");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);

        // Write 01TREE03 and 03GINF03 files (info about the albums)
        try {
            DataBaseOmgaudioToolBox.write01TREE03and03GINF03(databaseDir, super.titles);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 01TREE03 and 03GINF03 files: " + ex.getMessage());
            errorMessage +=  "01TREE03" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
            errorMessage +=  "03GINF03" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "01TREE03");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        genNw.sendFileChanged(NWGenericListener.UPDATING, "03GINF03");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);

        // Write 01TREE04 and 03GINF04 files (info about the genres)
        try {
            DataBaseOmgaudioToolBox.write01TREE04and03GINF04(databaseDir, titles);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 01TREE04 and 03GINF04 files: " + ex.getMessage());
            errorMessage +=  "01TREE04" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
            errorMessage +=  "03GINF04" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "01TREE04");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        genNw.sendFileChanged(NWGenericListener.UPDATING, "03GINF04");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);

        // Write 01TREE2D and 03GINF2D files
        try {
            DataBaseOmgaudioToolBox.write01TREE2Dand03GINF2D(databaseDir, titles);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 01TREE2D and 03GINF2D files: " + ex.getMessage());
            errorMessage +=  "01TREE2D" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
            errorMessage +=  "03GINF2D" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "01TREE2D");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        genNw.sendFileChanged(NWGenericListener.UPDATING, "03GINF2D");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);

        // Write 02TREINF file (list of the keys)
        try {
            DataBaseOmgaudioToolBox.write02TREINF(databaseDir, titles);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 02TREINF file: " + ex.getMessage());
            errorMessage +=  "02TREINF" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "02TREINF");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);


        // Write 04CNTINF file (list of the titles)
        try {
            DataBaseOmgaudioToolBox.write04CNTINF(databaseDir, titles, gotKey);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 04CNTINF file: " + ex.getMessage());
            errorMessage +=  "04CNTINF" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "04CNTINF");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);


        // Write 04PATLST file (list of the paths)
        try {
            DataBaseOmgaudioToolBox.write04PATLST(databaseDir, paths);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 04PATLST file: " + ex.getMessage());
            errorMessage +=  "04PATLST" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "04PATLST");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);


        // Write 05CIDLST file
        try {
            DataBaseOmgaudioToolBox.write05CIDLST(databaseDir, titles);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting 05CIDLST file: " + ex.getMessage());
            errorMessage +=  "05CIDLST" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "05CIDLST");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);

        return errorMessage;
    }

    /**
     * Write playlists info to the device.
     * Concerned files are 01TREE22 and 03GINF22.
     *
     * @param genNw instance of the generic device, used to inform the GUI of the progress of the task
     * @return Error messages written in a String. If no error occured, the string is empty. This String is meant to be displayed in the GUI to warn the user at the end of the task.
     */
    private String writePlaylists(NWGeneric genNw){
        String errorMessage = "";

        // Write 01TREE22 and 03GINF22 files
        try {
            DataBaseOmgaudioToolBox.write01TREE22and03GINF22(databaseDir, playlists, titles);
        } catch (Exception ex) {
            logger.severe("ERROR: while writing 01TREE22 and 03GINF22 files: " + ex.getMessage());
            errorMessage +=  "01TREE22" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
            errorMessage +=  "03GINF22" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "01TREE22");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        genNw.sendFileChanged(NWGenericListener.UPDATING, "03GINF22");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);
        
        return errorMessage;
    }

    /**
     * Write intelligent features info to the device.
     * Concerned files are "TODO"
     *
     * @param genNw instance of the generic device, used to inform the GUI of the progress of the task
     * @return Error messages written in a String. If no error occured, the string is empty. This String is meant to be displayed in the GUI to warn the user at the end of the task.
     */
    private String writeIntelligentFeatures(NWGeneric genNw){
        // TODO
        return "";
    }

    /**
     * Write covers info to the device.
     * Concerned files are "TODO"
     *
     * @param genNw instance of the generic device, used to inform the GUI of the progress of the task
     * @return Error messages written in a String. If no error occured, the string is empty. This String is meant to be displayed in the GUI to warn the user at the end of the task.
     */
    private String writeCovers(NWGeneric genNw){
        // TODO
        return "";
    }

    /**
     * Write sport features info to the device.
     * Concerned files are "TODO"
     *
     * @param genNw instance of the generic device, used to inform the GUI of the progress of the task
     * @return Error messages written in a String. If no error occured, the string is empty. This String is meant to be displayed in the GUI to warn the user at the end of the task.
     */
    private String writeSportFeatures(NWGeneric genNw){
        // TODO
        return "";
    }

    public void readPlaylists(){
        java.io.InputStream table122 = null; // The stream used to read the data
        java.io.InputStream table322 = null; // The stream used to read the data
        byte[] buffer1 = new byte[1];
        byte[] buffer2 = new byte[2];
        byte[] buffer4 = new byte[4];
        byte[] buffer80 = new byte[0x80 - 6];
        File table122File = new File(databaseDir.getPath() + File.separatorChar + "01TREE22.DAT");
        File table322File = new File(databaseDir.getPath() + File.separatorChar + "03GINF22.DAT");
        int numberOfPlaylists, elementSize, playlistId, flag, index, class2Offset, numberOfTitles, nextIndex, titleIdTmp;
        Title titleTmp;
        String playlistName;
        List indexTable = new ArrayList(), idTable = new ArrayList(), playlistTmp = new ArrayList();

        // Empty the list of playlists
        playlists.clear();

        // Search if files exist
        if(!table122File.exists()){
            // If file doesn't exist, try in lowercase
            table122File = new File(databaseDir.getPath() + File.separatorChar + "01tree22.dat");
            if(!table122File.exists()){
                // No file found, display a warning message
                logger.info("01TREE22.DAT file cannot be found. No playlist found.");
                return;
            }
        }
        if(!table322File.exists()){
            // If file doesn't exist, try in lowercase
            table322File = new File(databaseDir.getPath() + File.separatorChar + "03ginf22.dat");
            if(!table322File.exists()){
                // No file found, display a warning message
                logger.info("03GINF22.DAT file cannot be found. No playlist found.");
                return;
            }
        }

        try{
            //Open the file in stream mode (must be in try-catch)
            table122 = table122File.toURI().toURL().openStream();
            table322 = table322File.toURI().toURL().openStream();

            // First, read the playlist names
            table322.skip(0x10); // Skip table header
            table322.skip(0x10); // skip table description
            table322.skip(0x4); // Skip first 4 bytes of the class description

            // Read the number of elements in the class
            table322.read(buffer2);
            numberOfPlaylists = DataBaseOmgaudioToolBox.bytes2int(buffer2); // Convert it to int

            // Read the size of an element in the class
            table322.read(buffer2);
            elementSize = DataBaseOmgaudioToolBox.bytes2int(buffer2); // Convert it to int

            // Skip bytes to first element
            table322.skip(0x08);

            // Read each element
            for(int i=1; i <= numberOfPlaylists; i++){
                table322.skip(0x10); // skip element description
                table322.skip(6); // Skip "TIT2" tag and the 2 bytes giving the text-encoding
                table322.read(buffer80);
                playlistName = new String(DataBaseOmgaudioToolBox.rtrimZeros(buffer80),"UTF16"); // Convert to string
                playlistName = playlistName.replace(""+(char)0,""); //Remove empty characters from the TagValue (these characters are due to 16-bits encodage in Ea3Tag)

                // Create a playlist and add it to the list
                Playlist newPlaylist = new Playlist(playlistName);
                playlists.put(i, newPlaylist);

                // Skip bytes until the next element
                table322.skip(elementSize - 0x80 - 0x10);
            }

            // Once all the playlist names have been read, get the list of titles for each playlist
            table122.skip(0x10); // Skip table header
            table122.skip(0x10); // skip table 1 description
            // Table 2 description
            table122.skip(0x4); // skip table name "TPLB"
            table122.read(buffer4); // Read address of second table
            class2Offset = DataBaseOmgaudioToolBox.bytes2int(buffer4); // Convert it to int
            table122.skip(0x8); // Skip the end of the class description
            table122.skip(0x4); // Skip first 4 bytes of the class description

            // Read the number of elements in the class
            table122.read(buffer2);
            numberOfPlaylists = DataBaseOmgaudioToolBox.bytes2int(buffer2); // Convert it to int

            // Read the size of an element in the class
            table122.read(buffer2);
            elementSize = DataBaseOmgaudioToolBox.bytes2int(buffer2); // Convert it to int

            // Skip bytes to first element
            table122.skip(0x08);

            // Read each element in the first class
            for(int i=0; i < numberOfPlaylists; i++){
                // read playlist ID
                table122.read(buffer2);
                playlistId = DataBaseOmgaudioToolBox.bytes2int(buffer2); // Convert it to int

                // Read state flag
                table122.read(buffer1);
                flag = DataBaseOmgaudioToolBox.bytes2int(buffer1); // Convert it to int
                if(flag != 1){
                    // If flag is not set to "1", playlist is not used, skip to next one
                    table122.skip(elementSize-3);
                    continue;
                }

                // Read index of the first item
                table122.skip(1);
                table122.read(buffer2);
                index = DataBaseOmgaudioToolBox.bytes2int(buffer2); // Convert it to int

                // Skip bytes to next element
                table122.skip(elementSize-6);

                // Store info
                indexTable.add(i, index);
                idTable.add(i, playlistId);
            }

            // Skip bytes until the second class
            long amountToSkip = class2Offset - 0x40 - elementSize*numberOfPlaylists;
            long reallyskiped;
            while(amountToSkip > 0) {
                reallyskiped = table122.skip(amountToSkip);
                amountToSkip -= reallyskiped;
            }

            // Skip class name "TPLB"
            table122.skip(0x4);

            // Read the number of elements in the class
            table122.read(buffer2);
            numberOfTitles = DataBaseOmgaudioToolBox.bytes2int(buffer2); // Convert it to int

            // Read the size of an element in the class
            table122.read(buffer2);
            elementSize = DataBaseOmgaudioToolBox.bytes2int(buffer2); // Convert it to int

            // Skip bytes to first element
            table122.skip(0x08);

            // Prepare loop, read second element (first is 1, we need to know the one after, to know when to switch)
            int j = 0;
            if(indexTable.size() > 1){
                nextIndex = (Integer)indexTable.get(1);
            }
            else{
                nextIndex = numberOfTitles;
            }

            // Read each element in the first class
            for(int i=1; i <= numberOfTitles; i++){
                if(i == nextIndex){
                    // If next index is reached, save the current playlist and prepare next one (if any)
                    updatePlaylist((Integer)idTable.get(j), playlistTmp);
                    j++;

                    if(indexTable.size() > j+1){
                        nextIndex = (Integer)indexTable.get(j+1);
                    }
                    else{
                        // if no other playlist exist, the next index is the last one
                        nextIndex = numberOfTitles + 1;
                    }
                    playlistTmp = new ArrayList(); // empty the list
                }

                // Read title ID
                table122.read(buffer2);
                titleIdTmp = DataBaseOmgaudioToolBox.bytes2int(buffer2); // Convert it to int

                // Add it to the list
                playlistTmp.add(titles.getKey(titleIdTmp));
            }

            // Manage last playlist if any
            if(idTable.size() > 1){
                updatePlaylist((Integer)idTable.get(j), playlistTmp);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            logger.warning("Error while reading playlists: "+e.getMessage());
        }
    }

    /**
     * Replace a playlist in the list by a new one.
     * 
     * @param id The ID of the playlist to replace.
     * @param newPlaylist The new list of title for the playlist.
     */
    private void updatePlaylist(int id, List newTitleslist) {
        // Get the playlist to replace (to read its name)
        Playlist playlistTmp = (Playlist) playlists.getValue(id);

        // Remove the old playlist from the list
        playlists.remove(id);
        // Add the new playlist with the same ID, the same name but the new list of titles
        playlists.put(id, new Playlist(playlistTmp.getName(), newTitleslist));
    }

    @Override
    public int getNumberOfFiles() {
        // todo Update this method taking into account the fact that covers, intelligent features... has to be written
        int baseFiles = 15;
        int playlistFiles = 2;
        int intelligentFeaturesFiles = 0;
        int coversFiles = 0;
        int sportFeatures = 0;
        int returnNb = baseFiles;

        // Add playlist files (if concerned)
        if(gotPlaylists){
            returnNb += playlistFiles;
        }

        // Add intelligent features files (if concerned)
        if(gotIntelligentFeatures){
            returnNb += intelligentFeaturesFiles;
        }

        // Add covers files (if concerned)
        if(gotCovers){
            returnNb += coversFiles;
        }

        // Add sport features files (if concerned)
        if(gotSportFeatures){
            returnNb += sportFeatures;
        }
        
        return returnNb;
    }
}
