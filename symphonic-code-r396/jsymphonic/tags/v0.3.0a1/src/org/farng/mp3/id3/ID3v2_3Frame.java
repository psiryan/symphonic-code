package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.TagConstant;
import org.farng.mp3.TagUtility;
import org.farng.mp3.AbstractMP3FragmentBody;

import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * Title: ID3v2_3Frame Description: This class is the tag frame header used for ID3v2.30 tags Copyright: Copyright (c)
 * 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.3 $
 */
public class ID3v2_3Frame extends ID3v2_2Frame {

    /**
     *
     */
    protected boolean compression = false;
    /**
     *
     */
    protected boolean encryption = false;
    /**
     *
     */
    protected boolean fileAlterPreservation = false;
    /**
     *
     */
    protected boolean groupingIdentity = false;
    /**
     *
     */
    protected boolean readOnly = false; // @todo implement this read only!

    // these are flags for each frame them selves
    /**
     *
     */
    protected boolean tagAlterPreservation = false;

    /**
     * Creates a new ID3v2_3Frame object.
     */
    public ID3v2_3Frame() {
        setAlterPreservation();
    }

    /**
     * Creates a new ID3v2_3Frame object.
     */
    public ID3v2_3Frame(final AbstractID3v2FrameBody body) {
        super(body);
        setAlterPreservation();
    }

    /**
     * Creates a new ID3v2_3Frame object.
     */
    public ID3v2_3Frame(final ID3v2_3Frame copyObject) {
        super(copyObject);
        this.compression = copyObject.compression;
        this.encryption = copyObject.encryption;
        this.fileAlterPreservation = copyObject.fileAlterPreservation;
        this.groupingIdentity = copyObject.groupingIdentity;
        this.readOnly = copyObject.readOnly;
        this.tagAlterPreservation = copyObject.tagAlterPreservation;
    }

    /**
     * Creates a new ID3v2_3Frame object.
     */
    public ID3v2_3Frame(final boolean readOnly,
                        final boolean groupingIdentity,
                        final boolean compression,
                        final boolean encryption,
                        final AbstractID3v2FrameBody body) {
        super(body);
        this.readOnly = readOnly;
        this.groupingIdentity = groupingIdentity;
        this.compression = compression;
        this.encryption = encryption;
        setAlterPreservation();
    }

    /**
     * Creates a new ID3v2_4Frame object.
     */
    public ID3v2_3Frame(final AbstractID3v2Frame frame) {
        if (frame instanceof ID3v2_3Frame) {
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
        } else if (TagUtility.isID3v2_3FrameIdentifier(frame.getIdentifier())) {
            this.setBody((AbstractID3v2FrameBody) TagUtility.copyObject(frame.getBody()));
        }
    }

