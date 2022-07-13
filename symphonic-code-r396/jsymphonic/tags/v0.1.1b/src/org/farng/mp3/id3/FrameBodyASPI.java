package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.3 $
 */
public class FrameBodyASPI extends AbstractID3v2FrameBody {

    /**
     *
     */
    private short[] fraction = null;
    /**
     *
     */
    private int bitsPerPoint = 0;
    /**
     *
     */
    private int dataLength = 0;
    /**
     *
     */
    private int dataStart = 0;
    /**
     *
     */
    private int indexPoints = 0;

    /**
     * Creates a new FrameBodyASPI object.
     */
    public FrameBodyASPI() {
        super();
    }

    /**
     * Creates a new FrameBodyASPI object.
     */
    public FrameBodyASPI(final FrameBodyASPI copyObject) {
        super(copyObject);
        fraction = (short[]) copyObject.fraction.clone();
        bitsPerPoint = copyObject.bitsPerPoint;
        dataLength = copyObject.dataLength;
        dataStart = copyObject.dataStart;
        indexPoints = copyObject.indexPoints;
    }

    /**
     * Creates a new FrameBodyASPI object.
     */
    public FrameBodyASPI(final int dataStart,
                         final int dataLength,
                         final int indexPoints,
                         final int bitsPerPoint,
                         final short[] fraction) {
        super();
        this.dataStart = dataStart;
        this.dataLength = dataLength;
        this.indexPoints = indexPoints;
        this.bitsPerPoint = bitsPerPoint;
        this.fraction = new short[fraction.length];
        System.arraycopy(fraction, 0, this.fraction, 0, fraction.length);
    }

    /**
     * Creates a new FrameBodyASPI object.
     */
    public FrameBodyASPI(final RandomAccessFile file) throws IOException, InvalidTagException {
        super();
        read(file);
    }

    public String getIdentifier() {
        return "ASPI";
    }

    public int getSize() {
        return 4 + 4 + 2 + 1 + fraction.length << 1;
    }

    /**
     * This method is not yet supported.
     *
     * @throws UnsupportedOperationException This method is not yet supported
     */
    public void equals() {
        // todo Implement this java.lang.Object method
        throw new UnsupportedOperationException("Method equals() not yet implemented.");
    }

    protected void setupObjectList() {
//        throw new UnsupportedOperationException();
    }

    /**
     * @param file
     *
     * @throws IOException
     * @throws InvalidTagException
     */
    public void read(final RandomAccessFile file) throws IOException, InvalidTagException {
        final int size = readHeader(file);
        if (size == 0) {
            throw new InvalidTagException("Empty Frame");
        }
        dataStart = file.readInt();
        dataLength = file.readInt();
        indexPoints = (int) file.readShort();
        bitsPerPoint = (int) file.readByte();
        fraction = new short[indexPoints];
        for (int i = 0; i < indexPoints; i++) {
            if (bitsPerPoint == 8) {
                fraction[i] = (short) file.readByte();
            } else if (bitsPerPoint == 16) {
                fraction[i] = file.readShort();
            } else {
                throw new InvalidTagException("ASPI bits per point wasn't 8 or 16");
            }
        }
    }

    public String toString() {
        return getIdentifier() + ' ' + this
                .dataStart + ' ' + this
                .dataLength + ' ' + this
                .indexPoints + ' ' + this
                .bitsPerPoint + ' ' + this.fraction
                .toString();
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public void write(final RandomAccessFile file) throws IOException {
        writeHeader(file, getSize());
        file.writeInt(dataStart);
        file.writeInt(dataLength);
        file.writeShort(indexPoints);
        file.writeByte(16);
        for (int i = 0; i < indexPoints; i++) {
            file.writeShort((int) fraction[i]);
        }
    }
}