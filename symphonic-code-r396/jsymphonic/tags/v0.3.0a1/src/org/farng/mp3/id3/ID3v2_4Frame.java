package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.TagConstant;
import org.farng.mp3.TagUtility;
import org.farng.mp3.lyrics3.FieldBodyAUT;
import org.farng.mp3.lyrics3.FieldBodyEAL;
import org.farng.mp3.lyrics3.FieldBodyEAR;
import org.farng.mp3.lyrics3.FieldBodyETT;
import org.farng.mp3.lyrics3.FieldBodyINF;
import org.farng.mp3.lyrics3.FieldBodyLYR;
import org.farng.mp3.lyrics3.Lyrics3v2Field;
import org.farng.mp3.object.ObjectLyrics3Line;
import org.farng.mp3.AbstractMP3FragmentBody;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

/**
 * Title: ID3v2_4Frame Description: This class is the tag frame header used for ID3v2.40 tags Copyright: Copyright (c)
 * 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class ID3v2_4Frame extends ID3v2_3Frame {

    /**
     *
     */
    protected boolean dataLengthIndicator = false;
    /**
     *
     */
    protected boolean unsynchronization = false;

    /**
     * Creates a new ID3v2_4Frame object.
     */
    public ID3v2_4Frame() {
        // base empty constructor
    }

    /**
     * Creates a new ID3v2_4Frame object.
     */
    public ID3v2_4Frame(final ID3v2_4Frame copyObject) {
        super(copyObject);
        this.dataLengthIndicator = copyObject.dataLengthIndicator;
        this.unsynchronization = copyObject.unsynchronization;
    }

    /**
     * Creates a new ID3v2_4Frame object.
     */
    public ID3v2_4Frame(final AbstractID3v2FrameBody body) {
        super(body);
    }

    /**
     * Creates a new ID3v2_4Frame object.
     */
    public ID3v2_4Frame(final AbstractID3v2Frame frame) {
        if (frame instanceof ID3v2_4Frame) {
            final ID3v2_4Frame f = (ID3v2_4Frame) frame;
            this.unsynchronization = f.unsynchronization;
            this.dataLengthIndicator = f.dataLengthIndicator;
        }
        if (frame instanceof ID3v2_3Frame) {
            // a id3v2_4 frame is of type id3v2_3 frame also ...
            final ID3v2_3Frame f = (ID3v2_3Frame) frame;
            this.tagAlterPreservation = f.tagAlterPreservation;
            this.fileAlterPreservation = f.fileAlterPreservation;
            this.readOnly = f.readOnly;
            this.groupingIdentity = f.groupingIdentity;
            this.compression = f.compression;
            this.encryption = f.encryption;
        }
        if (frame instanceof ID3v2_2Frame) {
            // no variables yet
        }
        if (frame.getBody() == null) {
            // do nothing
        } else if (TagUtility.isID3v2_4FrameIdentifier(frame.getIdentifier())) {
            this.setBody((AbstractID3v2FrameBody) TagUtility.copyObject(frame.getBody()));
        }
    }

    /**
     * Creates a new ID3v2_4Frame object.
     */
    public ID3v2_4Frame(final boolean readOnly,
                        final boolean groupingIdentity,
                        final boolean compression,
                        final boolean encryption,
                        final boolean unsynchronization,
                        final boolean dataLengthIndicator,
                        final AbstractID3v2FrameBody body) {
        super(body);
        this.readOnly = readOnly;
        this.groupingIdentity = groupingIdentity;
        this.compression = compression;
        this.encryption = encryption;
        this.unsynchronization = unsynchronization;
        this.dataLengthIndicator = dataLengthIndicator;
    }

    /**
     * Creates a new ID3v2_4Frame object.
     */
    public ID3v2_4Frame(final Lyrics3v2Field field) throws InvalidTagException {
        final String id = field.getIdentifier();
        final String value;
        if (id.equals("IND")) {
            throw new InvalidTagException("Cannot create ID3v2.40 frame from Lyrics3 indications field.");
        } else if (id.equals("LYR")) {
            final FieldBodyLYR lyric = (FieldBodyLYR) field.getBody();
            ObjectLyrics3Line line;
            final Iterator iterator = lyric.iterator();
            final FrameBodySYLT sync;
            final FrameBodyUSLT unsync;
            final boolean hasTimeStamp = lyric.hasTimeStamp();

            // we'll create only one frame here.
            // if there is any timestamp at all, we will create a sync'ed frame.
            sync = new FrameBodySYLT((byte) 0, "ENG", (byte) 2, (byte) 1, "");
            unsync = new FrameBodyUSLT((byte) 0, "ENG", "", "");
            while (iterator.hasNext()) {
                line = (ObjectLyrics3Line) iterator.next();
                if (hasTimeStamp) {
                    sync.addLyric(line);
                } else {
                    unsync.addLyric(line);
                }
            }
            if (hasTimeStamp) {
                this.setBody(sync);
            } else {
                this.setBody(unsync);
            }
        } else if (id.equals("INF")) {
            value = ((FieldBodyINF) field.getBody()).getAdditionalInformation();
            this.setBody(new FrameBodyCOMM((byte) 0, "ENG", "", value));
        } else if (id.equals("AUT")) {
            value = ((FieldBodyAUT) field.getBody()).getAuthor();
            this.setBody(new FrameBodyTCOM((byte) 0, value));
        } else if (id.equals("EAL")) {
            value = ((FieldBodyEAL) field.getBody()).getAlbum();
            this.setBody(new FrameBodyTALB((byte) 0, value));
        } else if (id.equals("EAR")) {
            value = ((FieldBodyEAR) field.getBody()).getArtist();
            this.setBody(new FrameBodyTPE1((byte) 0, value));
        } else if (id.equals("ETT")) {
            value = ((FieldBodyETT) field.getBody()).getTitle();
            this.setBody(new FrameBodyTIT2((byte) 0, value));
        } else if (id.equals("IMG")) {
            throw new InvalidTagException("Cannot create ID3v2.40 frame from Lyrics3 image field.");
        } else {
            throw new InvalidTagException("Cannot caret ID3v2.40 frame from " + id + " Lyrics3 field");
        }
    }

    /**
     * Creates a new ID3v2_4Frame object.
     */
    public ID3v2_4Frame(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public int getSize() {
        return this.getBody().getSize() + 4 + 2 + 4;
    }

    public boolean equals(final Object obj) {
        if ((obj instanceof ID3v2_4Frame) == false) {
            return false;
        }
        final ID3v2_4Frame id3v2_4Frame = (ID3v2_4Frame) obj;
        if (this.unsynchronization != id3v2_4Frame.unsynchronization) {
            return false;
        }
        if (this.dataLengthIndicator != id3v2_4Frame.dataLengthIndicator) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * @param file
     *
     * @throws IOException
     * @throws InvalidTagException
     */
    public void read(final RandomAccessFile file) throws IOException, InvalidTagException {
        long filePointer;
        final byte[] buffer = new byte[4];
        byte b;

        // lets scan for a non-zero byte;
        do {
            filePointer = file.getFilePointer();
            b = file.readByte();
            org.farng.mp3.id3.AbstractID3v2.incrementPaddingCounter();
        } while (b == 0);
        file.seek(filePointer);
        org.farng.mp3.id3.AbstractID3v2.decrementPaddingCounter();

        // read the four character identifier
        file.read(buffer, 0, 4);
        final String identifier = new String(buffer, 0, 4);

        // is this a valid identifier?
        if (isValidID3v2FrameIdentifier(identifier) == false) {
            file.seek(file.getFilePointer() - 3);
            throw new InvalidTagException(identifier + " is not a valid ID3v2.40 frame");
        }
        filePointer = file.getFilePointer();

        // skip the 4 byte size
        file.skipBytes(4);

        // read the flag bytes
        file.read(buffer, 0, 2);
        this.tagAlterPreservation = (buffer[0] & TagConstant.MASK_V24_TAG_ALTER_PRESERVATION) != 0;
        this.fileAlterPreservation = (buffer[0] & TagConstant.MASK_V24_FILE_ALTER_PRESERVATION) != 0;
        this.readOnly = (buffer[0] & TagConstant.MASK_V24_READ_ONLY) != 0;
        this.groupingIdentity = (buffer[1] & TagConstant.MASK_V24_GROUPING_IDENTITY) != 0;
        this.compression = (buffer[1] & TagConstant.MASK_V24_COMPRESSION) != 0;
        this.encryption = (buffer[1] & TagConstant.MASK_V24_ENCRYPTION) != 0;
        this.unsynchronization = (buffer[1] & TagConstant.MASK_V24_FRAME_UNSYNCHRONIZATION) != 0;
        this.dataLengthIndicator = (buffer[1] & TagConstant.MASK_V24_DATA_LENGTH_INDICATOR) != 0;
        file.seek(filePointer);
        AbstractMP3FragmentBody newBody = readBody(identifier, file);
        
        // If unsynchronization is set, {0xff, 0x00} should be changed to {0xff}
        // Unfortunately, AbstractMP3Object class does not know about this
        // So we fix the raw data here
        if (this.unsynchronization) {
            final Iterator iterator = newBody.getObjectListIterator();
            while (iterator.hasNext()) {
                org.farng.mp3.object.AbstractMP3Object abstractMP3Object =
                        (org.farng.mp3.object.AbstractMP3Object) iterator.next();
                byte[] rawData = abstractMP3Object.getRawData();
                if (null == rawData)
                    continue;
                int dest = 0;
                for (int src = 0; src < rawData.length; src++) {
                    rawData[dest++] = rawData[src];
                    
                    if (-1 == rawData[src] &&
                            src < rawData.length -1 &&
                            0x00 == rawData[src + 1]) {
                        src++;
                    }
                }
                
                byte[] newRawData = new byte[dest];
                System.arraycopy(rawData, 0, newRawData, 0, newRawData.length);
                
                abstractMP3Object.setRawData(newRawData);
            }
        }
        
        this.setBody(decodeText(newBody));
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public void write(final RandomAccessFile file) throws IOException {
        final byte[] buffer = new byte[4];
        final long filePointer;
        final String str = TagUtility.truncate(getIdentifier(), 4);
        for (int i = 0; i < str.length(); i++) {
            buffer[i] = (byte) str.charAt(i);
        }
        file.write(buffer, 0, str.length());
        filePointer = file.getFilePointer();

        // skip the size bytes
        file.skipBytes(4);
        setAlterPreservation();
        buffer[0] = 0;
        buffer[1] = 0;
        if (this.tagAlterPreservation) {
            buffer[0] |= TagConstant.MASK_V24_TAG_ALTER_PRESERVATION;
        }
        if (this.fileAlterPreservation) {
            buffer[0] |= TagConstant.MASK_V24_FILE_ALTER_PRESERVATION;
        }
        if (this.readOnly) {
            buffer[0] |= TagConstant.MASK_V24_READ_ONLY;
        }
        if (this.groupingIdentity) {
            buffer[1] |= TagConstant.MASK_V24_GROUPING_IDENTITY;
        }
        if (this.compression) {
            buffer[1] |= TagConstant.MASK_V24_COMPRESSION;
        }
        if (this.encryption) {
            buffer[1] |= TagConstant.MASK_V24_ENCRYPTION;
        }
        if (this.unsynchronization) {
            buffer[1] |= TagConstant.MASK_V24_FRAME_UNSYNCHRONIZATION;
        }
        if (this.dataLengthIndicator) {
            buffer[1] |= TagConstant.MASK_V24_DATA_LENGTH_INDICATOR;
        }
        file.write(buffer, 0, 2);
        file.seek(filePointer);
        this.getBody().write(file);
    }
}