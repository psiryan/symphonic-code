package org.farng.mp3.id3;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.1 $
 */
public class FrameBodyRVA2 extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyRVA2 object.
     */
    public FrameBodyRVA2() {
        super();
    }

    /**
     * Creates a new FrameBodyRVA2 object.
     */
    public FrameBodyRVA2(final FrameBodyRVA2 body) {
        super(body);
    }

    protected void setupObjectList() {
        throw new UnsupportedOperationException("This frame has not been implemented.");
    }

    public String getIdentifier() {
        throw new UnsupportedOperationException("This frame has not been implemented.");
    }
}