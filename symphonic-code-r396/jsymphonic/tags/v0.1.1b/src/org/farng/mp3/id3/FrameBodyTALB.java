package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTALB extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTALB object.
     */
    public FrameBodyTALB() {
        super();
    }

    /**
     * Creates a new FrameBodyTALB object.
     */
    public FrameBodyTALB(final FrameBodyTALB body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTALB object.
     */
    public FrameBodyTALB(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTALB object.
     */
    public FrameBodyTALB(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TALB";
    }
}