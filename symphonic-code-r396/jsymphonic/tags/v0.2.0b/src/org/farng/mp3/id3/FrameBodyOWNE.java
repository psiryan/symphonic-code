package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringDate;
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
public class FrameBodyOWNE extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyOWNE object.
     */
    public FrameBodyOWNE() {
        super();
    }

    /**
     * Creates a new FrameBodyOWNE object.
     */
    public FrameBodyOWNE(final FrameBodyOWNE body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyOWNE object.
     */
    public FrameBodyOWNE(final byte textEncoding,
                         final String pricePaid,
                         final String dateOfPurchase,
                         final String seller) {
        setObject("Text Encoding", new Byte(textEncoding));
        setObject("Price Paid", pricePaid);
        setObject("Date Of Purchase", dateOfPurchase);
        setObject("Seller", seller);
    }

    /**
     * Creates a new FrameBodyOWNE object.
     */
    public FrameBodyOWNE(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        return "OWNE";
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap(ObjectNumberHashMap.TEXT_ENCODING, 1));
        appendToObjectList(new ObjectStringNullTerminated("Price Paid"));
        appendToObjectList(new ObjectStringDate("Date Of Purchase"));
        appendToObjectList(new ObjectStringSizeTerminated("Seller"));
    }
}