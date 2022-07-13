package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: DEPRECATED Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTIME extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTIME object.
     */
    public FrameBodyTIME() {
        super();
    }

    /**
     * Creates a new FrameBodyTIME object.
     */
    public FrameBodyTIME(final FrameBodyTIME body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTIME object.
     */
    public FrameBodyTIME(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTIME object.
     */
    public FrameBodyTIME(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TIME";
    }
}