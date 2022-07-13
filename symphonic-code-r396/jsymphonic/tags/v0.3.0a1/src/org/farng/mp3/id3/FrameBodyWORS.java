package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyWORS extends AbstractFrameBodyUrlLink {

    /**
     * Creates a new FrameBodyWORS object.
     */
    public FrameBodyWORS() {
        super();
    }

    /**
     * Creates a new FrameBodyWORS object.
     */
    public FrameBodyWORS(final String urlLink) {
        super(urlLink);
    }

    /**
     * Creates a new FrameBodyWORS object.
     */
    public FrameBodyWORS(final FrameBodyWORS body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyWORS object.
     */
    public FrameBodyWORS(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "WORS";
    }
}