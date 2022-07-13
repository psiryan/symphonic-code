package org.farng.mp3.lyrics3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectStringSizeTerminated;

import java.io.RandomAccessFile;

/**
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FieldBodyETT extends AbstractLyrics3v2FieldBody {

    /**
     * Creates a new FieldBodyETT object.
     */
    public FieldBodyETT() {
        super();
    }

    /**
     * Creates a new FieldBodyETT object.
     */
    public FieldBodyETT(final FieldBodyETT body) {
        super(body);
    }

    /**
     * Creates a new FieldBodyETT object.
     */
    public FieldBodyETT(final String title) {
        setObject("Title", title);
    }

    /**
     * Creates a new FieldBodyETT object.
     */
    public FieldBodyETT(final RandomAccessFile file) throws InvalidTagException, java.io.IOException {
        this.read(file);
    }

    public String getIdentifier() {
        return "ETT";
    }

    /**
     * @param title
     */
    public void setTitle(final String title) {
        setObject("Title", title);
    }

    public String getTitle() {
        return (String) getObject("Title");
    }

    protected void setupObjectList() {
        appendToObjectList(new ObjectStringSizeTerminated("Title"));
    }
}