package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTDLY extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTDLY object.
     */
    public FrameBodyTDLY() {
        super();
    }

    /**
     * Creates a new FrameBodyTDLY object.
     */
    public FrameBodyTDLY(final FrameBodyTDLY body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTDLY object.
     */
    public FrameBodyTDLY(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTDLY object.
     */
    public FrameBodyTDLY(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TDLY";
    }
}