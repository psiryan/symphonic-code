package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTOAL extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTOAL object.
     */
    public FrameBodyTOAL() {
        super();
    }

    /**
     * Creates a new FrameBodyTOAL object.
     */
    public FrameBodyTOAL(final FrameBodyTOAL body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTOAL object.
     */
    public FrameBodyTOAL(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTOAL object.
     */
    public FrameBodyTOAL(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TOAL";
    }
}