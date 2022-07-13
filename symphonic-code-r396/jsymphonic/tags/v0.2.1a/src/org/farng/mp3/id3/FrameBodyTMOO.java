package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTMOO extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTMOO object.
     */
    public FrameBodyTMOO() {
        super();
    }

    /**
     * Creates a new FrameBodyTMOO object.
     */
    public FrameBodyTMOO(final FrameBodyTMOO body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTMOO object.
     */
    public FrameBodyTMOO(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTMOO object.
     */
    public FrameBodyTMOO(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TMOO";
    }
}