package org.farng.mp3.id3;

import org.farng.mp3.AbstractMP3FragmentBody;
import org.farng.mp3.InvalidTagException;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Eric Farng Eric Farng
 * @version $Revision: 1.2 $
 */
public abstract class AbstractID3v2FrameBody extends AbstractMP3FragmentBody {

    /**
     * Creates a new AbstractID3v2FrameBody object.
     */
    protected AbstractID3v2FrameBody() {
        super();
    }

    /**
     * Creates a new AbstractID3v2FrameBody object.
     */
    protected AbstractID3v2FrameBody(final AbstractID3v2FrameBody copyObject) {
        super(copyObject);
    }

//    /**
//     *
//     *
//     * @return
//     */
//    public String getIdentifier() {
//        // todo Make this abstract. Can't do that yet because not all frame bodies
//        // have been finished.
//        return "";
//    }

    public boolean equals(final Object obj) {
        return obj instanceof AbstractID3v2FrameBody && super.equals(obj);
    }

    /**
     * @param file
     *
     * @throws IOException
     * @throws InvalidTagException
     */
    protected int readHeader(final RandomAccessFile file) throws IOException, InvalidTagException {
        final int size;
        final byte[] buffer = new byte[3];
        if (has6ByteHeader()) {
            // read the 3 byte size
            file.read(buffer, 0, 3);
            size = (int) (buffer[0] * Math.pow(2, 16) + buffer[1] * Math.pow(2, 8) + buffer[2]);
        } else {
            // read the 4 byte size
            size = file.readInt();

            // we need to skip the flag bytes;
            file.skipBytes(2);
        }
        if (size == 0) {
            throw new InvalidTagException("Found empty frame");
        }
        if (size <= 0 || size > file.length()) {
            throw new InvalidTagException("Invalid size for Frame Body");
        }
        return size;
    }

    /**
     * @param file
     * @param size
     *
     * @throws IOException
     */
    protected void writeHeader(final RandomAccessFile file, final int size) throws IOException {
        final byte[] buffer = new byte[3];
        if (has6ByteHeader()) {
            // write the 3 byte size;
            buffer[0] = (byte) ((size & 0x00FF0000) >> 16);
            buffer[1] = (byte) ((size & 0x0000FF00) >> 8);
            buffer[2] = (byte) (size & 0x000000FF);
            file.write(buffer);
        } else {
            // write the 4 byte size;
            file.writeInt(size);

            // need to skip 2 flag bytes
            file.skipBytes(2);
        }
    }
}