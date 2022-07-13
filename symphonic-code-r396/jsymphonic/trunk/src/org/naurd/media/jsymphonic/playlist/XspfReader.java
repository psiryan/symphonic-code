/*
 * Copyright (C) 2010 Nicolas Cardoso De Castro
 * (nicolas_cardoso@users.sourceforge.net), Daniel Žalar (danizmax@gmail.com).
 * Gabriel Sichardt (jastgasi@gmail.com)
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
 * XspfReader.java
 *
 * Created on May 11, 2010, 23:23 AM
 *
 */
package org.naurd.media.jsymphonic.playlist;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.naurd.media.jsymphonic.title.Title;

/**
 * This class reads XML Shareable Playlist Format (xspf) files from the local filesystem.
 * @author Gabriel Sichardt
 */
public class XspfReader extends Playlist {
    /* CONSTRUCTOR */

    /**
     * Create a XspfReader Object from a File
     * @param sourceFile The file to instance the object from.
     */
    public XspfReader(File sourceFile) {
        super(sourceFile.getName().substring(0, sourceFile.getName().length() - 5), getTitles(sourceFile.getAbsolutePath()));
        setSourceFile(sourceFile);
    }


    /* METHODS */

    /* STATIC METHODS */
    /**
     *
     * @param sourcefile
     * @return
     */
    public static ArrayList getTitles(String sourcefile) {
        /*@param sourcefile The local playlist filename.
         *@return The titles of the playlist in an ArrayList.
         *
         *This method creates an ArrayList from a source XSPF file and returns it.
         *Extended information is skipped and original order will be kept, starting
         *with standard index zero.
         */

        //method variables
        int counter1 = 0;
        int counter2 = 0;
        String line2;   //Line currently read from file
        //TODO: cleanup, error-message, call processing window
        
        
        //end of method varibles


        try {

            //Reading the sourcefile
            File sourcexspf = new File(sourcefile);
            BufferedReader in2 = new BufferedReader(new FileReader(sourcexspf));
            ArrayList titleList = new ArrayList(counter2);


            while ((line2 = in2.readLine()) != null) {
                if (line2.contains("<location>file://")) {	//filtering out lines with extended info

                    String[] cutting = line2.split(":"); //removing <location>file:
                    line2 = cutting[1];
                    line2 = line2.substring(2, line2.length() - 11); //removing // at the beginning and </location> at the end


                    if (line2.startsWith(".")) //create absoulute paths from relative paths
                    {
                        line2 = line2.substring(1);//removing leading dot
                        line2 = sourcexspf.getParent() + line2;//adding the path of the playlist file

                    }


                    // We are processing a line with a file address, that we should instance to a Title object, but to do so, we should create a new file instance
                    // Note that we are instancing a Title object using the "getTitleFromFile" method, which throws an UnknowFileTypeException when the file is not as expected, so this method should be called in a try-catch block
                    try {
                        line2 = line2.replace("%20", " ");
                        File titleFile = new File(line2);

                        if (titleFile.getAbsoluteFile().exists()) {
                            titleList.add(Title.getTitleFromFile(titleFile.getAbsoluteFile()));
                        } else {
                            // The file may have been written in the playlist file as an URL, check that (this may generate an exception)
                            try {
                                URL titleURL = new URL(line2);
                                titleFile = new File(titleURL.toURI());
                                if (titleFile.getAbsoluteFile().exists()) {
                                    titleList.add(Title.getTitleFromFile(titleFile.getAbsoluteFile()));
                                } else {
                                    logger.severe("The file " + line2 + " was not found!");
                                    
                                }
                            } catch (Exception e) {
                                logger.info("The file " + line2 + " is not an URL, and it can't be found!");
                            }
                        }

                    } catch (Exception ex) {
                        // File was not as expected, report the error
                        logger.severe("ERROR while loading a playlist file, the file type is not known. " + ex.getMessage());
                    }

                    counter1++;
                }//end of filter against extended info.

            }//end of reading loop
            System.out.println("end");
            in2.close();
          //  if (!fnotfound.isEmpty())
           // JOptionPane.showMessageDialog(null, "The following files were not found and are thus skipped:"+fnotfound.toString());
            return titleList;

        } catch (IOException except) {
            except.printStackTrace();
            return null;
        }



    }

    /**
     *Obtains the list of the possible file extentions for this format.
     *
     *@return A list of extentions separated with commas.
     */
    public static String getFileExtentionsList() {
        return "xspf";
    }
}
