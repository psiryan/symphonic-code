package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectByteArraySizeTerminated;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringDate;
import org.farng.mp3.object.ObjectStringNullTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyCOMR extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyCOMR object.
     */
    public FrameBodyCOMR() {
        super();
    }

    /**
     * Creates a new FrameBodyCOMR object.
     */
    public FrameBodyCOMR(final FrameBodyCOMR body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyCOMR object.
     */
    public FrameBodyCOMR(final byte textEncoding,
                         final String priceString,
                         final String validUntil,
                         final String contactUrl,
                         final byte recievedAs,
                         final String nameOfSeller,
                         final String description,
                         final String mimeType,
                         final byte[] sellerLogo) {
        setObject("Text Encoding", new Byte(textEncoding));
        setObject("Price String", priceString);
        setObject("Valid Until", validUntil);
        setObject("Contact URL", contactUrl);
        setObject("Recieved As", new Byte(recievedAs));
        setObject("Name Of Seller", nameOfSeller);
        setObject("Description", description);
        setObject("Picture MIME Type", mimeType);
        setObject("Seller Logo", sellerLogo);
    }

    /**
     * Creates a new FrameBodyCOMR object.
     */
    public FrameBodyCOMR(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        String str = "COMR";
        final java.util.Iterator iterator = getObjectListIterator();
        while (iterator.hasNext()) {
            str += (((char) 0) + getOwner());
        }
        return str;
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
        appendToObjectList(new ObjectNumberHashMap(ObjectNumberHashMap.TEXT_ENCODING, 1));
        appendToObjectList(new ObjectStringNullTerminated("Price String"));
        appendToObjectList(new ObjectStringDate("Valid Until"));
        appendToObjectList(new ObjectStringNullTerminated("Contact URL"));
        appendToObjectList(new ObjectNumberHashMap(ObjectNumberHashMap.RECIEVED_AS, 1));
        appendToObjectList(new ObjectStringNullTerminated("Name Of Seller"));
        appendToObjectList(new ObjectStringNullTerminated("Description"));
        appendToObjectList(new ObjectStringNullTerminated("Picture MIME Type"));
        appendToObjectList(new ObjectByteArraySizeTerminated("Seller Logo"));
    }
}