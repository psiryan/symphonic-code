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
 * M3uReader.java
 *
 * Created on June 15, 2008, 00:07 AM
 *
 */
package org.naurd.media.jsymphonic.playlist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.naurd.media.jsymphonic.title.Title;

/**
 * This class reads m3u playlist files from the local filesystem.
 *
 * @author Gabriel Sichardt
 */
public class M3uReader extends Playlist {
    /*
     * FIELDS
     */

    /*
     * CONSTRUCTOR
     */
    /**
     * Create a M3uReader object from a file.
     *
     * @param sourceFile The file to instance the object from.
     */
    public M3uReader(File sourceFile) {
        super(sourceFile.getName().substring(0, sourceFile.getName().length() - 4), getTitles(sourceFile.getAbsolutePath()));
        setSourceFile(sourceFile);
    }


    /*
     * METHODS
     */

    /*
     * STATIC METHODS
     */
    /**
     *
     * @param sourcefile
     * @return
     */
    public static ArrayList getTitles(String sourcefile) {
        /*
         * @param sourcefile The local playlist filename. @return The titles of
         * the playlist in an ArrayList.
         *
         * This method creates an ArrayList from a source M3U file and returns
         * it. Extended information is skipped and original order will be kept,
         * starting with standard index zero.
         */

        //method variables
        String line;   //Line currently read from file
        String filesNotAdded = "";
        ArrayList titleList = new ArrayList(0);
        ArrayList lines = new ArrayList();
        File sourcem3u = new File(sourcefile);
        ProcessingWindow proc = new ProcessingWindow();

        //end of method varibles
        proc.setLocationRelativeTo(proc.getParent());
        proc.setVisible(true);
        
        try {
            logger.info("Reading the m3u file.");
            //Reading the sourcefile

            BufferedReader in = new BufferedReader(new FileReader(sourcem3u));
            
            
            
            while ((line = in.readLine()) != null) {
                if (line.startsWith("#")) {	//filtering out lines with extended info
                    continue; //skip line
                }
                
                if (line.startsWith(".")) //create absoulute paths from relative paths
                {
                    line = line.substring(1);//removing leading dot
                    line = sourcem3u.getParent() + line;//adding the path of the playlist file
                }
                lines.add(line);
            }//end of reading loop
            in.close();
            proc.getProgressBar().setMaximum(lines.size());
            proc.getReadingCheckBox().setSelected(true);

            // We are processing a line with a file address, that we should instance to a Title object, but to do so, we should create a new file instance
            // Note that we are instancing a Title object using the "getTitleFromFile" method, which throws an UnknowFileTypeException when the file is not as expected, so this method should be called in a try-catch block
            for (int i = 0; i < lines.size(); i++) {
                line = (String) lines.get(i);
                proc.getCurrentFileLabel().setText(line);
                try {
                    
                    File titleFile = new File(line);
                    
                    if (titleFile.getAbsoluteFile().exists()) {
                        titleList.add(Title.getTitleFromFile(titleFile.getAbsoluteFile()));
                    } else {
                        // The file may have been written in the playlist file as an URL, check that (this may generate an exception)
                        try {
                            URL titleURL = new URL(line);
                            titleFile = new File(titleURL.toURI());
                            if (titleFile.getAbsoluteFile().exists()) {
                                titleList.add(Title.getTitleFromFile(titleFile.getAbsoluteFile()));
                            } else {
                                logger.severe("The file " + line + " was not found!");
                                filesNotAdded += line + "\n";
                            }
                        } catch (Exception e) {
                            logger.info("The file " + line + " is not an URL, and it can't be found!");
                            filesNotAdded += line;
                        }
                    }
                    
                } catch (Exception ex) {
                    // File was not as expected, report the error
                    logger.severe("ERROR while loading a playlist file, the file type is not known. " + ex.getMessage());
                }
                proc.getProgressBar().setValue(i);
            }
            proc.getScanningCheckBox().setSelected(true);
            proc.dispose();
            if (!filesNotAdded.equals("")) {
                //TODO: localisation
                JOptionPane.showMessageDialog(null, "The following files could not be added and were skipped:\n" + filesNotAdded);
            }
            return titleList;
            
        } catch (IOException except) {
            except.printStackTrace();
            return null;
        }
        
        
        
    }

    /**
     * Obtains the list of the possible file extentions for this format.
     *
     * @return A list of extentions separated with commas.
     */
    public static String getFileExtentionsList() {
        return "m3u";
    }
}
