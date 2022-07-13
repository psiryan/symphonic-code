package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectStringFixedLength;
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
public class FrameBodyLINK extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyLINK object.
     */
    public FrameBodyLINK() {
        super();
    }

    /**
     * Creates a new FrameBodyLINK object.
     */
    public FrameBodyLINK(final FrameBodyLINK body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyLINK object.
     */
    public FrameBodyLINK(final String frameIdentifier, final String url, final String additionalData) {
        setObject("Frame Identifier", frameIdentifier);
        setObject("URL", url);
        setObject("ID and Additional Data", additionalData);
    }

    /**
     * Creates a new FrameBodyLINK object.
     */
    public FrameBodyLINK(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getAdditionalData() {
        return (String) getObject("ID and Additional Data");
    }

    /**
     * @param additionalData
     */
    public void getAdditionalData(final String additionalData) {
        setObject("ID and Additional Data", additionalData);
    }

    public String getFrameIdentifier() {
        return (String) getObject("Frame Identifier");
    }

    /**
     * @param frameIdentifier
     */
    public void getFrameIdentifier(final String frameIdentifier) {
        setObject("Frame Identifier", frameIdentifier);
    }

    public String getIdentifier() {
        return "LINK" + ((char) 0) + getFrameIdentifier() + ((char) 0) + getAdditionalData();
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectStringFixedLength("Frame Identifier", 4));
        appendToObjectList(new ObjectStringNullTerminated("URL"));
        appendToObjectList(new ObjectStringSizeTerminated("ID and Additional Data"));
    }
}