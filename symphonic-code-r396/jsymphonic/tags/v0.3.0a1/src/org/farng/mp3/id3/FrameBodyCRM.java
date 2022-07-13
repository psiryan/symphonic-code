package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectByteArraySizeTerminated;
import org.farng.mp3.object.ObjectStringNullTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyCRM extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyCRM object.
     */
    public FrameBodyCRM() {
        super();
    }

    /**
     * Creates a new FrameBodyCRM object.
     */
    public FrameBodyCRM(final FrameBodyCRM body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyCRM object.
     */
    public FrameBodyCRM(final String owner, final String description, final byte[] data) {
        setObject("Owner", owner);
        setObject("Description", description);
        setObject("Encrypted datablock", data);
    }

    /**
     * Creates a new FrameBodyCRM object.
     */
    public FrameBodyCRM(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        return "CRM" + ((char) 0) + getOwner();
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
        appendToObjectList(new ObjectStringNullTerminated("Description"));
        appendToObjectList(new ObjectByteArraySizeTerminated("Encrypted datablock"));
    }
}