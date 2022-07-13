package org.farng.mp3.id3;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.1 $
 */
public class FrameBodySYTC extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodySYTC object.
     */
    public FrameBodySYTC() {
        super();
    }

    /**
     * Creates a new FrameBodySYTC object.
     */
    public FrameBodySYTC(final FrameBodySYTC body) {
        super(body);
    }

    protected void setupObjectList() {
        throw new UnsupportedOperationException("This frame has not been implemented.");
    }

    public String getIdentifier() {
        throw new UnsupportedOperationException("This frame has not been implemented.");
    }
}