package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTIT2 extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTIT2 object.
     */
    public FrameBodyTIT2() {
        super();
    }

    /**
     * Creates a new FrameBodyTIT2 object.
     */
    public FrameBodyTIT2(final FrameBodyTIT2 body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTIT2 object.
     */
    public FrameBodyTIT2(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTIT2 object.
     */
    public FrameBodyTIT2(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TIT2";
    }
}