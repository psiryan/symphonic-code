package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: DEPRECATED Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTORY extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTORY object.
     */
    public FrameBodyTORY() {
        super();
    }

    /**
     * Creates a new FrameBodyTORY object.
     */
    public FrameBodyTORY(final FrameBodyTORY body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTORY object.
     */
    public FrameBodyTORY(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTORY object.
     */
    public FrameBodyTORY(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TORY";
    }
}