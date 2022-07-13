package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectByteArraySizeTerminated;
import org.farng.mp3.object.ObjectNumberFixedLength;
import org.farng.mp3.object.ObjectStringNullTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyGRID extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyGRID object.
     */
    public FrameBodyGRID() {
        super();
    }

    /**
     * Creates a new FrameBodyGRID object.
     */
    public FrameBodyGRID(final FrameBodyGRID body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyGRID object.
     */
    public FrameBodyGRID(final String owner, final byte groupSymbol, final byte[] data) {
        setObject("Owner", owner);
        setObject("Group Symbol", new Byte(groupSymbol));
        setObject("Group Dependent Data", data);
    }

    /**
     * Creates a new FrameBodyGRID object.
     */
    public FrameBodyGRID(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    /**
     * @param textEncoding
     */
    public void setGroupSymbol(final byte textEncoding) {
        setObject("Group Symbol", new Byte(textEncoding));
    }

    public byte getGroupSymbol() {
        if (getObject("Group Symbol") != null) {
            return ((Byte) getObject("Group Symbol")).byteValue();
        }
        return 0;
    }

    public String getIdentifier() {
        return "GRID" + ((char) 0) + getOwner() + ((char) 0) + getGroupSymbol();
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
        appendToObjectList(new ObjectNumberFixedLength("Group Symbol", 1));
        appendToObjectList(new ObjectByteArraySizeTerminated("Group Dependent Data"));
    }
}