package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringSizeTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public abstract class AbstractFrameBodyTextInformation extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyTextInformation object.
     */
    protected AbstractFrameBodyTextInformation() {
        super();
    }

    /**
     * Creates a new AbstractFrameBodyTextInformation object.
     */
    protected AbstractFrameBodyTextInformation(final AbstractFrameBodyTextInformation body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTextInformation object.
     */
    protected AbstractFrameBodyTextInformation(final byte textEncoding, final String text) {
        super();
        setObject("Text Encoding", new Byte(textEncoding));
        setObject("Text", text);
    }

    /**
     * Creates a new FrameBodyTextInformation object.
     */
    protected AbstractFrameBodyTextInformation(final RandomAccessFile file) throws IOException, InvalidTagException {
        super();
        read(file);
    }

    public String getBriefDescription() {
        return getText();
    }

    /**
     * @param text
     */
    public void setText(final String text) {
        setObject("Text", text);
    }

    public String getText() {
        return (String) getObject("Text");
    }

    /**
     * @param textEncoding
     */
    public void setTextEncoding(final byte textEncoding) {
        setObject("Text Encoding", new Byte(textEncoding));
    }

    public byte getTextEncoding() {
        return ((Byte) getObject("Text Encoding")).byteValue();
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap("Text Encoding", 1));
        appendToObjectList(new ObjectStringSizeTerminated("Text"));
    }
}