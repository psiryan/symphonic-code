package org.farng.mp3.id3;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.1 $
 */
public class FrameBodyMLLT extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyMLLT object.
     */
    public FrameBodyMLLT() {
        super();
    }

    /**
     * Creates a new FrameBodyMLLT object.
     */
    public FrameBodyMLLT(final FrameBodyMLLT body) {
        super(body);
    }

    protected void setupObjectList() {
        throw new UnsupportedOperationException("This frame has not been implemented.");
    }

    public String getIdentifier() {
        throw new UnsupportedOperationException("This frame has not been implemented.");
    }
}