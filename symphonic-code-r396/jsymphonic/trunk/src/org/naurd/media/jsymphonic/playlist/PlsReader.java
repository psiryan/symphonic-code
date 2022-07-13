/*
 * Copyright (C) 2007, 2008, 2009 Patrick Balleux, Nicolas Cardoso De Castro
 * (nicolas_cardoso@users.sourceforge.net), Daniel Žalar (danizmax@gmail.com).
 * Gabriel Sichardt
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
 * PlsReader.java
 *
 * Created on June 15, 2008, 00:21 AM
 *
 */
package org.naurd.media.jsymphonic.playlist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.naurd.media.jsymphonic.title.Title;
import org.naurd.media.jsymphonic.title.UnknowFileTypeException;

/**
 * This class reads pls playlist files from the local filesystem.
 *
 * @author Gabriel Sichardt
 */
public class PlsReader extends Playlist {
    /*
     * FIELDS
     */

    /*
     * CONSTRUCTOR
     */
    /**
     * Create a PlsReader object from a file.
     *
     * @param sourceFile The file to instance the object from.
     */
    public PlsReader(File sourceFile) {
        super(sourceFile.getName().substring(0, sourceFile.getName().length() - 4), getTitles(sourceFile.getAbsolutePath()));
        setSourceFile(sourceFile);

    }

    /*
     * METHODS
     */

    /*
     * STATIC METHODS
     */
    public static ArrayList getTitles(String sourcefile) {
        /*
         * @param sourcefile The local playlist filename. @return The titles of
         * the playlist in an ArrayList.
         *
         * This method creates an ArrayList from a source PLS file and returns
         * it. Extended information is skipped and original order will be kept,
         * starting with standard index zero.
         */
        //TODO: cleanup, error messages, progress notification
        
//method variables
        int counter1 = 0;
        int counter2 = 0;
        String line2; //the line currently read
//end of method variables

        
        
        try {
            File sourcePLS = new File(sourcefile);
            BufferedReader in2 = new BufferedReader(new FileReader(sourcePLS));
            ArrayList titleList = new ArrayList(counter2);


            while ((line2 = in2.readLine()) != null) {

                if (line2.contains("File"))//filtering out lines with extended info
                {
                    String[] cutting = line2.split("=");
                    String filepath = cutting[1];

                    if (filepath.startsWith(".")) //create absoulute paths from relative paths
                    {
                        filepath = filepath.substring(1);
                        filepath = sourcePLS.getParent() + filepath;
                    }

                    // We have the pathname of a music file that we should instance to a Title object, but to do so, we should create a new file instance
                    // Note that we are instancing a Title object using the "getTitleFromFile" method, which throws an UnknowFileTypeException when the file is not as expected, so this method should be called in a try-catch block
                    try {
                        File titleFile = new File(filepath);
                        if (titleFile.getAbsoluteFile().exists()) {
                            titleList.add(Title.getTitleFromFile(titleFile.getAbsoluteFile()));
                        } else {
                            logger.severe("The file " + filepath + " was not found!");
                        }

                    } catch (UnknowFileTypeException ex) {
                        // File was not as expected, report the error
                        logger.severe("ERROR while loading a playlist file, the file type is not known. " + ex.getMessage());
                    }

                    counter1++;
                }

            }

            in2.close();
            return titleList;
        } catch (IOException except) {
            except.printStackTrace();
            return null;
        }

    }

    /**
     * Obtains the list of the possible file extentions for this format.
     *
     * @return A list of extentions separated with comas.
     */
    public static String getFileExtentionsList() {
        return "pls";
    }
}