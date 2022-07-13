package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectByteArraySizeTerminated;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringNullTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyAPIC extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyAPIC object.
     */
    public FrameBodyAPIC() {
        super();
    }

    /**
     * Creates a new FrameBodyAPIC object.
     */
    public FrameBodyAPIC(final FrameBodyAPIC body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyAPIC object.
     */
    public FrameBodyAPIC(final byte textEncoding,
                         final String mimeType,
                         final byte pictureType,
                         final String description,
                         final byte[] data) {
        super();
        setObject("Text Encoding", new Byte(textEncoding));
        setObject("MIME Type", mimeType);
        setObject("Picture Type", new Byte(pictureType));
        setObject("Description", description);
        setObject("Picture Data", data);
    }

    /**
     * Creates a new FrameBodyAPIC object.
     */
    public FrameBodyAPIC(final RandomAccessFile file) throws IOException, InvalidTagException {
        super();
        read(file);
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
        return "APIC" + (char) 0 + getDescription();
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap("Text Encoding", 1));
        appendToObjectList(new ObjectStringNullTerminated("MIME Type"));
        appendToObjectList(new ObjectStringNullTerminated("Description"));
        appendToObjectList(new ObjectByteArraySizeTerminated("Picture Data"));
    }
}