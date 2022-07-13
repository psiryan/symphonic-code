package org.farng.mp3.lyrics3;

import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.TagException;
import org.farng.mp3.TagNotFoundException;
import org.farng.mp3.id3.ID3v1;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Lyrics3 Description: This class represents a Lyrics3 1.00 tag Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.1 $
 */
public abstract class AbstractLyrics3 extends AbstractMP3Tag {

    /**
     * Creates a new AbstractLyrics3 object.
     */
    public AbstractLyrics3() {
        super();
    }

    /**
     * Creates a new AbstractLyrics3 object.
     */
    public AbstractLyrics3(final AbstractLyrics3 copyObject) {
        super(copyObject);
    }

    /**
     * @param file
     *
     * @throws IOException
     * @throws TagException
     */
    public void append(final RandomAccessFile file) throws IOException, TagException {
        AbstractLyrics3 oldTag;
        try {
            oldTag = new Lyrics3v2(file);
            oldTag.append(this);
            oldTag.write(file);
        } catch (TagNotFoundException ex) {
            try {
                oldTag = new Lyrics3v1(file);
                oldTag.append(this);
                oldTag.write(file);
            } catch (TagNotFoundException ex2) {
                this.write(file);
            }
        }
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public void delete(final RandomAccessFile file) throws IOException {
        long filePointer;
        ID3v1 id3v1tag = new ID3v1();
        if (seek(file)) {
            id3v1tag = id3v1tag.getID3tag(file);
            seek(file);
            filePointer = file.getFilePointer();

            // cut off the "LYRICSBEGIN"
            filePointer -= 11;
            file.setLength(filePointer);
            file.seek(file.length());
            if (id3v1tag != null) {
                id3v1tag.write(file);
            }
        }
    }

    /**
     * @param file
     *
     * @throws IOException
     * @throws TagException
     */
    public void overwrite(final RandomAccessFile file) throws IOException, TagException {
        AbstractLyrics3 oldTag;
        try {
            oldTag = new Lyrics3v2(file);
            oldTag.overwrite(this);
            oldTag.write(file);
        } catch (TagNotFoundException ex) {
            try {
                oldTag = new Lyrics3v1(file);
                oldTag.overwrite(this);
                oldTag.write(file);
            } catch (TagNotFoundException ex2) {
                this.write(file);
            }
        }
    }
}