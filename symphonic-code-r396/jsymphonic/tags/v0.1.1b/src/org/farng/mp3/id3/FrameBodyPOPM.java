package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectNumberFixedLength;
import org.farng.mp3.object.ObjectNumberVariableLength;
import org.farng.mp3.object.ObjectStringNullTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyPOPM extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyPOPM object.
     */
    public FrameBodyPOPM() {
        super();
    }

    /**
     * Creates a new FrameBodyPOPM object.
     */
    public FrameBodyPOPM(final FrameBodyPOPM body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyPOPM object.
     */
    public FrameBodyPOPM(final String emailToUser, final byte rating, final long counter) {
        setObject("Email to User", emailToUser);
        setObject("Rating", new Byte(rating));
        setObject("Counter", new Long(counter));
    }

    /**
     * Creates a new FrameBodyPOPM object.
     */
    public FrameBodyPOPM(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    /**
     * @param description
     */
    public void setEmailToUser(final String description) {
        setObject("Email to User", description);
    }

    public String getEmailToUser() {
        return (String) getObject("Email to User");
    }

    public String getIdentifier() {
        return "POPM" + ((char) 0) + getEmailToUser();
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectStringNullTerminated("Email to User"));
        appendToObjectList(new ObjectNumberFixedLength("Rating", 1));
        appendToObjectList(new ObjectNumberVariableLength("Counter", 1));
    }
}