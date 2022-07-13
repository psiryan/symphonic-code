package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTIT3 extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTIT3 object.
     */
    public FrameBodyTIT3() {
        super();
    }

    /**
     * Creates a new FrameBodyTIT3 object.
     */
    public FrameBodyTIT3(final FrameBodyTIT3 body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTIT3 object.
     */
    public FrameBodyTIT3(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTIT3 object.
     */
    public FrameBodyTIT3(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TIT3";
    }
}