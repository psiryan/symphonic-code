package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTPOS extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTPOS object.
     */
    public FrameBodyTPOS() {
        super();
    }

    /**
     * Creates a new FrameBodyTPOS object.
     */
    public FrameBodyTPOS(final FrameBodyTPOS body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTPOS object.
     */
    public FrameBodyTPOS(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTPOS object.
     */
    public FrameBodyTPOS(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TPOS";
    }
}