package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectNumberVariableLength;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyPCNT extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyPCNT object.
     */
    public FrameBodyPCNT() {
        super();
    }

    /**
     * Creates a new FrameBodyPCNT object.
     */
    public FrameBodyPCNT(final FrameBodyPCNT body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyPCNT object.
     */
    public FrameBodyPCNT(final long counter) {
        setObject("Counter", new Long(counter));
    }

    /**
     * Creates a new FrameBodyPCNT object.
     */
    public FrameBodyPCNT(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        return "PCNT";
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberVariableLength("Counter", 4));
    }
}