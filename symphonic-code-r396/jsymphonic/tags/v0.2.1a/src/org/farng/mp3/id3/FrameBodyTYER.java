package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: DEPRECATED Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTYER extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTYER object.
     */
    public FrameBodyTYER() {
        super();
    }

    /**
     * Creates a new FrameBodyTYER object.
     */
    public FrameBodyTYER(final FrameBodyTYER body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTYER object.
     */
    public FrameBodyTYER(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTYER object.
     */
    public FrameBodyTYER(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TYER";
    }
}