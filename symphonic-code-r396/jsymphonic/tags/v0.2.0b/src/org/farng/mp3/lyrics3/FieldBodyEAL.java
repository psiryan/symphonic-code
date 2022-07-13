package org.farng.mp3.lyrics3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectStringSizeTerminated;

import java.io.RandomAccessFile;

/**
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FieldBodyEAL extends AbstractLyrics3v2FieldBody {

    /**
     * Creates a new FieldBodyEAL object.
     */
    public FieldBodyEAL() {
        super();
    }

    /**
     * Creates a new FieldBodyEAL object.
     */
    public FieldBodyEAL(final FieldBodyEAL body) {
        super(body);
    }

    /**
     * Creates a new FieldBodyEAL object.
     */
    public FieldBodyEAL(final String album) {
        setObject("Album", album);
    }

    /**
     * Creates a new FieldBodyEAL object.
     */
    public FieldBodyEAL(final RandomAccessFile file) throws InvalidTagException, java.io.IOException {
        this.read(file);
    }

    /**
     * @param album
     */
    public void setAlbum(final String album) {
        setObject("Album", album);
    }

    public String getAlbum() {
        return (String) getObject("Album");
    }

    public String getIdentifier() {
        return "EAL";
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectStringSizeTerminated("Album"));
    }
}