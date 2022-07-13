package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: DEPRECATED Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTRDA extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTRDA object.
     */
    public FrameBodyTRDA() {
        super();
    }

    /**
     * Creates a new FrameBodyTRDA object.
     */
    public FrameBodyTRDA(final FrameBodyTRDA body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTRDA object.
     */
    public FrameBodyTRDA(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTRDA object.
     */
    public FrameBodyTRDA(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TRDA";
    }
}