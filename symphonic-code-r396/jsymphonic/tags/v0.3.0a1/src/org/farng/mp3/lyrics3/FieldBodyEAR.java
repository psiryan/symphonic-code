package org.farng.mp3.lyrics3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectStringSizeTerminated;

import java.io.RandomAccessFile;

/**
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FieldBodyEAR extends AbstractLyrics3v2FieldBody {

    /**
     * Creates a new FieldBodyEAR object.
     */
    public FieldBodyEAR() {
        super();
    }

    /**
     * Creates a new FieldBodyEAR object.
     */
    public FieldBodyEAR(final FieldBodyEAR body) {
        super(body);
    }

    /**
     * Creates a new FieldBodyEAR object.
     */
    public FieldBodyEAR(final String artist) {
        setObject("Artist", artist);
    }

    /**
     * Creates a new FieldBodyEAR object.
     */
    public FieldBodyEAR(final RandomAccessFile file) throws InvalidTagException, java.io.IOException {
        this.read(file);
    }

    /**
     * @param artist
     */
    public void setArtist(final String artist) {
        setObject("Artist", artist);
    }

    public String getArtist() {
        return (String) getObject("Artist");
    }

    public String getIdentifier() {
        return "EAR";
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectStringSizeTerminated("Artist"));
    }
}