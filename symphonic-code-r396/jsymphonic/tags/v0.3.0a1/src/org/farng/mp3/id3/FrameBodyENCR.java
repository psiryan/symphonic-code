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
public class FrameBodyENCR extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyENCR object.
     */
    public FrameBodyENCR() {
        super();
    }

    /**
     * Creates a new FrameBodyENCR object.
     */
    public FrameBodyENCR(final FrameBodyENCR body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyENCR object.
     */
    public FrameBodyENCR(final String owner, final byte methodSymbol, final byte[] data) {
        setObject("Owner", owner);
        setObject("Method Symbol", new Byte(methodSymbol));
        setObject("Encryption Data", data);
    }

    /**
     * Creates a new FrameBodyENCR object.
     */
    public FrameBodyENCR(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        return "ENCR" + ((char) 0) + getOwner();
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
        appendToObjectList(new ObjectNumberFixedLength("Method Symbol", 1));
        appendToObjectList(new ObjectByteArraySizeTerminated("Encryption Data"));
    }
}