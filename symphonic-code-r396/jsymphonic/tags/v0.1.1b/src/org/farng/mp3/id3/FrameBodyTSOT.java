package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTSOT extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTSOT object.
     */
    public FrameBodyTSOT() {
        super();
    }

    /**
     * Creates a new FrameBodyTSOT object.
     */
    public FrameBodyTSOT(final FrameBodyTSOT body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTSOT object.
     */
    public FrameBodyTSOT(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTSOT object.
     */
    public FrameBodyTSOT(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TSOT";
    }
}