package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectStringSizeTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public abstract class AbstractFrameBodyUrlLink extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyUrlLink object.
     */
    protected AbstractFrameBodyUrlLink() {
        super();
    }

    /**
     * Creates a new AbstractFrameBodyUrlLink object.
     */
    protected AbstractFrameBodyUrlLink(final AbstractFrameBodyUrlLink body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyUrlLink object.
     */
    protected AbstractFrameBodyUrlLink(final String urlLink) {
        super();
        setObject("URL Link", urlLink);
    }

    /**
     * Creates a new FrameBodyUrlLink object.
     */
    protected AbstractFrameBodyUrlLink(final RandomAccessFile file) throws IOException, InvalidTagException {
        super();
        read(file);
    }

    public String getBriefDescription() {
        return getUrlLink();
    }

    /**
     * @param urlLink
     */
    public void setUrlLink(final String urlLink) {
        setObject("URL Link", urlLink);
    }

    public String getUrlLink() {
        return (String) getObject("URL Link");
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectStringSizeTerminated("URL Link"));
    }
}