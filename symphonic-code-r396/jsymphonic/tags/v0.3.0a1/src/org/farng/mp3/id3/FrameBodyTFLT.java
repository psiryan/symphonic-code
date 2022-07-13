package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTFLT extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTFLT object.
     */
    public FrameBodyTFLT() {
        super();
    }

    /**
     * Creates a new FrameBodyTFLT object.
     */
    public FrameBodyTFLT(final FrameBodyTFLT body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTFLT object.
     */
    public FrameBodyTFLT(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTFLT object.
     */
    public FrameBodyTFLT(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TFLT";
    }
}