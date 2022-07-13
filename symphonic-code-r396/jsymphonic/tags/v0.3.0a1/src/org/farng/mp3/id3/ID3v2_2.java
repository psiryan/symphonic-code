package org.farng.mp3.id3;

import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.InvalidTagException;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagConstant;
import org.farng.mp3.TagException;
import org.farng.mp3.TagNotFoundException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

/**
 * Title: ID3v2_2 Description: Class that represents an ID3v2.20 tag Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class ID3v2_2 extends AbstractID3v2 {

    /**
     *
     */
    protected boolean compression = false;
    /**
     *
     */
    protected boolean unsynchronization = false;

    /**
     * Creates a new ID3v2_2 object.
     */
    public ID3v2_2() {
        super();
        setMajorVersion((byte) 2);
        setRevision((byte) 2);
    }

    /**
     * Creates a new ID3v2_2 object.
     */
    public ID3v2_2(final ID3v2_2 copyObject) {
        super(copyObject);
        this.compression = copyObject.compression;
        this.unsynchronization = copyObject.unsynchronization;
    }

    /**
     * Creates a new ID3v2_2 object.
     */
    public ID3v2_2(final AbstractMP3Tag mp3tag) {
        if (mp3tag != null) {
            final ID3v2_4 convertedTag;
            if ((mp3tag instanceof ID3v2_3 == false) && (mp3tag instanceof ID3v2_2 == true)) {
                throw new UnsupportedOperationException("Copy Constructor not called. Please type cast the argument");
            } else if (mp3tag instanceof ID3v2_4) {
                convertedTag = (ID3v2_4) mp3tag;
            } else {
                convertedTag = new ID3v2_4(mp3tag);
            }
            this.compression = convertedTag.compression;
            this.unsynchronization = convertedTag.unsynchronization;
            final AbstractID3v2 id3tag = convertedTag;
            final Iterator iterator = id3tag.getFrameIterator();
            AbstractID3v2Frame frame;
            ID3v2_2Frame newFrame;
            while (iterator.hasNext()) {
                frame = (AbstractID3v2Frame) iterator.next();
                newFrame = new ID3v2_2Frame(frame);
                this.setFrame(newFrame);
            }
        }
    }

    /**
     * Creates a new ID3v2_2 object.
     */
    public ID3v2_2(final RandomAccessFile file) throws TagException, IOException {
        this.read(file);
    }

    public String getIdentifier() {
        return "ID3v2_2.20";
    }

    public int getSize() {
        int size = 3 + 2 + 1 + 4;
        final Iterator iterator = getFrameIterator();
        ID3v2_2Frame frame;
        while (iterator.hasNext()) {
            frame = (ID3v2_2Frame) iterator.next();
            size += frame.getSize();
        }
        return size;
    }

    /**
     * @param tag
     */
    public void append(final AbstractMP3Tag tag) {
        if (tag instanceof ID3v2_2) {
            this.unsynchronization = ((ID3v2_2) tag).unsynchronization;
            this.compression = ((ID3v2_2) tag).compression;
        }
        super.append(tag);
    }

    public boolean equals(final Object obj) {
        if ((obj instanceof ID3v2_2) == false) {
            return false;
        }
        final ID3v2_2 id3v2_2 = (ID3v2_2) obj;
        if (this.compression != id3v2_2.compression) {
            return false;
        }
        if (this.unsynchronization != id3v2_2.unsynchronization) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * @param tag
     */
    public void overwrite(final AbstractMP3Tag tag) {
        if (tag instanceof ID3v2_2) {
            this.unsynchronization = ((ID3v2_2) tag).unsynchronization;
            this.compression = ((ID3v2_2) tag).compression;
        }
        super.overwrite(tag);
    }

    /**
     * @param file
     *
     * @throws TagException
     * @throws IOException
     * @throws TagNotFoundException
     */
    public void read(final RandomAccessFile file) throws TagException, IOException {
        final int size;
        ID3v2_2Frame next;
        final byte[] buffer = new byte[4];
        if (seek(file) == false) {
            throw new TagNotFoundException("ID3v2.20 tag not found");
        }

        // read the major and minor @version number & flags byte
        file.read(buffer, 0, 3);
        if ((buffer[0] != 2) || (buffer[1] != 0)) {
            throw new TagNotFoundException(getIdentifier() + " tag not found");
        }
        setMajorVersion(buffer[0]);
        setRevision(buffer[1]);
        this.unsynchronization = (buffer[2] & TagConstant.MASK_V22_UNSYNCHRONIZATION) != 0;
        this.compression = (buffer[2] & TagConstant.MASK_V22_COMPRESSION) != 0;

        // read the size
        file.read(buffer, 0, 4);
        size = byteArrayToSize(buffer);
        this.clearFrameMap();
        final long filePointer = file.getFilePointer();

        // read all frames
        this.setFileReadBytes(size);
        resetPaddingCounter();
        while ((file.getFilePointer() - filePointer) <= size) {
            try {
                next = new ID3v2_2Frame(file);
                final String id = next.getIdentifier();
                if (this.hasFrame(id)) {
                    this.appendDuplicateFrameId(id + "; ");
                    this.incrementDuplicateBytes(getFrame(id).getSize());
                }
                this.setFrame(next);
            } catch (InvalidTagException ex) {
                if (ex.getMessage().equals("Found empty frame")) {
                    this.incrementEmptyFrameBytes(10);
                } else {
                    this.incrementInvalidFrameBytes();
                }
            }
        }
        this.setPaddingSize(getPaddingCounter());

        /**
         * int newSize = this.getSize(); if ((this.padding + newSize - 10) !=
         * size) { System.out.println("WARNING: Tag sizes don't add up");
         * System.out.println("ID3v2.20 tag size : " + newSize);
         * System.out.println("ID3v2.20 padding : " + this.padding);
         * System.out.println("ID3v2.20 total : " + (this.padding + newSize));
         * System.out.println("ID3v2.20 file size: " + size); }
         */
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public boolean seek(final RandomAccessFile file) throws IOException {
        final byte[] buffer = new byte[3];
        file.seek(0);

        // read the tag if it exists
        file.read(buffer, 0, 3);
        final String tag = new String(buffer, 0, 3);
        if (tag.equals("ID3") == false) {
            return false;
        }

        // read the major and minor @version number
        file.read(buffer, 0, 2);

        // read back the @version bytes so we can read and save them later
        file.seek(file.getFilePointer() - 2);
        return ((buffer[0] == 2) && (buffer[1] == 0));
    }

    public String toString() {
        final Iterator iterator = this.getFrameIterator();
        ID3v2_2Frame frame;
        String str = getIdentifier() + " - " + this.getSize() + " bytes\n";
        str += ("compression        = " + this.compression + "\n");
        str += ("unsynchronization  = " + this.unsynchronization + "\n");
        while (iterator.hasNext()) {
            frame = (ID3v2_2Frame) iterator.next();
            str += (frame.toString() + "\n");
        }
        return str + "\n";
    }

    /**
     * @param tag
     */
    public void write(final AbstractMP3Tag tag) {
        if (tag instanceof ID3v2_2) {
            this.unsynchronization = ((ID3v2_2) tag).unsynchronization;
            this.compression = ((ID3v2_2) tag).compression;
        }
        super.write(tag);
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public void write(final RandomAccessFile file) throws IOException {
        final String str;
        ID3v2_2Frame frame;
        final Iterator iterator;
        final byte[] buffer = new byte[6];
        final MP3File mp3 = new MP3File();
        mp3.seekMP3Frame(file);
        final long mp3start = file.getFilePointer();
        file.seek(0);

        // write the first 10 tag bytes
        str = "ID3";
        for (int i = 0; i < str.length(); i++) {
            buffer[i] = (byte) str.charAt(i);
        }
        buffer[3] = 2;
        buffer[4] = 0;
        if (this.unsynchronization) {
            buffer[5] |= TagConstant.MASK_V22_UNSYNCHRONIZATION;
        }
        if (this.compression) {
            buffer[5] |= TagConstant.MASK_V22_COMPRESSION;
        }
        file.write(buffer);

        //write size;
        file.write(sizeToByteArray((int) mp3start - 10));

        // write all frames
        iterator = this.getFrameIterator();
        while (iterator.hasNext()) {
            frame = (ID3v2_2Frame) iterator.next();
            frame.write(file);
        }
    }
}