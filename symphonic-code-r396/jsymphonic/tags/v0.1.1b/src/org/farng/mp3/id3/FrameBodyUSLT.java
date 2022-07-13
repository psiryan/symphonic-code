package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectLyrics3Line;
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
public class FrameBodyUSLT extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyUSLT object.
     */
    public FrameBodyUSLT() {
        super();
    }

    /**
     * Creates a new FrameBodyUSLT object.
     */
    public FrameBodyUSLT(final FrameBodyUSLT body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyUSLT object.
     */
    public FrameBodyUSLT(final byte textEncoding, final String language, final String description, final String text) {
        setObject("Text Encoding", new Byte(textEncoding));
        setObject("Language", language);
        setObject("Description", description);
        setObject("Lyrics/Text", text);
    }

    /**
     * Creates a new FrameBodyUSLT object.
     */
    public FrameBodyUSLT(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
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
        return "USLT" + ((char) 0) + getLanguage() + ((char) 0) + getDescription();
    }

    /**
     * @param language
     */
    public void setLanguage(final String language) {
        setObject("Language", language);
    }

    public String getLanguage() {
        return (String) getObject("Language");
    }

    /**
     * @param lyric
     */
    public void setLyric(final String lyric) {
        setObject("Lyrics/Text", lyric);
    }

    public String getLyric() {
        return (String) getObject("Lyrics/Text");
    }

    /**
     * @param text
     */
    public void addLyric(final String text) {
        setLyric(getLyric() + text);
    }

    /**
     * @param line
     */
    public void addLyric(final ObjectLyrics3Line line) {
        setLyric(getLyric() + line.writeString());
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap("Text Encoding", 1));
        appendToObjectList(new ObjectStringHashMap("Language", 3));
        appendToObjectList(new ObjectStringNullTerminated("Description"));
        appendToObjectList(new ObjectStringSizeTerminated("Lyrics/Text"));
    }
}