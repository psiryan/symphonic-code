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
 * DataBaseEsysToolBox.java
 *
 * Created on 3 juin 2009, 15:32:27
 *
 */

package org.naurd.media.jsymphonic.toolBox;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.naurd.media.jsymphonic.device.sony.nw.DataBaseEsys;
import org.naurd.media.jsymphonic.title.Title;

/**
 * Provides basic function to write the data base on the Sony devices. This tool box should be replaced by the Sony Library coded in C++ when it will be ready.
 * This tool box is dedicated to ESYS database.
 *
 * @author skiron
 */
public class DataBaseEsysToolBox {
/* FIELDS */
    // Other
    private static Logger logger = Logger.getLogger("org.naurd.media.jsymphonic.toolBox.OmaDataBaseToolBox");

    // CONSTANT
    private final static int PBLIST0 = 0;
    private final static int PBLIST1 = 1;


/* CONSTRUCTORS */
    /**
     * This class is a tool box, it shouldn't be instanced.
     *
     * @author nicolas_cardoso
     */
    public DataBaseEsysToolBox() {
    }


/* METHODS */

    public static void writePBLIST0(File databaseDir, JSymphonicMap titles) throws IOException {
        File pblist0 = new File(databaseDir + File.separator +"PBLIST0.DAT");
        RandomAccessFile rafPblist0;

        // Create a new file
        pblist0.createNewFile();

        // Write data in the file :
        rafPblist0 = new RandomAccessFile(pblist0, "rw"); //Open the file in RAF

        // Header
        WriteHeader(rafPblist0, PBLIST0, 0); //Write table header

        // TODO, I assume here that only one album can be written
        String theAlbumName = "Only one album";
        DataBaseOmgaudioToolBox.WriteString16(rafPblist0, theAlbumName);

        // Fill the file with zeros
        DataBaseOmgaudioToolBox.WriteZeros(rafPblist0, 0x100 - theAlbumName.length()*2);
    }

    public static void writePBLIST1(File databaseDir, Map titles) throws IOException {
        File pblist1 = new File(databaseDir + File.separator +"PBLIST1.DAT");
        RandomAccessFile rafPblist1;

        // Create a new file
        pblist1.createNewFile();

        // Write data in the file :
        rafPblist1 = new RandomAccessFile(pblist1, "rw"); //Open the file in RAF

        // Compute the number of elements to write the header
        List sortedTitles = DataBaseOmgaudioToolBox.sortByTitleId(titles);
        WriteHeader(rafPblist1, PBLIST1, sortedTitles.size()); //Write table header

        // TODO, I assume here that only one album can be written
        String theAlbumName = "Only one album";
        DataBaseOmgaudioToolBox.WriteString16(rafPblist1, theAlbumName);

        // Fill the file with zeros
        DataBaseOmgaudioToolBox.WriteZeros(rafPblist1, 0xF0 - theAlbumName.length()*2);

        // Write the album description
        DataBaseOmgaudioToolBox.WriteZeros(rafPblist1, 0xE); // An empty beginning of line
        byte[] descConstant = {(byte)0x1, (byte)0x20, (byte)0x0, (byte)0x1}; // A constant
        rafPblist1.write(descConstant);
        if(sortedTitles.size() == 1){
            // If there is only one element in the database, write 0
            rafPblist1.write(DataBaseOmgaudioToolBox.int2bytes(0, 2));
        }
        else{
            // Else, write the number of elements
            rafPblist1.write(DataBaseOmgaudioToolBox.int2bytes(((JSymphonicMap)titles).maxValue(), 2));
        }
        DataBaseOmgaudioToolBox.WriteZeros(rafPblist1, 0xC); // Finish the line with zeros

        // Then, write the title info
        for(int i=1; i <= ((JSymphonicMap)titles).maxValue(); i++){
            // If the title ID is not used, fill in the space with zeros
            if(!titles.containsValue(i)){
                DataBaseOmgaudioToolBox.WriteZeros(rafPblist1, 3*0x100);
            }
            else{
                // Write the file info
                Title t = (Title) ((JSymphonicMap)titles).getKey(i);
                String artist = t.getArtist();
                String titleName = t.getTitle();

                // Write the file name
                String fileName = artist + " - " + titleName + ".mp3"; // Fake the file name
                DataBaseOmgaudioToolBox.WriteString16(rafPblist1, fileName);
                // Fill the file with zeros
                DataBaseOmgaudioToolBox.WriteZeros(rafPblist1, 0x100 - fileName.length()*2);

                // Write the titleName
                DataBaseOmgaudioToolBox.WriteString16(rafPblist1, titleName);
                // Fill the file with zeros
                DataBaseOmgaudioToolBox.WriteZeros(rafPblist1, 0x100 - titleName.length()*2);

                // Write the title
                DataBaseOmgaudioToolBox.WriteString16(rafPblist1, artist);
                // Fill the file with zeros
                DataBaseOmgaudioToolBox.WriteZeros(rafPblist1, 0x100 - artist.length()*2);
            }
        }
    }


    private static void WriteHeader(RandomAccessFile raf, int fileType, int nbOfElements) throws IOException {
        // Write file ID
        byte[] bytes8 = {(byte)0x57,(byte)0x4D,(byte)0x50,(byte)0x4C, (byte)0x45,(byte)0x53,(byte)0x59,(byte)0x53}; // "W","M","P","L","E","S","Y","S"
        raf.write(bytes8); // Write label

        // Write first constant
        raf.write(DataBaseEsys.HEADER_CST_1);

        // Write keys
        raf.write(DataBaseEsys.PLAYER_KEY);
        raf.write(DataBaseEsys.DATABASE_KEY);

        // Write second constant
        raf.write(DataBaseEsys.HEADER_CST_2);

        // Write the number of elements
        raf.write(DataBaseOmgaudioToolBox.int2bytes(nbOfElements, 4));

        // Write third constant
        raf.write(DataBaseEsys.HEADER_CST_3);

        // Write last constant, depending on the file
        if(fileType == PBLIST0){
            raf.write(DataBaseEsys.HEADER_CST_PBLIST0);
        }
        else{
            raf.write(DataBaseEsys.HEADER_CST_PBLIST1);
        }
    }
}
