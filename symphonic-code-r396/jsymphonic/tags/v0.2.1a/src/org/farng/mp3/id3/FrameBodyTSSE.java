package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTSSE extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTSSE object.
     */
    public FrameBodyTSSE() {
        super();
    }

    /**
     * Creates a new FrameBodyTSSE object.
     */
    public FrameBodyTSSE(final FrameBodyTSSE body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTSSE object.
     */
    public FrameBodyTSSE(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTSSE object.
     */
    public FrameBodyTSSE(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TSSE";
    }
}