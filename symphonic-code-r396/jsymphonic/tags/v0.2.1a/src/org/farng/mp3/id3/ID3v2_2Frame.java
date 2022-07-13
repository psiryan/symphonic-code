package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.TagUtility;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: ID3v2_2Frame Description: This class is the frame header used in ID3v2.2 tags Copyright: Copyright (c) 2002
 * Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class ID3v2_2Frame extends AbstractID3v2Frame {

    /**
     * Creates a new ID3v2_2Frame object.
     */
    public ID3v2_2Frame() {
        // base empty constructor
    }

    /**
     * Creates a new ID3v2_2Frame object.
     */
    public ID3v2_2Frame(final AbstractID3v2FrameBody body) {
        super(body);
    }

    /**
     * Creates a new ID3v2_2Frame object.
     */
    public ID3v2_2Frame(final ID3v2_2Frame frame) {
        super(frame);
    }

    /**
     * Creates a new ID3v2_3Frame object.
     */
    public ID3v2_2Frame(final AbstractID3v2Frame frame) {
        if (frame.getBody() == null) {
            // do nothing
        } else if (TagUtility.isID3v2_2FrameIdentifier(frame.getIdentifier())) {
            this.setBody((AbstractID3v2FrameBody) TagUtility.copyObject(frame.getBody()));
        }
    }

    /**
     * Creates a new ID3v2_2Frame object.
     */
    public ID3v2_2Frame(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public int getSize() {
        return this.getBody().getSize() + 3 + 3;
    }

    /**
     * @param file
     *
     * @throws IOException
     * @throws InvalidTagException
     */
    public void read(final RandomAccessFile file) throws IOException, InvalidTagException {
        final byte[] buffer = new byte[3];

        // lets scan for a non-zero byte;
        long filePointer;
        byte b;
        do {
            filePointer = file.getFilePointer();
            b = file.readByte();
            org.farng.mp3.id3.AbstractID3v2.incrementPaddingCounter();
        } while (b == 0);
        file.seek(filePointer);
        org.farng.mp3.id3.AbstractID3v2.decrementPaddingCounter();

        // read the 3 chracter identifier
        file.read(buffer, 0, 3);
        final String identifier = new String(buffer, 0, 3);

        // is this a valid identifier?
        if (isValidID3v2FrameIdentifier(identifier) == false) {
            file.seek(file.getFilePointer() - 2);
            throw new InvalidTagException(identifier + " is not a valid ID3v2.20 frame");
        }
        this.setBody(readBody(identifier, file));
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public void write(final RandomAccessFile file) throws IOException {
        final byte[] buffer = new byte[4];
        final String str = TagUtility.truncate(getIdentifier(), 3);
        for (int i = 0; i < str.length(); i++) {
            buffer[i] = (byte) str.charAt(i);
        }
        file.write(buffer, 0, str.length());
        this.getBody().write(file);
    }
}