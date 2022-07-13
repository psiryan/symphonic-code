package org.farng.mp3.object;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.3 $
 */
public class ObjectLyrics3TimeStamp extends AbstractMP3Object {

    /**
     *
     */
    private long minute = 0;
    /**
     *
     */
    private long second = 0;
    private byte timeStampFormat = 0;

    /**
     * Creates a new ObjectLyrics3TimeStamp object.
     */
    public ObjectLyrics3TimeStamp(final String identifier) {
        this.identifier = identifier;
    }

    /**
     * Creates a new ObjectLyrics3TimeStamp object.
     */
    public ObjectLyrics3TimeStamp(final ObjectLyrics3TimeStamp copyObject) {
        super(copyObject);
        this.minute = copyObject.minute;
        this.second = copyObject.second;
    }

    /**
     * @param minute
     */
    public void setMinute(final long minute) {
        this.minute = minute;
    }

    public long getMinute() {
        return this.minute;
    }

    /**
     * @return Returns the timeStampFormat.
     */
    public byte getTimeStampFormat() {
        return this.timeStampFormat;
    }

    /**
     * @param timeStampFormat The timeStampFormat to set.
     */
    public void setTimeStampFormat(final byte timeStampFormat) {
        this.timeStampFormat = timeStampFormat;
    }

    /**
     * @param second
     */
    public void setSecond(final long second) {
        this.second = second;
    }

    public long getSecond() {
        return this.second;
    }

    public int getSize() {
        return 7;
    }

    /**
     * Creates a new ObjectLyrics3TimeStamp object.
     */
    public void setTimeStamp(long timeStamp, final byte timeStampFormat) {
        // todo convert both types of formats
        timeStamp = timeStamp / 1000;
        this.minute = timeStamp / 60;
        this.second = timeStamp % 60;
        this.timeStampFormat = timeStampFormat;
    }

    public boolean equals(final Object obj) {
        if ((obj instanceof ObjectLyrics3TimeStamp) == false) {
            return false;
        }
        final ObjectLyrics3TimeStamp objectLyrics3TimeStamp = (ObjectLyrics3TimeStamp) obj;
        if (this.minute != objectLyrics3TimeStamp.minute) {
            return false;
        }
        if (this.second != objectLyrics3TimeStamp.second) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * @param timeStamp
     * @param offset
     *
     * @throws NullPointerException
     * @throws IndexOutOfBoundsException
     */
    public void readString(String timeStamp, final int offset) {
        if (timeStamp == null) {
            throw new NullPointerException("Image is null");
        }
        if ((offset < 0) || (offset >= timeStamp.length())) {
            throw new IndexOutOfBoundsException("Offset to timeStamp is out of bounds: offset = " +
                                                offset +
                                                ", timeStamp.length()" +
                                                timeStamp.length());
        }
        timeStamp = timeStamp.substring(offset);
        if (timeStamp.length() == 7) {
            this.minute = Integer.parseInt(timeStamp.substring(1, 3));
            this.second = Integer.parseInt(timeStamp.substring(4, 6));
        } else {
            this.minute = 0;
            this.second = 0;
        }
    }

    public String toString() {
        return writeString();
    }

    public String writeString() {
        String str;
        str = "[";
        if (this.minute < 0) {
            str += "00";
        } else {
            if (this.minute < 10) {
                str += '0';
            }
            str += Long.toString(this.minute);
        }
        str += ':';
        if (this.second < 0) {
            str += "00";
        } else {
            if (this.second < 10) {
                str += '0';
            }
            str += Long.toString(this.second);
        }
        str += ']';
        return str;
    }
}