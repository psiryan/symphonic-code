package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyWCOP extends AbstractFrameBodyUrlLink {

    /**
     * Creates a new FrameBodyWCOP object.
     */
    public FrameBodyWCOP() {
        super();
    }

    /**
     * Creates a new FrameBodyWCOP object.
     */
    public FrameBodyWCOP(final String urlLink) {
        super(urlLink);
    }

    /**
     * Creates a new FrameBodyWCOP object.
     */
    public FrameBodyWCOP(final FrameBodyWCOP body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyWCOP object.
     */
    public FrameBodyWCOP(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "WCOP";
    }
}