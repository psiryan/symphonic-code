package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectByteArraySizeTerminated;
import org.farng.mp3.object.ObjectNumberFixedLength;
import org.farng.mp3.object.ObjectStringNullTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyAENC extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyAENC object.
     */
    public FrameBodyAENC() {
        super();
    }

    /**
     * Creates a new FrameBodyAENC object.
     */
    public FrameBodyAENC(final FrameBodyAENC body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyAENC object.
     */
    public FrameBodyAENC(final String owner, final short previewStart, final short previewLength, final byte[] data) {
        super();
        setObject("Owner", owner);
        setObject("Preview Start", new Short(previewStart));
        setObject("Preview Length", new Short(previewLength));
        setObject("Encryption Info", data);
    }

    /**
     * Creates a new FrameBodyAENC object.
     */
    public FrameBodyAENC(final RandomAccessFile file) throws IOException, InvalidTagException {
        super();
        read(file);
    }

    public String getIdentifier() {
        return "AENC" + (char) 0 + getOwner();
    }

    public String getOwner() {
        return (String) getObject("Owner");
    }

    /**
     * @param description
     */
    public void getOwner(final String description) {
        setObject("Owner", description);
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectStringNullTerminated("Owner"));
        appendToObjectList(new ObjectNumberFixedLength("Preview Start", 2));
        appendToObjectList(new ObjectNumberFixedLength("Preview Length", 2));
        appendToObjectList(new ObjectByteArraySizeTerminated("Encryption Info"));
    }
}