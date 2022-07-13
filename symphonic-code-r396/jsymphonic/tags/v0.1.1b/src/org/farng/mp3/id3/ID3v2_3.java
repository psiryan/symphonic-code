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
 * Title: ID3v2_3 Description: This class represents an ID3v2.30 tag Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class ID3v2_3 extends ID3v2_2 {

    /**
     *
     */
    protected boolean crcDataFlag = false;
    /**
     *
     */
    protected boolean experimental = false;
    /**
     *
     */
    protected boolean extended = false;
    /**
     *
     */
    protected int crcData = 0;
    /**
     *
     */
    protected int paddingSize = 0;

    /**
     * Creates a new ID3v2_3 object.
     */
    public ID3v2_3() {
        setMajorVersion((byte) 2);
        setRevision((byte) 3);
    }

    /**
     * Creates a new ID3v2_3 object.
     */
    public ID3v2_3(final ID3v2_3 copyObject) {
        super(copyObject);
        this.crcDataFlag = copyObject.crcDataFlag;
        this.experimental = copyObject.experimental;
        this.extended = copyObject.extended;
        this.crcData = copyObject.crcData;
        this.paddingSize = copyObject.paddingSize;
    }

    /**
     * Creates a new ID3v2_3 object.
     */
    public ID3v2_3(final AbstractMP3Tag mp3tag) {
        if (mp3tag != null) {
            final ID3v2_4 convertedTag;
            if ((mp3tag instanceof ID3v2_4 == false) && (mp3tag instanceof ID3v2_3 == true)) {
                throw new UnsupportedOperationException("Copy Constructor not called. Please type cast the argument");
            }
            if (mp3tag instanceof ID3v2_4) {
                convertedTag = (ID3v2_4) mp3tag;
            } else {
                convertedTag = new ID3v2_4(mp3tag);
            }
            this.extended = convertedTag.extended;
            this.experimental = convertedTag.experimental;
            this.crcDataFlag = convertedTag.crcDataFlag;
            this.crcData = convertedTag.crcData;
            this.paddingSize = convertedTag.paddingSize;
            this.compression = convertedTag.compression;
            this.unsynchronization = convertedTag.unsynchronization;
            final AbstractID3v2 id3tag = convertedTag;
            final Iterator iterator = id3tag.getFrameIterator();
            AbstractID3v2Frame frame;
            ID3v2_3Frame newFrame;
            while (iterator.hasNext()) {
                frame = (AbstractID3v2Frame) iterator.next();
                newFrame = new ID3v2_3Frame(frame);
                this.setFrame(newFrame);
            }
        }
    }

    /**
     * Creates a new ID3v2_3 object.
     */
    public ID3v2_3(final RandomAccessFile file) throws TagException, IOException {
        this.read(file);
    }

    public String getIdentifier() {
        return "ID3v2.30";
    }

    public int getSize() {
        int size = 3 + 2 + 1 + 4;
        if (this.extended) {
            if (this.crcDataFlag) {
                size += (4 + 2 + 4 + 4);
            } else {
                size += (4 + 2 + 4);
            }
        }
        final Iterator iterator = this.getFrameIterator();
        AbstractID3v2Frame frame;
        while (iterator.hasNext()) {
            frame = (AbstractID3v2Frame) iterator.next();
            size += frame.getSize();
        }
        return size;
    }

    /**
     * @param tag
     */
    public void append(final AbstractMP3Tag tag) {
        if (tag instanceof ID3v2_3) {
            this.experimental = ((ID3v2_3) tag).experimental;
            this.extended = ((ID3v2_3) tag).extended;
            this.crcDataFlag = ((ID3v2_3) tag).crcDataFlag;
            this.paddingSize = ((ID3v2_3) tag).paddingSize;
            this.crcData = ((ID3v2_3) tag).crcData;
        }
        super.append(tag);
    }

    public boolean equals(final Object obj) {
        if ((obj instanceof ID3v2_3) == false) {
            return false;
        }
        final ID3v2_3 id3v2_3 = (ID3v2_3) obj;
        if (this.crcData != id3v2_3.crcData) {
            return false;
        }
        if (this.crcDataFlag != id3v2_3.crcDataFlag) {
            return false;
        }
        if (this.experimental != id3v2_3.experimental) {
            return false;
        }
        if (this.extended != id3v2_3.extended) {
            return false;
        }
        if (this.paddingSize != id3v2_3.paddingSize) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * @param tag
     */
    public void overwrite(final AbstractMP3Tag tag) {
        if (tag instanceof ID3v2_3) {
            this.experimental = ((ID3v2_3) tag).experimental;
            this.extended = ((ID3v2_3) tag).extended;
            this.crcDataFlag = ((ID3v2_3) tag).crcDataFlag;
            this.paddingSize = ((ID3v2_3) tag).paddingSize;
            this.crcData = ((ID3v2_3) tag).crcData;
        }
        super.overwrite(tag);
    }

    /**
     * @param file
     *
     * @throws TagException
     * @throws IOException
     * @throws TagNotFoundException
     * @throws InvalidTagException
     */
    public void read(final RandomAccessFile file) throws TagException, IOException {
        final int size;
        final byte[] buffer = new byte[4];
        if (seek(file) == false) {
            throw new TagNotFoundException(getIdentifier() + " tag not found");
        }

        // read the major and minor @version number & flags byte
        file.read(buffer, 0, 3);
        if ((buffer[0] != 3) || (buffer[1] != 0)) {
            throw new TagNotFoundException(getIdentifier() + " tag not found");
        }
        setMajorVersion(buffer[0]);
        setRevision(buffer[1]);
        this.unsynchronization = (buffer[2] & TagConstant.MASK_V23_UNSYNCHRONIZATION) != 0;
        this.extended = (buffer[2] & TagConstant.MASK_V23_EXTENDED_HEADER) != 0;
        this.experimental = (buffer[2] & TagConstant.MASK_V23_EXPERIMENTAL) != 0;

        // read the size
        file.read(buffer, 0, 4);
        size = byteArrayToSize(buffer);
        final long filePointer = file.getFilePointer();
        if (this.extended) {
            // int is 4 bytes.
            final int extendedHeaderSize = file.readInt();

            // the extended header is only 6 or 10 bytes.
            if (extendedHeaderSize != 6 && extendedHeaderSize != 10) {
                throw new InvalidTagException("Invalid Extended Header Size.");
            }
            file.read(buffer, 0, 2);
            this.crcDataFlag = (buffer[0] & TagConstant.MASK_V23_CRC_DATA_PRESENT) != 0;

            // if it's 10 bytes, the CRC flag must be set
            // and if it's 6 bytes, it must not be set
            if (((extendedHeaderSize == 10) && (this.crcDataFlag == false)) ||
                ((extendedHeaderSize == 6) && (this.crcDataFlag == true))) {
                throw new InvalidTagException("CRC Data flag not set correctly.");
            }
            this.paddingSize = file.readInt();
            if ((extendedHeaderSize == 10) && this.crcDataFlag) {
                this.crcData = file.readInt();
            }
        }
        ID3v2_3Frame next;
        this.clearFrameMap();

        // read all the frames.
        this.setFileReadBytes(size);
        AbstractID3v2.resetPaddingCounter();
        while ((file.getFilePointer() - filePointer) <= size) {
            try {
                next = new ID3v2_3Frame(file);
                final String id = next.getIdentifier();
                if (this.hasFrame(id)) {
                    this.appendDuplicateFrameId(id + "; ");
                    this.incrementDuplicateBytes(this.getFrame(id).getSize());
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
        return ((buffer[0] == 3) && (buffer[1] == 0));
    }

    public String toString() {
        final Iterator iterator = this.getFrameIterator();
        AbstractID3v2Frame frame;
        String str = getIdentifier() + " " + this.getSize() + "\n";
        str += ("compression        = " + this.compression + "\n");
        str += ("unsynchronization  = " + this.unsynchronization + "\n");
        str += ("crcData            = " + this.crcData + "\n");
        str += ("crcDataFlag        = " + this.crcDataFlag + "\n");
        str += ("experimental       = " + this.experimental + "\n");
        str += ("extended           = " + this.extended + "\n");
        str += ("paddingSize        = " + this.paddingSize + "\n");
        while (iterator.hasNext()) {
            frame = (ID3v2_3Frame) iterator.next();
            str += (frame.toString() + "\n");
        }
        return str + "\n";
    }

    /**
     * @param tag
     */
    public void write(final AbstractMP3Tag tag) {
        if (tag instanceof ID3v2_3) {
            this.experimental = ((ID3v2_3) tag).experimental;
            this.extended = ((ID3v2_3) tag).extended;
            this.crcDataFlag = ((ID3v2_3) tag).crcDataFlag;
            this.paddingSize = ((ID3v2_3) tag).paddingSize;
            this.crcData = ((ID3v2_3) tag).crcData;
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
        final Iterator iterator;
        final byte[] buffer = new byte[6];
        final MP3File mp3 = new MP3File();
        mp3.seekMP3Frame(file);
        final long mp3start = file.getFilePointer();
        file.seek(0);
        ID3v2_3Frame frame;
        str = "ID3";
        for (int i = 0; i < str.length(); i++) {
            buffer[i] = (byte) str.charAt(i);
        }
        buffer[3] = 3;
        buffer[4] = 0;
        if (this.unsynchronization) {
            buffer[5] |= TagConstant.MASK_V23_UNSYNCHRONIZATION;
        }
        if (this.extended) {
            buffer[5] |= TagConstant.MASK_V23_EXTENDED_HEADER;
        }
        if (this.experimental) {
            buffer[5] |= TagConstant.MASK_V23_EXPERIMENTAL;
        }
        file.write(buffer);

        // write size
        file.write(sizeToByteArray((int) mp3start - 10));
        if (this.extended) {
            if (this.crcDataFlag) {
                file.writeInt(10);
                buffer[0] = 0;
                buffer[0] |= TagConstant.MASK_V23_CRC_DATA_PRESENT;
                file.write(buffer, 0, 2);
                file.writeInt(this.paddingSize);
                file.writeInt(this.crcData);
            } else {
                file.writeInt(6);
                file.write(buffer, 0, 2);
                file.writeInt(this.paddingSize);
            }
        }

        // write all frames
        iterator = this.getFrameIterator();
        while (iterator.hasNext()) {
            frame = (ID3v2_3Frame) iterator.next();
            frame.write(file);
        }
    }
}