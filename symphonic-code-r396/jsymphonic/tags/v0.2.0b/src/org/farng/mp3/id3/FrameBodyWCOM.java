package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyWCOM extends AbstractFrameBodyUrlLink {

    /**
     * Creates a new FrameBodyWCOM object.
     */
    public FrameBodyWCOM() {
        super();
    }

    /**
     * Creates a new FrameBodyWCOM object.
     */
    public FrameBodyWCOM(final String urlLink) {
        super(urlLink);
    }

    /**
     * Creates a new FrameBodyWCOM object.
     */
    public FrameBodyWCOM(final FrameBodyWCOM body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyWCOM object.
     */
    public FrameBodyWCOM(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "WCOM" + ((char) 0) + getUrlLink();
    }
}