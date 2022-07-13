package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTPE3 extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTPE3 object.
     */
    public FrameBodyTPE3() {
        super();
    }

    /**
     * Creates a new FrameBodyTPE3 object.
     */
    public FrameBodyTPE3(final FrameBodyTPE3 body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTPE3 object.
     */
    public FrameBodyTPE3(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTPE3 object.
     */
    public FrameBodyTPE3(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TPE3";
    }
}