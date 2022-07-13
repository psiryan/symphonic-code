package org.farng.mp3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This class is a facade for all classes that can write to an MP3 file. It has abstract methods that needs to be
 * implemented, and a few default implementations of other methods.
 *
 * @author Eric Farng
 * @version $Revision: 1.1 $
 */
public abstract class AbstractMP3FileItem {

    /**
     * Creates a new AbstractMP3FileItem object.
     */
    protected AbstractMP3FileItem() {
        super();
    }

    /**
     * Creates a new AbstractMP3FileItem object.
     */
    protected AbstractMP3FileItem(final AbstractMP3FileItem copyObject) {
        super();
    }

    /**
     * ID string that usually corresponds to the class name, but can be displayed to the user. It is not indended to
     * identify each individual instance.
     *
     * @return ID string
     */
    public abstract String getIdentifier();

    public abstract int getSize();

    /**
     * import java.io.IOException; import java.io.RandomAccessFile; rent file pointer position.
     *
     * @param file file to read from
     *
     * @throws TagException on any exception generated by this library.
     * @throws IOException  on any I/O error
     */
    public abstract void read(RandomAccessFile file) throws TagException, IOException;

    /**
     * Method to write this object to the file argument at is current file pointer position.
     *
     * @param file file to write to
     *
     * @throws IOException on any I/O error
     */
    public abstract void write(RandomAccessFile file) throws TagException, IOException;

    /**
     * Returns true if this object is a subset of the argument. This instance is a subset if it is the same class as the
     * argument.
     *
     * @param object object to determine subset of
     *
     * @return true if this instance and its entire object array list is a subset of the argument.
     */
    public boolean isSubsetOf(final Object object) {
        return object instanceof AbstractMP3FileItem;
    }

    /**
     * Returns true if this object and its body equals the argument and its body. this object is equal if and only if
     * they are the same class
     *
     * @param obj object to determine equality of
     *
     * @return true if this object and its body are equal
     */
    public boolean equals(final Object obj) {
        return obj instanceof AbstractMP3FileItem;
    }
}