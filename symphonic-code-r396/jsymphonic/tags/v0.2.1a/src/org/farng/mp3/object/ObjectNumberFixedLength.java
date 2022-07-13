package org.farng.mp3.object;

import org.farng.mp3.TagUtility;

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
public class ObjectNumberFixedLength extends AbstractMP3Object {

    /**
     *
     */
    int length = 0;

    /**
     * Creates a new ObjectNumberFixedLength object.
     */
    public ObjectNumberFixedLength(final String identifier, final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Length is less than zero: " + this.length);
        }
        this.length = size;
        this.identifier = identifier;
    }

    /**
     * Creates a new ObjectNumberFixedLength object.
     */
    public ObjectNumberFixedLength(final ObjectNumberFixedLength copyObject) {
        super(copyObject);
        this.length = copyObject.length;
    }

    public int getLength() {
        return this.length;
    }

    /**
     * @param length
     */
    public void setSize(final int length) {
        if (length > 0) {
            this.length = length;
        }
    }

    public int getSize() {
        return this.length;
    }

    public boolean equals(final Object obj) {
        if ((obj instanceof ObjectNumberFixedLength) == false) {
            return false;
        }
        final ObjectNumberFixedLength objectNumberFixedLength = (ObjectNumberFixedLength) obj;
        if (this.length != objectNumberFixedLength.length) {
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
        long lvalue = 0;
        for (int i = offset; i < (offset + this.length); i++) {
            lvalue <<= 8;
            lvalue += arr[i];
        }
        this.value = new Long(lvalue);
    }

    /**
     * @param str
     * @param offset
     *
     * @throws NullPointerException
     * @throws IndexOutOfBoundsException
     */
    public void readString(final String str, final int offset) {
        if (str == null) {
            throw new NullPointerException("Number string is null");
        }
        if ((offset < 0) || (offset >= str.length())) {
            throw new IndexOutOfBoundsException("Offset to number string is out of bounds: offset = " +
                                                offset +
                                                ", string.length()" +
                                                str.length());
        }
        this.value = Long.getLong(str.substring(offset));
    }

    public String toString() {
        if (this.value == null) {
            return "";
        }
        return this.value.toString();
    }

    public byte[] writeByteArray() {
        final byte[] arr;
        arr = new byte[this.length];
        if (this.value != null) {
            long temp = TagUtility.getWholeNumber(this.value);
            for (int i = this.length - 1; i >= 0; i--) {
                arr[i] = (byte) (temp & 0xFF);
                temp >>= 8;
            }
        }
        return arr;
    }

    public String writeString() {
        if (this.value == null) {
            return String.valueOf(new char[this.length]);
        }
        return this.value.toString();
    }
}