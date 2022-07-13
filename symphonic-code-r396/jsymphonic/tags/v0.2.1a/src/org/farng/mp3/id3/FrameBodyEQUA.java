package org.farng.mp3.id3;

/**
 * Title: Description: DEPRECATED Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.1 $
 */
public class FrameBodyEQUA extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyEQUA object.
     */
    public FrameBodyEQUA() {
        super();
    }

    /**
     * Creates a new FrameBodyEQUA object.
     */
    public FrameBodyEQUA(final FrameBodyEQUA body) {
        super(body);
    }

    protected void setupObjectList() {
        throw new UnsupportedOperationException("This frame has not been implemented.");
    }

    public String getIdentifier() {
        throw new UnsupportedOperationException("This frame has not been implemented.");
    }
}