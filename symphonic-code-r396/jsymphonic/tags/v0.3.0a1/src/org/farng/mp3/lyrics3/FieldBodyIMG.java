package org.farng.mp3.lyrics3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.TagConstant;
import org.farng.mp3.TagOptionSingleton;
import org.farng.mp3.object.ObjectLyrics3Image;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FieldBodyIMG extends AbstractLyrics3v2FieldBody {

    /**
     *
     */
    private ArrayList images = new ArrayList();

    /**
     * Creates a new FieldBodyIMG object.
     */
    public FieldBodyIMG() {
        super();
    }

    /**
     * Creates a new FieldBodyIMG object.
     */
    public FieldBodyIMG(final FieldBodyIMG copyObject) {
        super(copyObject);
        ObjectLyrics3Image oldObject;
        for (int i = 0; i < copyObject.images.size(); i++) {
            oldObject = (ObjectLyrics3Image) copyObject.images.get(i);
            this.images.add(new ObjectLyrics3Image(oldObject));
        }
    }

    /**
     * Creates a new FieldBodyIMG object.
     */
    public FieldBodyIMG(final String imageString) {
        readString(imageString);
    }

    /**
     * Creates a new FieldBodyIMG object.
     */
    public FieldBodyIMG(final ObjectLyrics3Image image) {
        this.images.add(image);
    }

    /**
     * Creates a new FieldBodyIMG object.
     */
    public FieldBodyIMG(final RandomAccessFile file) throws InvalidTagException, java.io.IOException {
        this.read(file);
    }

    public String getIdentifier() {
        return "IMG";
    }

    public int getSize() {
        int size = 0;
        ObjectLyrics3Image image;
        for (int i = 0; i < this.images.size(); i++) {
            image = (ObjectLyrics3Image) this.images.get(i);
            size += (image.getSize() + 2); // add CRLF pair
        }
        return size - 2; // cut off trailing crlf pair
    }

    public boolean isSubsetOf(final Object object) {
        if ((object instanceof FieldBodyIMG) == false) {
            return false;
        }
        final ArrayList superset = ((FieldBodyIMG) object).images;
        for (int i = 0; i < this.images.size(); i++) {
            if (superset.contains(this.images.get(i)) == false) {
                return false;
            }
        }
        return super.isSubsetOf(object);
    }

    /**
     * @param value
     */
    public void setValue(final String value) {
        readString(value);
    }

    public String getValue() {
        return writeString();
    }

    /**
     * @param image
     */
    public void addImage(final ObjectLyrics3Image image) {
        this.images.add(image);
    }

    public boolean equals(final Object obj) {
        if ((obj instanceof FieldBodyIMG) == false) {
            return false;
        }
        final FieldBodyIMG fieldBodyIMG = (FieldBodyIMG) obj;
        if (this.images.equals(fieldBodyIMG.images) == false) {
            return false;
        }
        return super.equals(obj);
    }

    public Iterator iterator() {
        return this.images.iterator();
    }

    protected void setupObjectList() {
//        throw new UnsupportedOperationException();
    }

    /**
     * @param file
     *
     * @throws InvalidTagException
     * @throws java.io.IOException
     */
    public void read(final RandomAccessFile file) throws InvalidTagException, java.io.IOException {
        final String imageString;
        byte[] buffer = new byte[5];

        // read the 5 character size
        file.read(buffer, 0, 5);
        final int size = Integer.parseInt(new String(buffer, 0, 5));
        if ((size == 0) && (TagOptionSingleton.getInstance().isLyrics3KeepEmptyFieldIfRead() == false)) {
            throw new InvalidTagException("Lyircs3v2 Field has size of zero.");
        }
        buffer = new byte[size];

        // read the SIZE length description
        file.read(buffer);
        imageString = new String(buffer);
        readString(imageString);
    }

    public String toString() {
        String str = getIdentifier() + " : ";
        for (int i = 0; i < this.images.size(); i++) {
            str += (this.images.get(i).toString() + " ; ");
        }
        return str;
    }

    /**
     * @param file
     *
     * @throws java.io.IOException
     */
    public void write(final RandomAccessFile file) throws java.io.IOException {
        final int size;
        int offset = 0;
        byte[] buffer = new byte[5];
        String str;
        size = getSize();
        str = Integer.toString(size);
        for (int i = 0; i < (5 - str.length()); i++) {
            buffer[i] = (byte) '0';
        }
        offset += (5 - str.length());
        for (int i = 0; i < str.length(); i++) {
            buffer[i + offset] = (byte) str.charAt(i);
        }
        offset += str.length();
        file.write(buffer, 0, 5);
        if (size > 0) {
            str = writeString();
            buffer = new byte[str.length()];
            for (int i = 0; i < str.length(); i++) {
                buffer[i] = (byte) str.charAt(i);
            }
            file.write(buffer);
        }
    }

    /**
     * @param imageString
     */
    private void readString(final String imageString) {
        // now read each picture and put in the vector;
        ObjectLyrics3Image image;
        String token;
        int offset = 0;
        int delim = imageString.indexOf(TagConstant.SEPERATOR_LINE);
        this.images = new ArrayList();
        while (delim >= 0) {
            token = imageString.substring(offset, delim);
            image = new ObjectLyrics3Image("Image");
            image.setFilename(token);
            this.images.add(image);
            offset = delim + TagConstant.SEPERATOR_LINE.length();
            delim = imageString.indexOf(TagConstant.SEPERATOR_LINE, offset);
        }
        if (offset < imageString.length()) {
            token = imageString.substring(offset);
            image = new ObjectLyrics3Image("Image");
            image.setFilename(token);
            this.images.add(image);
        }
    }

    private String writeString() {
        String str = "";
        ObjectLyrics3Image image;
        for (int i = 0; i < this.images.size(); i++) {
            image = (ObjectLyrics3Image) this.images.get(i);
            str += (image.writeString() + TagConstant.SEPERATOR_LINE);
        }
        if (str.length() > 2) {
            return str.substring(0, str.length() - 2);
        }
        return str;
    }
}