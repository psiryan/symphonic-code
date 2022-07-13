package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringHashMap;
import org.farng.mp3.object.ObjectStringSizeTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyUSER extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyUSER object.
     */
    public FrameBodyUSER() {
        super();
    }

    /**
     * Creates a new FrameBodyUSER object.
     */
    public FrameBodyUSER(final FrameBodyUSER body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyUSER object.
     */
    public FrameBodyUSER(final byte textEncoding, final String language, final String text) {
        setObject("Text Encoding", new Byte(textEncoding));
        setObject("Language", language);
        setObject("Text", text);
    }

    /**
     * Creates a new FrameBodyUSER object.
     */
    public FrameBodyUSER(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        return "USER" + ((char) 0) + getLanguage();
    }

    public String getLanguage() {
        return (String) getObject(ObjectStringHashMap.LANGUAGE);
    }

    /**
     * @param language
     */
    public void setOwner(final String language) {
        setObject(ObjectStringHashMap.LANGUAGE, language);
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap(ObjectNumberHashMap.TEXT_ENCODING, 1));
        appendToObjectList(new ObjectStringHashMap(ObjectStringHashMap.LANGUAGE, 3));
        appendToObjectList(new ObjectStringSizeTerminated("Text"));
    }
}