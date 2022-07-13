package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectByteArraySizeTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyMCDI extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyMCDI object.
     */
    public FrameBodyMCDI() {
        super();
    }

    /**
     * Creates a new FrameBodyMCDI object.
     */
    public FrameBodyMCDI(final FrameBodyMCDI body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyMCDI object.
     */
    public FrameBodyMCDI(final byte[] cdTOC) {
        setObject("CD Table of Contents", cdTOC);
    }

    /**
     * Creates a new FrameBodyMCDI object.
     */
    public FrameBodyMCDI(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        return "MCDI";
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectByteArraySizeTerminated("CD Table of Contents"));
    }
}