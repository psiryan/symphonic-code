package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTCON extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTCON object.
     */
    public FrameBodyTCON() {
        super();
    }

    /**
     * Creates a new FrameBodyTCON object.
     */
    public FrameBodyTCON(final FrameBodyTCON body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTCON object.
     */
    public FrameBodyTCON(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTCON object.
     */
    public FrameBodyTCON(final java.io.RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TCON";
    }
}