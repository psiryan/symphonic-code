package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectByteArraySizeTerminated;
import org.farng.mp3.object.ObjectNumberFixedLength;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodySIGN extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodySIGN object.
     */
    public FrameBodySIGN() {
        super();
    }

    /**
     * Creates a new FrameBodySIGN object.
     */
    public FrameBodySIGN(final FrameBodySIGN body) {
        super(body);
    }

    /**
     * Creates a new FrameBodySIGN object.
     */
    public FrameBodySIGN(final byte groupSymbol, final byte[] signature) {
        setObject("Group Symbol", new Byte(groupSymbol));
        setObject("Signature", signature);
    }

    /**
     * Creates a new FrameBodySIGN object.
     */
    public FrameBodySIGN(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    /**
     * @param groupSymbol
     */
    public void setGroupSymbol(final byte groupSymbol) {
        setObject("Group Symbol", new Byte(groupSymbol));
    }

    public byte getGroupSymbol() {
        if (getObject("Group Symbol") != null) {
            return ((Byte) getObject("Group Symbol")).byteValue();
        }
        return 0;
    }

    public String getIdentifier() {
        return "SIGN" + ((char) 0) + getGroupSymbol() + ((char) 0) + (new String(getSignature()));
    }

    /**
     * @param signature
     */
    public void setSignature(final byte[] signature) {
        setObject("Signature", signature);
    }

    public byte[] getSignature() {
        return (byte[]) getObject("Signature");
    }

    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberFixedLength("Group Symbol", 1));
        appendToObjectList(new ObjectByteArraySizeTerminated("Signature"));
    }
}