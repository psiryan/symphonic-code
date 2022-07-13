package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTIPL extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTIPL object.
     */
    public FrameBodyTIPL() {
        super();
    }

    /**
     * Creates a new FrameBodyTIPL object.
     */
    public FrameBodyTIPL(final FrameBodyTIPL body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTIPL object.
     */
    public FrameBodyTIPL(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTIPL object.
     */
    public FrameBodyTIPL(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TIPL";
    }
}