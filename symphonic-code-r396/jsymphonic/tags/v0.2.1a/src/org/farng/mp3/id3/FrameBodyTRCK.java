package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTRCK extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTRCK object.
     */
    public FrameBodyTRCK() {
        super();
    }

    /**
     * Creates a new FrameBodyTRCK object.
     */
    public FrameBodyTRCK(final FrameBodyTRCK body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTRCK object.
     */
    public FrameBodyTRCK(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTRCK object.
     */
    public FrameBodyTRCK(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TRCK";
    }
}