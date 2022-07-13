package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringHashMap;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTLAN extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTLAN object.
     */
    public FrameBodyTLAN() {
        super();
    }

    /**
     * Creates a new FrameBodyTLAN object.
     */
    public FrameBodyTLAN(final FrameBodyTLAN body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTLAN object.
     */
    public FrameBodyTLAN(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTLAN object.
     */
    public FrameBodyTLAN(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TLAN";
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap("Text Encoding", 1));
        appendToObjectList(new ObjectStringHashMap("Language", 3));
    }
}