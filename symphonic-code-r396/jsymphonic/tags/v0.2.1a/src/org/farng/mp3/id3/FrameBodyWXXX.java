package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectNumberHashMap;
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
public class FrameBodyWXXX extends AbstractID3v2FrameBody {

    /**
     *
     */
    String description = "";
    /**
     *
     */
    String urlLink = "";
    /**
     *
     */
    byte textEncoding = 0;

    /**
     * Creates a new FrameBodyWXXX object.
     */
    public FrameBodyWXXX() {
        super();
    }

    /**
     * Creates a new FrameBodyWXXX object.
     */
    public FrameBodyWXXX(final FrameBodyWXXX body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyWXXX object.
     */
    public FrameBodyWXXX(final byte textEncoding, final String description, final String urlLink) {
        setObject("Text Encoding", new Byte(textEncoding));
        setObject("Description", description);
        setObject("URL", urlLink);
    }

    /**
     * Creates a new FrameBodyWXXX object.
     */
    public FrameBodyWXXX(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getBriefDescription() {
        return this.getUrlLink();
    }

    public String getIdentifier() {
        return "WXXX" + ((char) 0) + this.description;
    }

    /**
     * @param urlLink
     */
    public void setUrlLink(final String urlLink) {
        setObject("URL", urlLink);
    }

    public String getUrlLink() {
        return (String) getObject("URL");
    }

    public boolean equals(final Object obj) {
        if ((obj instanceof FrameBodyWXXX) == false) {
            return false;
        }
        final FrameBodyWXXX frameBodyWXXX = (FrameBodyWXXX) obj;
        if (this.description.equals(frameBodyWXXX.description) == false) {
            return false;
        }
        if (this.textEncoding != frameBodyWXXX.textEncoding) {
            return false;
        }
        if (this.urlLink.equals(frameBodyWXXX.urlLink) == false) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap("Text Encoding", 1));
        appendToObjectList(new ObjectStringNullTerminated("Description"));
        appendToObjectList(new ObjectStringSizeTerminated("URL"));
    }
}