package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTPE1 extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTPE1 object.
     */
    public FrameBodyTPE1() {
        super();
    }

    /**
     * Creates a new FrameBodyTPE1 object.
     */
    public FrameBodyTPE1(final FrameBodyTPE1 body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTPE1 object.
     */
    public FrameBodyTPE1(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTPE1 object.
     */
    public FrameBodyTPE1(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TPE1";
    }
}