package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyWOAS extends AbstractFrameBodyUrlLink {

    /**
     * Creates a new FrameBodyWOAS object.
     */
    public FrameBodyWOAS() {
        super();
    }

    /**
     * Creates a new FrameBodyWOAS object.
     */
    public FrameBodyWOAS(final String urlLink) {
        super(urlLink);
    }

    /**
     * Creates a new FrameBodyWOAS object.
     */
    public FrameBodyWOAS(final FrameBodyWOAS body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyWOAS object.
     */
    public FrameBodyWOAS(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "WOAS";
    }
}