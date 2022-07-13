package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.TagUtility;
import org.farng.mp3.object.ObjectID3v2LyricLine;
import org.farng.mp3.object.ObjectLyrics3Line;
import org.farng.mp3.object.ObjectLyrics3TimeStamp;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.3 $
 */
public class FrameBodySYLT extends AbstractID3v2FrameBody {

    /**
     *
     */
    LinkedList lines = new LinkedList();
    /**
     *
     */
    String description = "";
    /**
     *
     */
    String language = "";
    /**
     *
     */
    byte contentType = 0;
    /**
     *
     */
    byte textEncoding = 0;
    /**
     *
     */
    byte timeStampFormat = 0;

    /**
     * Creates a new FrameBodySYLT object.
     */
    public FrameBodySYLT() {
        super();
    }

    /**
     * Creates a new FrameBodySYLT object.
     */
    public FrameBodySYLT(final FrameBodySYLT copyObject) {
        super(copyObject);
        this.description = new String(copyObject.description);
        this.language = new String(copyObject.language);
        this.contentType = copyObject.contentType;
        this.textEncoding = copyObject.textEncoding;
        this.timeStampFormat = copyObject.timeStampFormat;
        ObjectID3v2LyricLine newLine;
        for (int i = 0; i < copyObject.lines.size(); i++) {
            newLine = new ObjectID3v2LyricLine((ObjectID3v2LyricLine) copyObject.lines.get(i));
            this.lines.add(newLine);
        }
    }

    /**
     * Creates a new FrameBodySYLT object.
     */
    public FrameBodySYLT(final byte textEncoding,
                         final String language,
                         final byte timeStampFormat,
                         final byte contentType,
                         final String description) {
        this.textEncoding = textEncoding;
        this.language = language;
        this.timeStampFormat = timeStampFormat;
        this.contentType = contentType;
        this.description = description;
    }

    /**
     * Creates a new FrameBodySYLT object.
     */
    public FrameBodySYLT(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public byte getContentType() {
        return this.contentType;
    }

    public String getDescription() {
        return this.description;
    }

    public String getIdentifier() {
        return "SYLT";
    }

    public String getLanguage() {
        return this.language;
    }

    public String getLyric() {
        String lyrics = "";
        for (int i = 0; i < this.lines.size(); i++) {
            lyrics += this.lines.get(i);
        }
        return lyrics;
    }

    public int getSize() {
        int size;
        size = 1 + 3 + 1 + 1 + this.description.length();
        for (int i = 0; i < this.lines.size(); i++) {
            size += ((ObjectID3v2LyricLine) this.lines.get(i)).getSize();
        }
        return size;
    }

    public byte getTextEncoding() {
        return this.textEncoding;
    }

    public byte getTimeStampFormat() {
        return this.timeStampFormat;
    }

    /**
     * @param timeStamp
     * @param text
     */
    public void addLyric(final int timeStamp, final String text) {
        final ObjectID3v2LyricLine line = new ObjectID3v2LyricLine("Lyric Line");
        line.setTimeStamp(timeStamp);
        line.setText(text);
        this.lines.add(line);
    }

    /**
     * @param line
     */
    public void addLyric(final ObjectLyrics3Line line) {
        final Iterator iterator = line.getTimeStamp();
        ObjectLyrics3TimeStamp timeStamp;
        final String lyric = line.getLyric();
        long time;
        final ObjectID3v2LyricLine id3Line;
        id3Line = new ObjectID3v2LyricLine("Lyric Line");
        if (iterator.hasNext() == false) {
            // no time stamp, give it 0
            time = 0;
            id3Line.setTimeStamp(time);
            id3Line.setText(lyric);
            this.lines.add(id3Line);
        } else {
            while (iterator.hasNext()) {
                timeStamp = (ObjectLyrics3TimeStamp) iterator.next();
                time = (timeStamp.getMinute() * 60) + timeStamp.getSecond(); // seconds
                time *= 1000; // milliseconds
                id3Line.setTimeStamp(time);
                id3Line.setText(lyric);
                this.lines.add(id3Line);
            }
        }
    }

    /**
     * This method is not yet supported.
     *
     * @throws java.lang.UnsupportedOperationException
     *          This method is not yet supported
     */
    public void equals() {
        // todo Implement this java.lang.Object method
        throw new java.lang.UnsupportedOperationException("Method equals() not yet implemented.");
    }

    public Iterator iterator() {
        return this.lines.iterator();
    }

    protected void setupObjectList() {
//        throw new UnsupportedOperationException();
    }

    /**
     * @param file
     *
     * @throws IOException
     * @throws InvalidTagException
     */
    public void read(final RandomAccessFile file) throws IOException, InvalidTagException {
        final int size;
        final int delim;
        int offset = 0;
        final byte[] buffer;
        final String str;
        size = readHeader(file);
        buffer = new byte[size];
        file.read(buffer);
        str = new String(buffer);
        this.textEncoding = buffer[offset++];
        this.language = str.substring(offset, offset + 3);
        offset += 3;
        this.timeStampFormat = buffer[offset++];
        this.contentType = buffer[offset++];
        delim = str.indexOf(0, offset);
        this.description = str.substring(offset, delim);
        offset = delim + 1;
        final byte[] data = new byte[size - offset];
        System.arraycopy(buffer, offset, data, 0, size - offset);
        readByteArray(data);
    }

    /**
     * @param arr
     */
    public void readByteArray(final byte[] arr) {
        int offset = 0;
        int delim;
        byte[] line;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 0) {
                delim = i;
                line = new byte[offset - delim + 4];
                System.arraycopy(arr, offset, line, 0, offset - delim + 4);
                this.lines.add(new ObjectID3v2LyricLine("Lyric Line"));
                i += 4;
                offset += 4;
            }
        }
    }

    public String toString() {
        String str;
        str = getIdentifier() + " " + this
                .textEncoding + " " + this
                .language + " " + this
                .timeStampFormat + " " + this
                .contentType + " " + this
                .description;
        for (int i = 0; i < this.lines.size(); i++) {
            str += (this.lines.get(i)).toString();
        }
        return str;
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public void write(final RandomAccessFile file) throws IOException {
        final byte[] buffer;
        int offset = 0;
        writeHeader(file, this.getSize());
        buffer = new byte[this.getSize()];
        buffer[offset++] = this.textEncoding; // text encoding;
        this.language = TagUtility.truncate(this.language, 3);
        for (int i = 0; i < this.language.length(); i++) {
            buffer[i + offset] = (byte) this.language.charAt(i);
        }
        offset += this.language.length();
        buffer[offset++] = this.timeStampFormat;
        buffer[offset++] = this.contentType;
        for (int i = 0; i < this.description.length(); i++) {
            buffer[i + offset] = (byte) this.description.charAt(i);
        }
        offset += this.description.length();
        buffer[offset++] = 0; // null character
        System.arraycopy(writeByteArray(), 0, buffer, offset, buffer.length - offset);
        file.write(buffer);
    }

    public byte[] writeByteArray() {
        final byte[] arr;
        ObjectID3v2LyricLine line = null;
        int offset = 0;
        int size = 0;
        for (int i = 0; i < this.lines.size(); i++) {
            line = (ObjectID3v2LyricLine) this.lines.get(i);
            size += line.getSize();
        }
        arr = new byte[size];
        for (int i = 0; i < this.lines.size(); i++) {
            line = (ObjectID3v2LyricLine) this.lines.get(i);
        }
        if (line != null) {
            System.arraycopy(line.writeByteArray(), 0, arr, offset, line.getSize());
            offset += line.getSize();
        }
        return arr;
    }
}