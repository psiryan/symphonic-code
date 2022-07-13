package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.AbstractMP3Object;
import org.farng.mp3.object.ObjectGroupRepeated;
import org.farng.mp3.object.ObjectNumberFixedLength;
import org.farng.mp3.object.ObjectNumberHashMap;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyETCO extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyETCO object.
     */
    public FrameBodyETCO() {
        super();
    }

    /**
     * Creates a new FrameBodyETCO object.
     */
    public FrameBodyETCO(final FrameBodyETCO body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyETCO object.
     */
    public FrameBodyETCO(final byte timeStampFormat, final byte event, final int timeStamp) {
        setObject("Time Stamp Format", new Long(timeStampFormat));
        this.addGroup(event, timeStamp);
    }

    /**
     * Creates a new FrameBodyETCO object.
     */
    public FrameBodyETCO(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        return "ETCO" + ((char) 0) + getOwner();
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
     * @param event
     * @param timeStamp
     */
    public void addGroup(final byte event, final int timeStamp) {
        final ObjectGroupRepeated group = (ObjectGroupRepeated) this.getObject("Data");
        final AbstractMP3Object ev = new ObjectNumberHashMap("Type Of Event", 1);
        final AbstractMP3Object ts = new ObjectNumberFixedLength("Time Stamp", 4);
        group.addObject(ev);
        group.addObject(ts);
        setObject("Data", group);
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap("Time Stamp Format", 1));
        final ObjectGroupRepeated group = new ObjectGroupRepeated("Data");
        group.addProperty(new ObjectNumberHashMap("Type Of Event", 1));
        group.addProperty(new ObjectNumberFixedLength("Time Stamp", 4));
        appendToObjectList(group);
    }
}