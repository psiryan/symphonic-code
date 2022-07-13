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
 * Wmmp.java
 *
 * Created on June 3, 2009, 11:25 AM
 *
 */

package org.naurd.media.jsymphonic.title;

import java.io.File;
import java.io.FileOutputStream;
import org.naurd.media.jsymphonic.device.sony.nw.DataBaseEsys;
import org.naurd.media.jsymphonic.toolBox.DataBaseOmgaudioToolBox;

/**
 * This class describe an audio file within the ESYS folder (coresponding to generations 1 and 2).
 * This file is a MP3 file wrapped into something strange...
 * The ID3 tag v1 and v2 are removed from the original file.
 * A wmmp header is added.
 * The file (except the header) is encrypted using a "one byte" key, used as a mask.
 *
 * @author nicolas_cardoso
 */
public class Wmmp extends Title{
/* FIELDS */
    protected byte mpegVersion = 3; // Mpeg version of the file (only used when wrapping MP3)
    protected byte layer = 1; // Layer of the file (only used when wrapping MP3)
    protected long framesNumber; // Number of frames in the MP3 file (only used when wrapping MP3)

    /**
     * Creates a new instance of a Wmmp title from title, album and artist info.
     *
     * @param title The title name of the title.
     * @param album The album name of the title.
     * @param artist The artist name of the title.
     */
    public Wmmp(File sourceFile, String title, String album, String artist) {
        super();
        format = Title.WMMP;
        
        this.sourceFile = sourceFile;

        titleName = title;
        artistName = artist;
        albumName = album;
    }
  /**
     * Create an instance from a file and an existing title (used when importing files to device, to wrap an existing title to a new Wmmp object).
     *
     * @param file The file to create the instance from (will only be used as a source).
     * @param title The title to create the instance from (all data will be read from that title).
     */
    public Wmmp(File file, Title title){
        // Set files info
        sourceFile = file;
        format = Title.WMMP;
        fileSize = title.size() - title.getNbOfBytesBeforeMusic();

        // Other info are read from the title
        titleName= title.getTitle();
        artistName = title.getArtist();
        albumName = title.getAlbum();
        genre = title.getGenre();
        titleNumber = title.getTitleNumber();
        year = title.getYear();
        length = title.getLength();
        vbr = title.getVbr();
        bitRate = title.getBitRate();
        frequency = title.getFrequency();
        status = title.getStatus();
        nbOfBytesBeforeMusic = title.getNbOfBytesBeforeMusic();
        nbChannels = title.getNbChannels();

        if(title instanceof Mp3){
            // If wrapped title is an MP3, we should read the layer and the mpegVersion
            Mp3 mp3 = (Mp3) title;
            mpegVersion = mp3.getMpegVersion();
            layer = mp3.getLayer();
            framesNumber = mp3.getFramesNumber();
        }

        if(title instanceof Oma){
            // If title is an OMA, we should read the OMA info (note that we don't know what is the wrapped format, it could be a MP3 wrapped file directly taken from a OMGAUDIO folder)
/* TODO           Oma oma = (Oma) title;
            formatWrapped = oma.getFormatWrapped();
            omaInfo = oma.getOmaInfo();
            version = oma.getVersion();
            mpegVersion = oma.getMpegVersion();
            layer = oma.getLayer();
            framesNumber = oma.getFramesNumber();
            drm = oma.getDrm();
            drmKey = oma.getDrmKey();*/
        }
    }

/* STATIC METHODS */

/* GET METHODS */

    /**
     *Obtains the format of the title as a string.
     *
     *@return the format of the title as a string.
     */
    @Override
    public String getFormatAsString() {
        return "MP3";
    }

    @Override
    public int getNbOfBytesBeforeMusic() {
        return 0x20;
    }

/* METHODS */

    @Override public void writeTagInfoToFile(FileOutputStream stream, boolean gotKey) throws Exception{
        //Title name
        String label = "WMMP";
        stream.write(label.getBytes()); // Write the file ID

        // Write the file size ( it's the actual size plus the header length)
        stream.write(DataBaseOmgaudioToolBox.int2bytes((int) fileSize + 0x20 , 4));

        // Then write the track length in milliseconds
        stream.write(DataBaseOmgaudioToolBox.int2bytes(length, 4));

        // And finally the number of frames. If the number of frame is not available, it can be computed. But here, we have a lib that already does the job
        if(framesNumber == 0){
            // Only compute the number of frame is it's zero
            // To have if need to make some computation
                // CODE FROM ML_SONY
                //int  SAMPLING_RATES[] = {11025, 12000, 8000, 0, 0, 0, 22050, 24000, 16000, 44100, 48000, 32000};

                //sample per frame 0=reserved
                //          MPG2.5 res        MPG2   MPG1
                //reserved  0      0          0      0
                //Layer III 576    0          576    1152
                //Layer II  1152   0          1152   1152
                //Layer I   384    0          384    384

                //int samplingRate = SAMPLING_RATES[(mpegVersion * 3) + samplingRateIndex];

            int  SAMPLE_PER_FRAME[] = {0,576,1152,384,0,0,0,0,0,576,1152,384,0,1152,1152,384};

            double samplingRate = frequency;
            int samplePerFrame = SAMPLE_PER_FRAME[(mpegVersion * 4) + layer]; // Compute the sample per frame
            // Compute the number of frames
            framesNumber = (int)((length * samplingRate) / samplePerFrame); // frequency is in kHz, don't really know why, but it should be kept as it is to have a correct result
        }
        // Write the number of frames
        stream.write(DataBaseOmgaudioToolBox.int2bytes((int)framesNumber, 4));

        // Write the keys
        stream.write(DataBaseEsys.PLAYER_KEY);
        stream.write(DataBaseEsys.DATABASE_KEY);

        // Unknown constant
        byte[] unknownConstant = {(byte)0x01};
        stream.write(unknownConstant);

        // Constant
        byte[] constant = {(byte)0x01};
        stream.write(constant);
        DataBaseOmgaudioToolBox.WriteZeros(stream, 0x0A);
    }
}
