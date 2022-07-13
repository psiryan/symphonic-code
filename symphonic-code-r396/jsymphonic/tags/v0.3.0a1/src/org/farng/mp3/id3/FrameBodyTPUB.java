package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTPUB extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTPUB object.
     */
    public FrameBodyTPUB() {
        super();
    }

    /**
     * Creates a new FrameBodyTPUB object.
     */
    public FrameBodyTPUB(final FrameBodyTPUB body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTPUB object.
     */
    public FrameBodyTPUB(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTPUB object.
     */
    public FrameBodyTPUB(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TPUB";
    }
}