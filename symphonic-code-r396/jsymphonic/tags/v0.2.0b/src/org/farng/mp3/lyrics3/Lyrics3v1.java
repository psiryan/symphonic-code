package org.farng.mp3.lyrics3;

import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.TagNotFoundException;
import org.farng.mp3.TagOptionSingleton;
import org.farng.mp3.TagUtility;
import org.farng.mp3.id3.ID3v1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class Lyrics3v1 extends AbstractLyrics3 {

    /**
     *
     */
    private String lyric = "";

    /**
     * Creates a new Lyrics3v1 object.
     */
    public Lyrics3v1() {
        super();
    }

    /**
     * Creates a new Lyrics3v1 object.
     */
    public Lyrics3v1(final Lyrics3v1 copyObject) {
        super(copyObject);
        this.lyric = new String(copyObject.lyric);
    }

    /**
     * Creates a new Lyrics3v1 object.
     */
    public Lyrics3v1(final AbstractMP3Tag mp3Tag) {
        if (mp3Tag != null) {
            final Lyrics3v2 lyricTag;
            if (mp3Tag instanceof Lyrics3v1) {
                throw new UnsupportedOperationException("Copy Constructor not called. Please type cast the argument");
            } else if (mp3Tag instanceof Lyrics3v2) {
                lyricTag = (Lyrics3v2) mp3Tag;
            } else {
                lyricTag = new Lyrics3v2(mp3Tag);
            }
            final FieldBodyLYR lyricField;
            lyricField = (FieldBodyLYR) lyricTag.getField("LYR").getBody();
            this.lyric = new String(lyricField.getLyric());
        }
    }

    /**
     * Creates a new Lyrics3v1 object.
     */
    public Lyrics3v1(final RandomAccessFile file) throws TagNotFoundException, java.io.IOException {
        this.read(file);
    }

    public String getIdentifier() {
        return "Lyrics3v1.00";
    }

    /**
     * @param lyric
     */
    public void setLyric(final String lyric) {
        this.lyric = TagUtility.truncate(lyric, 5100);
    }

    public String getLyric() {
        return this.lyric;
    }

    public int getSize() {
        return "LYRICSBEGIN".length() + this.lyric.length() + "LYRICSEND".length();
    }

    public boolean isSubsetOf(final Object object) {
        if ((object instanceof Lyrics3v1) == false) {
            return false;
        }
        return (((Lyrics3v1) object).lyric.indexOf(this.lyric) >= 0);
    }

    /**
     * @param tag
     */
    public void append(final AbstractMP3Tag tag) {
        final Lyrics3v1 oldTag = this;
        final Lyrics3v1 newTag;
        if (tag != null) {
            if (tag instanceof Lyrics3v1) {
                newTag = (Lyrics3v1) tag;
            } else {
                newTag = new Lyrics3v1();
            }
            this.lyric = oldTag.lyric + "\n" + newTag.lyric;
        }
    }

    public boolean equals(final Object obj) {
        if ((obj instanceof Lyrics3v1) == false) {
            return false;
        }
        final Lyrics3v1 lyrics3v1 = (Lyrics3v1) obj;
        if (this.lyric.equals(lyrics3v1.lyric) == false) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * @throws java.lang.UnsupportedOperationException
     *
     */
    public Iterator iterator() {
        // todo Implement this org.farng.mp3.AbstractMP3Tag abstract method
        throw new java.lang.UnsupportedOperationException("Method iterator() not yet implemented.");
    }

    /**
     * @param tag
     */
    public void overwrite(final AbstractMP3Tag tag) {
        final Lyrics3v1 oldTag = this;
        final Lyrics3v1 newTag;
        if (tag != null) {
            if (tag instanceof Lyrics3v1) {
                newTag = (Lyrics3v1) tag;
            } else {
                newTag = new Lyrics3v1();
            }
            this.lyric = TagOptionSingleton.getInstance().isLyrics3Save() ? newTag.lyric : oldTag.lyric;
        }
    }

    /**
     * @param file
     *
     * @throws TagNotFoundException
     * @throws IOException
     */
    public void read(final RandomAccessFile file) throws TagNotFoundException, IOException {
        final byte[] buffer = new byte[5100 + 9 + 11];
        final String lyricBuffer;
        if (seek(file) == false) {
            throw new TagNotFoundException("ID3v1 tag not found");
        }
        file.read(buffer);
        lyricBuffer = new String(buffer);
        this.lyric = lyricBuffer.substring(0, lyricBuffer.indexOf("LYRICSEND"));
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public boolean seek(final RandomAccessFile file) throws IOException {
        final byte[] buffer = new byte[5100 + 9 + 11];
        String lyricsEnd;
        final String lyricsStart;
        long offset;

        // check right before the ID3 1.0 tag for the lyrics tag
        file.seek(file.length() - 128 - 9);
        file.read(buffer, 0, 9);
        lyricsEnd = new String(buffer, 0, 9);
        if (lyricsEnd.equals("LYRICSEND")) {
            offset = file.getFilePointer();
        } else {
            // check the end of the file for a lyrics tag incase an ID3
            // tag wasn't placed after it.
            file.seek(file.length() - 9);
            file.read(buffer, 0, 9);
            lyricsEnd = new String(buffer, 0, 9);
            if (lyricsEnd.equals("LYRICSEND")) {
                offset = file.getFilePointer();
            } else {
                return false;
            }
        }

        // the tag can at most only be 5100 bytes
        offset -= (5100 + 9 + 11);
        file.seek(offset);
        file.read(buffer);
        lyricsStart = new String(buffer);

        // search for the tag
        final int i = lyricsStart.indexOf("LYRICSBEGIN");
        if (i == -1) {
            return false;
        }
        file.seek(offset + i + 11);
        return true;
    }

    public String toString() {
        final String str = getIdentifier() + " " + this.getSize() + "\n";
        return str + this.lyric;
    }

    /**
     * @param tag
     */
    public void write(final AbstractMP3Tag tag) {
        final Lyrics3v1 newTag;
        if (tag != null) {
            if (tag instanceof Lyrics3v1) {
                newTag = (Lyrics3v1) tag;
            } else {
                newTag = new Lyrics3v1();
            }
            this.lyric = newTag.lyric;
        }
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public void write(final RandomAccessFile file) throws IOException {
        String str;
        int offset;
        final byte[] buffer;
        final ID3v1 id3v1tag;
        id3v1tag = (new ID3v1()).getID3tag(file);
        delete(file);
        file.seek(file.length());
        buffer = new byte[this.lyric.length() + 11 + 9];
        str = "LYRICSBEGIN";
        for (int i = 0; i < str.length(); i++) {
            buffer[i] = (byte) str.charAt(i);
        }
        offset = str.length();
        str = TagUtility.truncate(this.lyric, 5100);
        for (int i = 0; i < str.length(); i++) {
            buffer[i + offset] = (byte) str.charAt(i);
        }
        offset += str.length();
        str = "LYRICSEND";
        for (int i = 0; i < str.length(); i++) {
            buffer[i + offset] = (byte) str.charAt(i);
        }
        offset += str.length();
        file.write(buffer, 0, offset);
        if (id3v1tag != null) {
            id3v1tag.write(file);
        }
    }
}