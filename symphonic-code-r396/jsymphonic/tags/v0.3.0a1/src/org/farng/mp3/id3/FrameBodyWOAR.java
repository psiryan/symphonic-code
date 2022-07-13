package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyWOAR extends AbstractFrameBodyUrlLink {

    /**
     * Creates a new FrameBodyWOAR object.
     */
    public FrameBodyWOAR() {
        super();
    }

    /**
     * Creates a new FrameBodyWOAR object.
     */
    public FrameBodyWOAR(final String urlLink) {
        super(urlLink);
    }

    /**
     * Creates a new FrameBodyWOAR object.
     */
    public FrameBodyWOAR(final FrameBodyWOAR body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyWOAR object.
     */
    public FrameBodyWOAR(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "WOAR" + ((char) 0) + getUrlLink();
    }
}