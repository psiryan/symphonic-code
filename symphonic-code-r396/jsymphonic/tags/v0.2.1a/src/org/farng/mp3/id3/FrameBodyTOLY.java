package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTOLY extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTOLY object.
     */
    public FrameBodyTOLY() {
        super();
    }

    /**
     * Creates a new FrameBodyTOLY object.
     */
    public FrameBodyTOLY(final FrameBodyTOLY body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTOLY object.
     */
    public FrameBodyTOLY(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTOLY object.
     */
    public FrameBodyTOLY(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TOLY";
    }
}