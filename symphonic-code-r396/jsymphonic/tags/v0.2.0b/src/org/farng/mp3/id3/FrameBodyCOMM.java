package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringHashMap;
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
public class FrameBodyCOMM extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyCOMM object.
     */
    public FrameBodyCOMM() {
        super();
    }

    /**
     * Creates a new FrameBodyCOMM object.
     */
    public FrameBodyCOMM(final FrameBodyCOMM body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyCOMM object.
     */
    public FrameBodyCOMM(final byte textEncoding, final String language, final String description, final String text) {
        setObject(ObjectNumberHashMap.TEXT_ENCODING, new Byte(textEncoding));
        setObject(ObjectStringHashMap.LANGUAGE, language);
        setObject("Description", description);
        setObject("Text", text);
    }

    /**
     * Creates a new FrameBodyCOMM object.
     */
    public FrameBodyCOMM(final RandomAccessFile file) throws IOException, InvalidTagException {
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
        return "COMM" + ((char) 0) + getLanguage() + ((char) 0) + getDescription();
    }

    /**
     * @param language
     */
    public void setLanguage(final String language) {
        setObject(ObjectStringHashMap.LANGUAGE, language);
    }

    public String getLanguage() {
        return (String) getObject(ObjectStringHashMap.LANGUAGE);
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
        setObject(ObjectNumberHashMap.TEXT_ENCODING, new Byte(textEncoding));
    }

    public byte getTextEncoding() {
        return ((Byte) getObject(ObjectNumberHashMap.TEXT_ENCODING)).byteValue();
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap(ObjectNumberHashMap.TEXT_ENCODING, 1));
        appendToObjectList(new ObjectStringHashMap(ObjectStringHashMap.LANGUAGE, 3));
        appendToObjectList(new ObjectStringNullTerminated("Description"));
        appendToObjectList(new ObjectStringSizeTerminated("Text"));
    }
}