    /**
     * Creates a new ID3v2_3Frame object.
     */
    public ID3v2_3Frame(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public int getSize() {
        return this.getBody().getSize() + 4 + 2 + 4;
    }

    public boolean equals(final Object obj) {
        if ((obj instanceof ID3v2_3Frame) == false) {
            return false;
        }
        final ID3v2_3Frame id3v2_3Frame = (ID3v2_3Frame) obj;
        if (this.compression != id3v2_3Frame.compression) {
            return false;
        }
        if (this.encryption != id3v2_3Frame.encryption) {
            return false;
        }
        if (this.fileAlterPreservation != id3v2_3Frame.fileAlterPreservation) {
            return false;
        }
        if (this.groupingIdentity != id3v2_3Frame.groupingIdentity) {
            return false;
        }
        if (this.readOnly != id3v2_3Frame.readOnly) {
            return false;
        }
        if (this.tagAlterPreservation != id3v2_3Frame.tagAlterPreservation) {
            return false;
        }
        return super.equals(obj);
    }
    
    /**
     * Because Each ID3 frame itself does not know its encoding, you must decode
     * the text again. Also v2.4 unsynchronized flag does matter.
     * @param newBody
     */
    protected AbstractMP3FragmentBody decodeText(AbstractMP3FragmentBody newBody) {
        String encoding = null;
        org.farng.mp3.object.AbstractMP3Object textObject = null;
        final java.util.Iterator iterator = newBody.getObjectListIterator();
        while (iterator.hasNext()) {
            org.farng.mp3.object.AbstractMP3Object abstractMP3Object =
                    (org.farng.mp3.object.AbstractMP3Object) iterator.next();
            if (abstractMP3Object.getIdentifier().equals("Text Encoding")) {
                encoding = abstractMP3Object.toString();
            }

            if (abstractMP3Object.getIdentifier().equals("Text") ||
                    abstractMP3Object.getIdentifier().equals("Date Time")) {
                textObject = abstractMP3Object;
            }
        }

        if (null != encoding && textObject != null) {
            byte[] rawTextData = textObject.getRawData();
            
            try {
                textObject.setValue(
                        new String(rawTextData, 1, rawTextData.length - 1, encoding));
            } catch (java.io.UnsupportedEncodingException e) {
                // leave the text as it is
            }
        }

        return newBody;
    }

    /**
     * @param file
     *
     * @throws IOException
     * @throws InvalidTagException
     */
    public void read(final RandomAccessFile file) throws IOException, InvalidTagException {
        byte b;
        long filePointer;
        final byte[] buffer = new byte[4];

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
            throw new InvalidTagException(identifier + " is not a valid ID3v2.30 frame");
        }
        filePointer = file.getFilePointer();

        // skip the 4 byte size
        file.skipBytes(4);

        // read the flag bytes
        file.read(buffer, 0, 2);
        this.tagAlterPreservation = (buffer[0] & TagConstant.MASK_V23_TAG_ALTER_PRESERVATION) != 0;
        this.fileAlterPreservation = (buffer[0] & TagConstant.MASK_V23_FILE_ALTER_PRESERVATION) != 0;
        this.readOnly = (buffer[0] & TagConstant.MASK_V23_READ_ONLY) != 0;
        this.compression = (buffer[1] & TagConstant.MASK_V23_COMPRESSION) != 0;
        this.encryption = (buffer[1] & TagConstant.MASK_V23_ENCRYPTION) != 0;
        this.groupingIdentity = (buffer[1] & TagConstant.MASK_V23_GROUPING_IDENTITY) != 0;
        file.seek(filePointer);
        this.setBody(decodeText(readBody(identifier, file)));
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public void write(final RandomAccessFile file) throws IOException {
        final long filePointer;
        final byte[] buffer = new byte[4];
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
            buffer[0] |= TagConstant.MASK_V23_TAG_ALTER_PRESERVATION;
        }
        if (this.fileAlterPreservation) {
            buffer[0] |= TagConstant.MASK_V23_FILE_ALTER_PRESERVATION;
        }
        if (this.readOnly) {
            buffer[0] |= TagConstant.MASK_V23_READ_ONLY;
        }
        if (this.compression) {
            buffer[1] |= TagConstant.MASK_V23_COMPRESSION;
        }
        if (this.encryption) {
            buffer[1] |= TagConstant.MASK_V23_ENCRYPTION;
        }
        if (this.groupingIdentity) {
            buffer[1] |= TagConstant.MASK_V23_GROUPING_IDENTITY;
        }
        file.write(buffer, 0, 2);
        file.seek(filePointer);
        this.getBody().write(file);
    }

    /**
     *
     */
    protected void setAlterPreservation() {
        final String str = getIdentifier();
        if (str.equals("ETCO") ||
            str.equals("EQUA") ||
            str.equals("MLLT") ||
            str.equals("POSS") ||
            str.equals("SYLT") ||
            str.equals("SYTC") ||
            str.equals("RVAD") ||
            str.equals("TENC") ||
            str.equals("TLEN") ||
            str.equals("TSIZ")) {
            this.tagAlterPreservation = false;
            this.fileAlterPreservation = true;
        } else {
            this.tagAlterPreservation = false;
            this.fileAlterPreservation = true;
        }
    }
}