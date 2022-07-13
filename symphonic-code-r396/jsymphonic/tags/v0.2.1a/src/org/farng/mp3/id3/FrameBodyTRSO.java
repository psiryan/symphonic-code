package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTRSO extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTRSO object.
     */
    public FrameBodyTRSO() {
        super();
    }

    /**
     * Creates a new FrameBodyTRSO object.
     */
    public FrameBodyTRSO(final FrameBodyTRSO body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTRSO object.
     */
    public FrameBodyTRSO(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTRSO object.
     */
    public FrameBodyTRSO(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TRSO";
    }
}