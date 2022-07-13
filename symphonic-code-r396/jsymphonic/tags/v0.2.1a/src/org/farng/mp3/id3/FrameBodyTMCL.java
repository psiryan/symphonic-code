package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTMCL extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTMCL object.
     */
    public FrameBodyTMCL() {
        super();
    }

    /**
     * Creates a new FrameBodyTMCL object.
     */
    public FrameBodyTMCL(final FrameBodyTMCL body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTMCL object.
     */
    public FrameBodyTMCL(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTMCL object.
     */
    public FrameBodyTMCL(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TMCL";
    }
}