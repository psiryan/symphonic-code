package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringNullTerminated;
import org.farng.mp3.object.ObjectStringSizeTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyTXXX extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyTXXX object.
     */
    public FrameBodyTXXX() {
        super();
    }

    /**
     * Creates a new FrameBodyTXXX object.
     */
    public FrameBodyTXXX(final FrameBodyTXXX body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTXXX object.
     */
    public FrameBodyTXXX(final byte textEncoding, final String description, final String text) {
        setObject("Text Encoding", new Byte(textEncoding));
        setObject("Description", description);
        setObject("Text", text);
    }

    /**
     * Creates a new FrameBodyTXXX object.
     */
    public FrameBodyTXXX(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getBriefDescription() {
        return this.getText();
    }

    /**
     * @param description
     */
    public void setDescription(final String description) {
        setObject("Description", description);
    }

    public String getDescription() {
        return (String) getObject("Description");
    }

    public String getIdentifier() {
        return "TXXX" + ((char) 0) + getDescription();
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

    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap("Text Encoding", 1));
        appendToObjectList(new ObjectStringNullTerminated("Description"));
        appendToObjectList(new ObjectStringSizeTerminated("Text"));
    }
}