package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTSOA extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTSOA object.
     */
    public FrameBodyTSOA() {
        super();
    }

    /**
     * Creates a new FrameBodyTSOA object.
     */
    public FrameBodyTSOA(final FrameBodyTSOA body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTSOA object.
     */
    public FrameBodyTSOA(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTSOA object.
     */
    public FrameBodyTSOA(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TSOA";
    }
}