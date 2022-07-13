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
public class FrameBodyUFID extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyUFID object.
     */
    public FrameBodyUFID() {
        super();
    }

    /**
     * Creates a new FrameBodyUFID object.
     */
    public FrameBodyUFID(final FrameBodyUFID body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyUFID object.
     */
    public FrameBodyUFID(final String owner, final byte[] identifier) {
        setObject("Owner", owner);
        setObject("Identifier", identifier);
    }

    /**
     * Creates a new FrameBodyUFID object.
     */
    public FrameBodyUFID(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        return "UFID" + ((char) 0) + getOwner();
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
        appendToObjectList(new ObjectByteArraySizeTerminated("Identifier"));
    }
}