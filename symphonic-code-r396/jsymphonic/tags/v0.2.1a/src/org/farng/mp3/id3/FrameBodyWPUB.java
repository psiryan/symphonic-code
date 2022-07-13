package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyWPUB extends AbstractFrameBodyUrlLink {

    /**
     * Creates a new FrameBodyWPUB object.
     */
    public FrameBodyWPUB() {
        super();
    }

    /**
     * Creates a new FrameBodyWPUB object.
     */
    public FrameBodyWPUB(final String urlLink) {
        super(urlLink);
    }

    /**
     * Creates a new FrameBodyWPUB object.
     */
    public FrameBodyWPUB(final FrameBodyWPUB body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyWPUB object.
     */
    public FrameBodyWPUB(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "WPUB";
    }
}