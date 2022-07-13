package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectNumberFixedLength;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodySEEK extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodySEEK object.
     */
    public FrameBodySEEK() {
        super();
    }

    /**
     * Creates a new FrameBodySEEK object.
     */
    public FrameBodySEEK(final int minOffsetToNextTag) {
        setObject("Minimum Offset to Next Tag", new Integer(minOffsetToNextTag));
    }

    /**
     * Creates a new FrameBodySEEK object.
     */
    public FrameBodySEEK(final FrameBodySEEK body) {
        super(body);
    }

    /**
     * Creates a new FrameBodySEEK object.
     */
    public FrameBodySEEK(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        return "SEEK";
    }

    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberFixedLength("Minimum Offset to Next Tag", 4));
    }
}