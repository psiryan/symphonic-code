package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectByteArraySizeTerminated;
import org.farng.mp3.object.ObjectStringNullTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyPRIV extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyPRIV object.
     */
    public FrameBodyPRIV() {
        super();
    }

    /**
     * Creates a new FrameBodyPRIV object.
     */
    public FrameBodyPRIV(final FrameBodyPRIV body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyPRIV object.
     */
    public FrameBodyPRIV(final String owner, final byte[] data) {
        setObject("Owner", owner);
        setObject("Private Data", data);
    }

    /**
     * Creates a new FrameBodyPRIV object.
     */
    public FrameBodyPRIV(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getBriefDescription() {
        return this.getOwner();
    }

    /**
     * @param data
     */
    public void setData(final byte[] data) {
        setObject("Private Data", data);
    }

    public byte[] getData() {
        return (byte[]) getObject("Private Data");
    }

    public String getIdentifier() {
        return "PRIV" + ((char) 0) + getOwner() + ((char) 0) + (new String(getData()));
    }

    /**
     * @param owner
     */
    public void setOwner(final String owner) {
        setObject("Owner", owner);
    }

    public String getOwner() {
        return (String) getObject("Owner");
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectStringNullTerminated("Owner"));
        appendToObjectList(new ObjectByteArraySizeTerminated("Private Data"));
    }
}