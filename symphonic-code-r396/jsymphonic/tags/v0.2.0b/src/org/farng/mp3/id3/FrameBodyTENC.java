package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTENC extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTENC object.
     */
    public FrameBodyTENC() {
        super();
    }

    /**
     * Creates a new FrameBodyTENC object.
     */
    public FrameBodyTENC(final FrameBodyTENC body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTENC object.
     */
    public FrameBodyTENC(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTENC object.
     */
    public FrameBodyTENC(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TENC";
    }
}