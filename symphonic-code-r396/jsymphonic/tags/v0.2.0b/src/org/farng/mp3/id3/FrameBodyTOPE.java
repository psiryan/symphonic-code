package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTOPE extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTOPE object.
     */
    public FrameBodyTOPE() {
        super();
    }

    /**
     * Creates a new FrameBodyTOPE object.
     */
    public FrameBodyTOPE(final FrameBodyTOPE body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTOPE object.
     */
    public FrameBodyTOPE(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTOPE object.
     */
    public FrameBodyTOPE(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TOPE";
    }
}