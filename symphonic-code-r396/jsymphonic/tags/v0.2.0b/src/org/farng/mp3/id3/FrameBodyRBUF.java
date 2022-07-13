package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectBooleanByte;
import org.farng.mp3.object.ObjectNumberFixedLength;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyRBUF extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyRBUF object.
     */
    public FrameBodyRBUF() {
        super();
    }

    /**
     * Creates a new FrameBodyRBUF object.
     */
    public FrameBodyRBUF(final FrameBodyRBUF body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyRBUF object.
     */
    public FrameBodyRBUF(final byte bufferSize, final boolean embeddedInfoFlag, final byte offsetToNextTag) {
        setObject("Buffer Size", new Byte(bufferSize));
        setObject("Embedded Info Flag", new Boolean(embeddedInfoFlag));
        setObject("Offset to Next Flag", new Byte(offsetToNextTag));
    }

    /**
     * Creates a new FrameBodyRBUF object.
     */
    public FrameBodyRBUF(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        return "RBUF";
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberFixedLength("Buffer Size", 3));
        appendToObjectList(new ObjectBooleanByte("Embedded Info Flag", (byte) 1));
        appendToObjectList(new ObjectNumberFixedLength("Offset to Next Tag", 4));
    }
}