package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectByteArraySizeTerminated;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringNullTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Eric Farng Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyPIC extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyPIC object.
     */
    public FrameBodyPIC() {
        super();
    }

    /**
     * Creates a new FrameBodyPIC object.
     */
    public FrameBodyPIC(final FrameBodyPIC body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyPIC object.
     */
    public FrameBodyPIC(final byte textEncoding,
                        final String imageFormat,
                        final byte pictureType,
                        final String description,
                        final byte[] data) {
        setObject("Text Encoding", new Byte(textEncoding));
        setObject("Image Format", imageFormat);
        setObject("Picture Type", new Byte(pictureType));
        setObject("Description", description);
        setObject("Picture Data", data);
    }

    /**
     * Creates a new FrameBodyPIC object.
     */
    public FrameBodyPIC(final RandomAccessFile file) throws IOException, InvalidTagException {
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
        return "PIC" + ((char) 0) + getDescription();
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap(ObjectNumberHashMap.TEXT_ENCODING, 1));
        appendToObjectList(new ObjectStringNullTerminated("Image Format"));
        appendToObjectList(new ObjectNumberHashMap(ObjectNumberHashMap.PICTURE_TYPE, 3));
        appendToObjectList(new ObjectStringNullTerminated("Description"));
        appendToObjectList(new ObjectByteArraySizeTerminated("Picture Data"));
    }
}