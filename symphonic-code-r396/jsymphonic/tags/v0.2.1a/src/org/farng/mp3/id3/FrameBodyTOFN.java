package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTOFN extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTOFN object.
     */
    public FrameBodyTOFN() {
        super();
    }

    /**
     * Creates a new FrameBodyTOFN object.
     */
    public FrameBodyTOFN(final FrameBodyTOFN body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTOFN object.
     */
    public FrameBodyTOFN(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTOFN object.
     */
    public FrameBodyTOFN(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TOFN";
    }
}