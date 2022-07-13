package org.farng.mp3.lyrics3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectStringSizeTerminated;

import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FieldBodyAUT extends AbstractLyrics3v2FieldBody {

    /**
     * Creates a new FieldBodyAUT object.
     */
    public FieldBodyAUT() {
        super();
    }

    /**
     * Creates a new FieldBodyAUT object.
     */
    public FieldBodyAUT(final FieldBodyAUT body) {
        super(body);
    }

    /**
     * Creates a new FieldBodyAUT object.
     */
    public FieldBodyAUT(final String author) {
        setObject("Author", author);
    }

    /**
     * Creates a new FieldBodyAUT object.
     */
    public FieldBodyAUT(final RandomAccessFile file) throws InvalidTagException, java.io.IOException {
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
        return "AUT";
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectStringSizeTerminated("Author"));
    }
}