package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectByteArraySizeTerminated;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringNullTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyGEOB extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyGEOB object.
     */
    public FrameBodyGEOB() {
        super();
    }

    /**
     * Creates a new FrameBodyGEOB object.
     */
    public FrameBodyGEOB(final FrameBodyGEOB body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyGEOB object.
     */
    public FrameBodyGEOB(final byte textEncoding,
                         final String mimeType,
                         final String filename,
                         final String description,
                         final byte[] object) {
        setObject("TextEncoding", new Byte(textEncoding));
        setObject("MIME Type", mimeType);
        setObject("Filename", filename);
        setObject("Description", description);
        setObject("Encapsulated Object", object);
    }

    /**
     * Creates a new FrameBodyGEOB object.
     */
    public FrameBodyGEOB(final RandomAccessFile file) throws IOException, InvalidTagException {
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
        return "GEOB" + ((char) 0) + getDescription();
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap(ObjectNumberHashMap.TEXT_ENCODING, 1));
        appendToObjectList(new ObjectStringNullTerminated("MIME Type"));
        appendToObjectList(new ObjectStringNullTerminated("Filename"));
        appendToObjectList(new ObjectStringNullTerminated("Description"));
        appendToObjectList(new ObjectByteArraySizeTerminated("Encapsulated Object"));
    }
}