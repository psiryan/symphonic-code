package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTEXT extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTEXT object.
     */
    public FrameBodyTEXT() {
        super();
    }

    /**
     * Creates a new FrameBodyTEXT object.
     */
    public FrameBodyTEXT(final FrameBodyTEXT body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTEXT object.
     */
    public FrameBodyTEXT(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTEXT object.
     */
    public FrameBodyTEXT(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TEXT";
    }
}