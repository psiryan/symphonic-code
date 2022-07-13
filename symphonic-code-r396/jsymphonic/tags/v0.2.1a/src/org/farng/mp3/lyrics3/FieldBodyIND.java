package org.farng.mp3.lyrics3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectBooleanString;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FieldBodyIND extends AbstractLyrics3v2FieldBody {

    /**
     * Creates a new FieldBodyIND object.
     */
    public FieldBodyIND() {
        super();
    }

    /**
     * Creates a new FieldBodyIND object.
     */
    public FieldBodyIND(final FieldBodyIND body) {
        super(body);
    }

    /**
     * Creates a new FieldBodyIND object.
     */
    public FieldBodyIND(final boolean lyricsPresent, final boolean timeStampPresent) {
        setObject("Lyrics Present", new Boolean(lyricsPresent));
        setObject("Timestamp Present", new Boolean(timeStampPresent));
    }

    /**
     * Creates a new FieldBodyIND object.
     */
    public FieldBodyIND(final RandomAccessFile file) throws InvalidTagException, java.io.IOException {
        this.read(file);
    }

    /**
     * @param author
     */
    public void setAuthor(final String author) {
        setObject("Author", author);
    }

    public String getAuthor() {
        return (String) getObject("Author");
    }

    public String getIdentifier() {
        return "IND";
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectBooleanString("Lyrics Present"));
        appendToObjectList(new ObjectBooleanString("Timestamp Present"));
    }
}