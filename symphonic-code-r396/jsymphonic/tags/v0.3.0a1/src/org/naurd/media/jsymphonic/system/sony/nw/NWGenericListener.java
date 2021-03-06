/*
 * Copyright (C) __DATE__ Daniel Žalar (danizmax@yahoo.com)
 * 
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
 */

package org.naurd.media.jsymphonic.system.sony.nw;

import java.util.EventListener;

/**
 *
 * @author Daniel Žalar, nicolas_cardoso
 */
public interface NWGenericListener extends EventListener{
    // Steps constant
    public static final int EXPORTING = 1;
    public static final int DELETING = 2 ;
    public static final int IMPORTING = 3;
    public static final int DECODING = 4;
    public static final int ENCODING = 5;
    public static final int UPDATING = 6;
    
    // Errors constant
    public static final int NO_ERROR = 0;
    public static final int EXPORT_PATH_ERROR = -1;
    public static final int EXPORTATION_ERROR = -2;
    public static final int DELETING_ERROR = -3;
    public static final int DECODING_ERROR = -4;
    public static final int ENCODING_ERROR = -5;
    public static final int IMPORTING_ERROR = -6;

    /**
     *  This method is called when a transfer started to initialize the GUI for a new transfer.
     */
    public void transferInitialization(int numberOfExportFiles, int numberOfDeleteFiles, int numberOfDecodeFiles, int numberOfEncodeFiles, int numberOfTransferFiles, int numberOfDbFiles);

    /**
     * This method is called when the transfer is over.
     */
    public void transferTermination();

    /**
     * This method is called when a step of the transfer is started (Exporting or Deleting for instance).
     *
     * @param step the started step, should be EXPORTING, or DELETING,...
     */
    public void transferStepStarted(int step);

    /**
     * This method is called when a step of the transfer is finished (Exporting or Deleting for instance).
     *
     * @param step the finished step, should be EXPORTING, or DELETING,...
     * @param success a description of how the step completed (0 means that all went right, negative values mean that a problem occured, the problem depends on the number)
     */
    public void transferStepFinished(int step, int success);

    /**
     * This method is called when a new file is managed. The name of the file is provided.
     * When this method is called, at least two progress bars need to be updated: the overall progress bar and a particular bar corresponding to the current stage and designed by the input.
     *
     * @param step The current step we're in, used to increase the corresponding progress bar (should be EXPORTING, or DELETING,...).
     * @param name The name of the file currently in progress.
     */
    public void fileChanged(int step, String name);

    /**
     * This method is call to change the progress of the currently exported file.
     * The method "exportingFileChange()" should reset the progress bar when a new file is exported.
     *
     * @param step The current step we're in, used to increase the corresponding progress bar (should be EXPORTING, or DELETING,...).
     * @param value The current value of the progress bar (0 means that the bar is empty and 100 filled).
     * @param speed the current speed of the operation (in kops)
     */
    public void fileProgressChanged(int step, double value, double speed);
    
    /**
     * This method is used to initialize the progress bar when the content of the player is read.
     *
     * @param numberOfFile Number of files in the device to be read.
     */
    public void loadingInitialization(int numberOfFile);

    /**
     * This method is used to update the progress bar when the content of the player is read.
     * 
     * @param value
     */
    public void loadingProgresChanged(double value);
    
}
