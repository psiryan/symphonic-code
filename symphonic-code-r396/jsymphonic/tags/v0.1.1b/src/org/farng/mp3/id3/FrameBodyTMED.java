package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTMED extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTMED object.
     */
    public FrameBodyTMED() {
        super();
    }

    /**
     * Creates a new FrameBodyTMED object.
     */
    public FrameBodyTMED(final FrameBodyTMED body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTMED object.
     */
    public FrameBodyTMED(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTMED object.
     */
    public FrameBodyTMED(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TMED";
    }
}