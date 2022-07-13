package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTKEY extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTKEY object.
     */
    public FrameBodyTKEY() {
        super();
    }

    /**
     * Creates a new FrameBodyTKEY object.
     */
    public FrameBodyTKEY(final FrameBodyTKEY body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTKEY object.
     */
    public FrameBodyTKEY(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTKEY object.
     */
    public FrameBodyTKEY(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TKEY";
    }
}