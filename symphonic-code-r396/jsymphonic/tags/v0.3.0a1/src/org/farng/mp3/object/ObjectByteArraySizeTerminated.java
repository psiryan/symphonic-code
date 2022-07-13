package org.farng.mp3.object;

/**
 * <p/>
 * Title: </p>
 * <p/>
 * <p/>
 * Description: </p>
 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2002 </p>
 * <p/>
 * <p/>
 * Company: </p>
 *
 * @author Eric Farng
 * @version $Revision: 1.3 $
 */
public class ObjectByteArraySizeTerminated extends AbstractMP3Object {

    /**
     * Creates a new ObjectByteArraySizeTerminated object.
     */
    public ObjectByteArraySizeTerminated(final String identifier) {
        this.identifier = identifier;
    }

    /**
     * Creates a new ObjectByteArraySizeTerminated object.
     */
    public ObjectByteArraySizeTerminated(final ObjectByteArraySizeTerminated object) {
        super(object);
    }

    public int getSize() {
        int len = 0;
        if (this.value != null) {
            len = ((byte[]) this.value).length;
        }
        return len;
    }

    public boolean equals(final Object obj) {
        if (obj instanceof ObjectByteArraySizeTerminated == false) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * @param arr
     * @param offset
     *
     * @throws NullPointerException
     * @throws IndexOutOfBoundsException
     */
    public void readByteArray(final byte[] arr, final int offset) {
        if (arr == null) {
            throw new NullPointerException("Byte array is null");
        }
        if ((offset < 0) || (offset >= arr.length)) {
            throw new IndexOutOfBoundsException("Offset to byte array is out of bounds: offset = " +
                                                offset +
                                                ", array.length = " +
                                                arr
                                                        .length);
        }
        final int len = arr.length - offset;
        this.value = new byte[len];
        System.arraycopy(arr, offset, this.value, 0, len);
    }

    public String toString() {
        return getSize() + " bytes";
    }

    public byte[] writeByteArray() {
        return (byte[]) this.value;
    }
}