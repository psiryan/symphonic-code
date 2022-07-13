package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectNumberVariableLength;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyPOSS extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyPOSS object.
     */
    public FrameBodyPOSS() {
        super();
    }

    /**
     * Creates a new FrameBodyPOSS object.
     */
    public FrameBodyPOSS(final FrameBodyPOSS body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyPOSS object.
     */
    public FrameBodyPOSS(final byte timeStampFormat, final long position) {
        setObject(ObjectNumberHashMap.TIME_STAMP_FORMAT, new Byte(timeStampFormat));
        setObject("Position", new Long(position));
    }

    /**
     * Creates a new FrameBodyPOSS object.
     */
    public FrameBodyPOSS(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        return "POSS";
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap(ObjectNumberHashMap.TIME_STAMP_FORMAT, 1));
        appendToObjectList(new ObjectNumberVariableLength("Position", 1));
    }
}