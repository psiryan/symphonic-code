package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTPRO extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTPRO object.
     */
    public FrameBodyTPRO() {
        super();
    }

    /**
     * Creates a new FrameBodyTPRO object.
     */
    public FrameBodyTPRO(final FrameBodyTPRO body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTPRO object.
     */
    public FrameBodyTPRO(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTPRO object.
     */
    public FrameBodyTPRO(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TPRO";
    }
}