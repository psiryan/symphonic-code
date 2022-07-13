package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTOWN extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTOWN object.
     */
    public FrameBodyTOWN() {
        super();
    }

    /**
     * Creates a new FrameBodyTOWN object.
     */
    public FrameBodyTOWN(final FrameBodyTOWN body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTOWN object.
     */
    public FrameBodyTOWN(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTOWN object.
     */
    public FrameBodyTOWN(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TOWN";
    }
}