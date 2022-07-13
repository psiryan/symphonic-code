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
 * DataBaseEsys.java
 *
 * Created on 25 february 2008, 20:44
 *
 */

package org.naurd.media.jsymphonic.device.sony.nw;

import java.io.File;
import java.io.IOException;
import org.naurd.media.jsymphonic.toolBox.DataBaseEsysToolBox;
import org.naurd.media.jsymphonic.toolBox.DataBaseOmgaudioToolBox;
import org.naurd.media.jsymphonic.toolBox.ToolBox;

/**
 *
 * @author skiron
 * @author Daniel Žalar - added events to ensure GUI independancy
 */
public class DataBaseEsys extends DataBase{
/* FIELDS */
    // Constant
    public final static byte[] HEADER_CST_1 = {(byte)0x0, (byte)0x8, (byte)0x3A, (byte)0xB3};
    public final static byte[] HEADER_CST_2 = {(byte)0x0, (byte)0x7, (byte)0x9D, (byte)0x60, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x1};
    public final static byte[] HEADER_CST_3 = {(byte)0x1A, (byte)0x9C, (byte)0x75};
    public final static byte[] HEADER_CST_PBLIST0 = {(byte)0xAA};
    public final static byte[] HEADER_CST_PBLIST1 = {(byte)0xAB};
    public final static byte[] PLAYER_KEY = {(byte)0x8, (byte)0x8D, (byte)0xDB};
    public final static byte[] DATABASE_KEY = {(byte)0x67};

/* CONSTRUCTORS */
    /**
     * Creates a new instance of DataBaseEsys
     */
    public DataBaseEsys(java.io.File esys) {
        // Call the super contructor
        super();

        this.databaseDir = esys;
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
            if((databaseFileName.toLowerCase().compareTo("ffmpeg.exe") == 0) || (databaseFileName.toLowerCase().compareTo("pthreadGC2.dll") == 0) || (databaseFileName.toLowerCase().compareTo("ffmpeg-win32.zip") ==0 ) || (databaseFileName.toLowerCase().compareTo("ffmpeg-win32") == 0) || (databaseFileName.compareTo("db_backup") == 0)  || (databaseFileName.toLowerCase().contains("nw-mp3")) || (databaseFileName.toLowerCase().contains("jsymphonic")) || (databaseFileName.toLowerCase().startsWith(".")) ){
                continue;
            }

            File databaseFile = new File(databaseDir + File.separator + databaseFileName);
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

        // Write PBLIST0 file
        try {
            DataBaseEsysToolBox.writePBLIST0(databaseDir, titles);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting PBLIST0 file: " + ex.getMessage());
            errorMessage =  "PBLIST0" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "PBLIST0");
        genNw.sendFileProgressChanged(NWGenericListener.UPDATING, 100, 0);


        // Write PBLIST0 file
        try {
            DataBaseEsysToolBox.writePBLIST1(databaseDir, titles);
        } catch (Exception ex) {
            logger.severe("ERROR: while writting PBLIST1 file: " + ex.getMessage());
            errorMessage =  "PBLIST1" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.err_update_file_error") + "<br>";
        }

        // Update progress bar in GUI
        genNw.sendFileChanged(NWGenericListener.UPDATING, "PBLIST1");
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


        // Database is up to date, inform GUI
        if(errorMessage.length() > 0){
            errorMessage = "<html>" + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.Errors_during_update") + ":<br>" + errorMessage + java.util.ResourceBundle.getBundle("localization/devicepanel").getString("DevicePanel.consult_log") + ".";
            genNw.sendTransferStepFinished(NWGenericListener.UPDATING, errorMessage);
        }
        else {
            genNw.sendTransferStepFinished(NWGenericListener.UPDATING, "");
        }
    }

    @Override
    public int getNumberOfFiles() {
        return 3;
    }

    @Override
    public void readPlaylists() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
