package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringDateTime;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTDEN extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTDEN object.
     */
    public FrameBodyTDEN() {
        super();
    }

    /**
     * Creates a new FrameBodyTDEN object.
     */
    public FrameBodyTDEN(final FrameBodyTDEN body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTDEN object.
     */
    public FrameBodyTDEN(final byte textEncoding, final String text) {
        setObject(ObjectNumberHashMap.TEXT_ENCODING, new Byte(textEncoding));
        setObject("Date Time", text);
    }

    /**
     * Creates a new FrameBodyTDEN object.
     */
    public FrameBodyTDEN(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TDEN";
    }

    /**
     * @param text
     */
    public void setText(final String text) {
        setObject("Date Time", text);
    }

    public String getText() {
        return (String) getObject("Date Time");
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap(ObjectNumberHashMap.TEXT_ENCODING, 1));
        appendToObjectList(new ObjectStringDateTime("Date Time"));
    }
}