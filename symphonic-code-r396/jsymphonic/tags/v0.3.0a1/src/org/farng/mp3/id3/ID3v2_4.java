package org.farng.mp3.id3;

import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.InvalidTagException;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagConstant;
import org.farng.mp3.TagException;
import org.farng.mp3.TagNotFoundException;
import org.farng.mp3.filename.FilenameTag;
import org.farng.mp3.lyrics3.AbstractLyrics3;
import org.farng.mp3.lyrics3.Lyrics3v2;
import org.farng.mp3.lyrics3.Lyrics3v2Field;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

/**
 * Title: ID3v2_4 Description: This class represents an ID3v2.40 tag Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.3 $
 */
public class ID3v2_4 extends ID3v2_3 {

    /**
     *
     */
    protected boolean footer = false;
    /**
     *
     */
    protected boolean tagRestriction = false;
    /**
     *
     */
    protected boolean updateTag = false;
    /**
     *
     */
    protected byte imageEncodingRestriction = 0;
    /**
     *
     */
    protected byte imageSizeRestriction = 0;
    /**
     *
     */
    protected byte tagSizeRestriction = 0;
    /**
     *
     */
    protected byte textEncodingRestriction = 0;
    /**
     *
     */
    protected byte textFieldSizeRestriction = 0;

    /**
     * Creates a new ID3v2_4 object.
     */
    public ID3v2_4() {
        setMajorVersion((byte) 2);
        setRevision((byte) 4);
    }

    /**
     * Creates a new ID3v2_4 object.
     */
    public ID3v2_4(final ID3v2_4 copyObject) {
        super(copyObject);
        this.footer = copyObject.footer;
        this.tagRestriction = copyObject.tagRestriction;
        this.updateTag = copyObject.updateTag;
        this.imageEncodingRestriction = copyObject.imageEncodingRestriction;
        this.imageSizeRestriction = copyObject.imageSizeRestriction;
        this.tagSizeRestriction = copyObject.tagSizeRestriction;
        this.textEncodingRestriction = copyObject.textEncodingRestriction;
        this.textFieldSizeRestriction = copyObject.textFieldSizeRestriction;
    }

    /**
     * Creates a new ID3v2_4 object.
     */
    public ID3v2_4(final AbstractMP3Tag mp3tag) {
        if (mp3tag != null) {
            // if we get a tag, we want to convert to id3v2_4
            // both id3v1 and lyrics3 convert to this type
            // id3v1 needs to convert to id3v2_4 before converting to lyrics3
            if (mp3tag instanceof AbstractID3v2) {
                copyFromID3v2Tag((AbstractID3v2) mp3tag);
            } else if (mp3tag instanceof ID3v1) {
                // convert id3v1 tags.
                final ID3v1 id3tag = (ID3v1) mp3tag;
                ID3v2_4Frame newFrame;
                AbstractID3v2FrameBody newBody;
                if (id3tag.title.length() > 0) {
                    newBody = new FrameBodyTIT2((byte) 0, id3tag.title);
                    newFrame = new ID3v2_4Frame(false, false, false, false, false, false, newBody);
                    this.setFrame(newFrame);
                }
                if (id3tag.artist.length() > 0) {
                    newBody = new FrameBodyTPE1((byte) 0, id3tag.artist);
                    newFrame = new ID3v2_4Frame(false, false, false, false, false, false, newBody);
                    this.setFrame(newFrame);
                }
                if (id3tag.album.length() > 0) {
                    newBody = new FrameBodyTALB((byte) 0, id3tag.album);
                    newFrame = new ID3v2_4Frame(false, false, false, false, false, false, newBody);
                    this.setFrame(newFrame);
                }
                if (id3tag.year.length() > 0) {
                    newBody = new FrameBodyTDRC((byte) 0, id3tag.year);
                    newFrame = new ID3v2_4Frame(false, false, false, false, false, false, newBody);
                    this.setFrame(newFrame);
                }
                if (id3tag.comment.length() > 0) {
                    newBody = new FrameBodyCOMM((byte) 0, "ENG", "", id3tag.comment);
                    newFrame = new ID3v2_4Frame(false, false, false, false, false, false, newBody);
                    this.setFrame(newFrame);
                }
                if (id3tag.genre >= 0) {
                    final String genre = "(" +
                                         Byte.toString(id3tag.genre) +
                                         ") " +
                                         TagConstant.genreIdToString.get(new Long(id3tag.genre));
                    newBody = new FrameBodyTCON((byte) 0, genre);
                    newFrame = new ID3v2_4Frame(false, false, false, false, false, false, newBody);
                    this.setFrame(newFrame);
                }
                if (mp3tag instanceof ID3v1_1) {
                    final ID3v1_1 id3tag2 = (ID3v1_1) mp3tag;
                    if (id3tag2.track > 0) {
                        newBody = new FrameBodyTRCK((byte) 0, Byte.toString(id3tag2.track));
                        newFrame = new ID3v2_4Frame(false, false, false, false, false, false, newBody);
                        this.setFrame(newFrame);
                    }
                }
            } else if (mp3tag instanceof AbstractLyrics3) {
                // put the conversion stuff in the individual frame code.
                final Lyrics3v2 lyric;
                if (mp3tag instanceof Lyrics3v2) {
                    lyric = new Lyrics3v2((Lyrics3v2) mp3tag);
                } else {
                    lyric = new Lyrics3v2(mp3tag);
                }
                final Iterator iterator = lyric.iterator();
                Lyrics3v2Field field;
                ID3v2_4Frame newFrame;
                while (iterator.hasNext()) {
                    try {
                        field = (Lyrics3v2Field) iterator.next();
                        newFrame = new ID3v2_4Frame(field);
                        this.setFrame(newFrame);
                    } catch (InvalidTagException ex) {
                    }
                }
            } else if (mp3tag instanceof FilenameTag) {
                copyFromID3v2Tag(((FilenameTag) mp3tag).getId3tag());
            }
        }
    }

    /**
     * Creates a new ID3v2_4 object.
     */
    public ID3v2_4(final RandomAccessFile file) throws TagException, IOException {
        this.read(file);
    }

    public String getIdentifier() {
        return "ID3v2.40";
    }

    public int getSize() {
        int size = 3 + 2 + 1 + 4;
        if (this.extended) {
            size += (4 + 1 + 1);
            if (this.updateTag) {
                size++;
            }
            if (this.crcDataFlag) {
                size += 5;
            }
            if (this.tagRestriction) {
                size += 2;
            }
        }
        final Iterator iterator = this.getFrameIterator();
        AbstractID3v2Frame frame;
        while (iterator.hasNext()) {
            frame = (AbstractID3v2Frame) iterator.next();
            size += frame.getSize();
        }
        return size;
    }

    /**
     * @param tag
     */
    public void append(final AbstractMP3Tag tag) {
        if (tag instanceof ID3v2_4) {
            this.updateTag = ((ID3v2_4) tag).updateTag;
            this.footer = ((ID3v2_4) tag).footer;
            this.tagRestriction = ((ID3v2_4) tag).tagRestriction;
            this.tagSizeRestriction = ((ID3v2_4) tag).tagSizeRestriction;
            this.textEncodingRestriction = ((ID3v2_4) tag).textEncodingRestriction;
            this.textFieldSizeRestriction = ((ID3v2_4) tag).textFieldSizeRestriction;
            this.imageEncodingRestriction = ((ID3v2_4) tag).imageEncodingRestriction;
            this.imageSizeRestriction = ((ID3v2_4) tag).imageSizeRestriction;
        }
        super.append(tag);
    }

    public boolean equals(final Object obj) {
        if ((obj instanceof ID3v2_4) == false) {
            return false;
        }
        final ID3v2_4 id3v2_4 = (ID3v2_4) obj;
        if (this.footer != id3v2_4.footer) {
            return false;
        }
        if (this.imageEncodingRestriction != id3v2_4.imageEncodingRestriction) {
            return false;
        }
        if (this.imageSizeRestriction != id3v2_4.imageSizeRestriction) {
            return false;
        }
        if (this.tagRestriction != id3v2_4.tagRestriction) {
            return false;
        }
        if (this.tagSizeRestriction != id3v2_4.tagSizeRestriction) {
            return false;
        }
        if (this.textEncodingRestriction != id3v2_4.textEncodingRestriction) {
            return false;
        }
        if (this.textFieldSizeRestriction != id3v2_4.textFieldSizeRestriction) {
            return false;
        }
        if (this.updateTag != id3v2_4.updateTag) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * @param tag
     */
    public void overwrite(final AbstractMP3Tag tag) {
        if (tag instanceof ID3v2_4) {
            this.updateTag = ((ID3v2_4) tag).updateTag;
            this.footer = ((ID3v2_4) tag).footer;
            this.tagRestriction = ((ID3v2_4) tag).tagRestriction;
            this.tagSizeRestriction = ((ID3v2_4) tag).tagSizeRestriction;
            this.textEncodingRestriction = ((ID3v2_4) tag).textEncodingRestriction;
            this.textFieldSizeRestriction = ((ID3v2_4) tag).textFieldSizeRestriction;
            this.imageEncodingRestriction = ((ID3v2_4) tag).imageEncodingRestriction;
            this.imageSizeRestriction = ((ID3v2_4) tag).imageSizeRestriction;
        }
        super.overwrite(tag);
    }

    /**
     * @param file
     *
     * @throws TagException
     * @throws IOException
     * @throws TagNotFoundException
     * @throws InvalidTagException
     */
    public void read(final RandomAccessFile file) throws TagException, IOException {
        final int size;
        byte[] buffer = new byte[4];
        file.seek(0);
        if (seek(file) == false) {
            throw new TagNotFoundException(getIdentifier() + " tag not found");
        }

        // read the major and minor @version bytes & flag bytes
        file.read(buffer, 0, 3);
        if ((buffer[0] != 4) || (buffer[1] != 0)) {
            throw new TagNotFoundException(getIdentifier() + " tag not found");
        }
        setMajorVersion(buffer[0]);
        setRevision(buffer[1]);
        this.unsynchronization = (buffer[2] & TagConstant.MASK_V24_UNSYNCHRONIZATION) != 0;
        this.extended = (buffer[2] & TagConstant.MASK_V24_EXTENDED_HEADER) != 0;
        this.experimental = (buffer[2] & TagConstant.MASK_V24_EXPERIMENTAL) != 0;
        this.footer = (buffer[2] & TagConstant.MASK_V24_FOOTER_PRESENT) != 0;

        // read the size
        file.read(buffer, 0, 4);
        size = byteArrayToSize(buffer);
        final long filePointer = file.getFilePointer();
        if (this.extended) {
            // int is 4 bytes.
            final int extendedHeaderSize = file.readInt();

            // the extended header must be atleast 6 bytes
            if (extendedHeaderSize <= 6) {
                throw new InvalidTagException("Invalid Extended Header Size.");
            }
            final byte numberOfFlagBytes = file.readByte();

            // read the flag bytes
            file.read(buffer, 0, numberOfFlagBytes);
            this.updateTag = (buffer[0] & TagConstant.MASK_V24_TAG_UPDATE) != 0;
            this.crcDataFlag = (buffer[0] & TagConstant.MASK_V24_CRC_DATA_PRESENT) != 0;
            this.tagRestriction = (buffer[0] & TagConstant.MASK_V24_TAG_RESTRICTIONS) != 0;

            // read the length byte if the flag is set
            // this tag should always be zero but just in case
            // read this information.
            if (this.updateTag) {
                final int len = file.readByte();
                buffer = new byte[len];
                file.read(buffer, 0, len);
            }
            if (this.crcDataFlag) {
                // the CRC has a variable length
                final int len = file.readByte();
                buffer = new byte[len];
                file.read(buffer, 0, len);
                this.crcData = 0;
                for (int i = 0; i < len; i++) {
                    this.crcData <<= 8;
                    this.crcData += buffer[i];
                }
            }
            if (this.tagRestriction) {
                final int len = file.readByte();
                buffer = new byte[len];
                file.read(buffer, 0, len);
                this.tagSizeRestriction = (byte) ((buffer[0] & TagConstant.MASK_V24_TAG_SIZE_RESTRICTIONS) >> 6);
                this.textEncodingRestriction = (byte) ((buffer[0] & TagConstant.MASK_V24_TEXT_ENCODING_RESTRICTIONS) >>
                                                       5);
                this.textFieldSizeRestriction = (byte) ((buffer[0] & TagConstant
                        .MASK_V24_TEXT_FIELD_SIZE_RESTRICTIONS) >> 3);
                this.imageEncodingRestriction = (byte) ((buffer[0] & TagConstant.MASK_V24_IMAGE_ENCODING) >> 2);
                this.imageSizeRestriction = (byte) (buffer[0] & TagConstant.MASK_V24_IMAGE_SIZE_RESTRICTIONS);
            }
        }
        ID3v2_4Frame next;
        this.clearFrameMap();

        // read the frames
        this.setFileReadBytes(size);
        resetPaddingCounter();
        while ((file.getFilePointer() - filePointer) <= size) {
            try {
                next = new ID3v2_4Frame(file);
                final String id = next.getIdentifier();
                if (this.hasFrame(id)) {
                    this.appendDuplicateFrameId(id + "; ");
                    this.incrementDuplicateBytes(this.getFrame(id).getSize());
                }
                this.setFrame(next);
            } catch (InvalidTagException ex) {
                if (ex.getMessage().equals("Found empty frame")) {
                    this.incrementEmptyFrameBytes(10);
                } else {
                    this.incrementInvalidFrameBytes();
                }
            }
        }
        this.setPaddingSize(getPaddingCounter());

        /**
         * int newSize = this.getSize(); if ((this.padding + newSize - 10) !=
         * size) { System.out.println("WARNING: Tag sizes don't add up");
         * System.out.println("ID3v2.40 tag size : " + newSize);
         * System.out.println("ID3v2.40 padding : " + this.padding);
         * System.out.println("ID3v2.40 total : " + (this.padding + newSize));
         * System.out.println("ID3v2.40 file size: " + size); }
         */
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public boolean seek(final RandomAccessFile file) throws IOException {
        final byte[] buffer = new byte[3];
        file.seek(0);

        // read the tag if it exists
        file.read(buffer, 0, 3);
        final String tag = new String(buffer, 0, 3);
        if (tag.equals("ID3") == false) {
            return false;
        }

        // read the major and minor @version number
        file.read(buffer, 0, 2);

        // read back the @version bytes so we can read and save them later
        file.seek(file.getFilePointer() - 2);
        return ((buffer[0] == 4) && (buffer[1] == 0));
    }

    public String toString() {
        final Iterator iterator = this.getFrameIterator();
        AbstractID3v2Frame frame;
        String str = getIdentifier() + " " + this.getSize() + "\n";
        str += ("compression              = " + this.compression + "\n");
        str += ("unsynchronization        = " + this.unsynchronization + "\n");
        str += ("crcData                  = " + this.crcData + "\n");
        str += ("crcDataFlag              = " + this.crcDataFlag + "\n");
        str += ("experimental             = " + this.experimental + "\n");
        str += ("extended                 = " + this.extended + "\n");
        str += ("paddingSize              = " + this.paddingSize + "\n");
        str += ("footer                   = " + this.footer + "\n");
        str += ("imageEncodingRestriction = " + this.imageEncodingRestriction + "\n");
        str += ("imageSizeRestriction     = " + this.imageSizeRestriction + "\n");
        str += ("tagRestriction           = " + this.tagRestriction + "\n");
        str += ("tagSizeRestriction       = " + this.tagSizeRestriction + "\n");
        str += ("textEncodingRestriction  = " + this.textEncodingRestriction + "\n");
        str += ("textFieldSizeRestriction = " + this.textFieldSizeRestriction + "\n");
        str += ("updateTag                = " + this.updateTag + "\n");
        while (iterator.hasNext()) {
            frame = (ID3v2_4Frame) iterator.next();
            str += (frame.toString() + "\n");
        }
        return str + "\n";
    }

    /**
     * @param tag
     */
    public void write(final AbstractMP3Tag tag) {
        if (tag instanceof ID3v2_4) {
            this.updateTag = ((ID3v2_4) tag).updateTag;
            this.footer = ((ID3v2_4) tag).footer;
            this.tagRestriction = ((ID3v2_4) tag).tagRestriction;
            this.tagSizeRestriction = ((ID3v2_4) tag).tagSizeRestriction;
            this.textEncodingRestriction = ((ID3v2_4) tag).textEncodingRestriction;
            this.textFieldSizeRestriction = ((ID3v2_4) tag).textFieldSizeRestriction;
            this.imageEncodingRestriction = ((ID3v2_4) tag).imageEncodingRestriction;
            this.imageSizeRestriction = ((ID3v2_4) tag).imageSizeRestriction;
        }
        super.write(tag);
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public void write(final RandomAccessFile file) throws IOException {
        int size;
        final String str;
        final Iterator iterator;
        ID3v2_4Frame frame;
        final byte[] buffer = new byte[6];
        final MP3File mp3 = new MP3File();
        mp3.seekMP3Frame(file);
        final long mp3start = file.getFilePointer();
        file.seek(0);
        str = "ID3";
        for (int i = 0; i < str.length(); i++) {
            buffer[i] = (byte) str.charAt(i);
        }
        buffer[3] = 4;
        buffer[4] = 0;
        if (this.unsynchronization) {
            buffer[5] |= TagConstant.MASK_V24_UNSYNCHRONIZATION;
        }
        if (this.extended) {
            buffer[5] |= TagConstant.MASK_V24_EXTENDED_HEADER;
        }
        if (this.experimental) {
            buffer[5] |= TagConstant.MASK_V24_EXPERIMENTAL;
        }
        if (this.footer) {
            buffer[5] |= TagConstant.MASK_V24_FOOTER_PRESENT;
        }
        file.write(buffer);

        // write size
        file.write(sizeToByteArray((int) mp3start - 10));
        if (this.extended) {
            size = 6;
            if (this.updateTag) {
                size++;
            }
            if (this.crcDataFlag) {
                size += 5;
            }
            if (this.tagRestriction) {
                size += 2;
            }
            file.writeInt(size);
            file.writeByte(1); // always 1 byte of flags in this tag
            buffer[0] = 0;
            if (this.updateTag) {
                buffer[0] |= TagConstant.MASK_V24_TAG_UPDATE;
            }
            if (this.crcDataFlag) {
                buffer[0] |= TagConstant.MASK_V24_CRC_DATA_PRESENT;
            }
            if (this.tagRestriction) {
                buffer[0] |= TagConstant.MASK_V24_TAG_RESTRICTIONS;
            }
            file.writeByte(buffer[0]);
            if (this.updateTag) {
                file.writeByte(0);
            }

            // this can be variable length, but this is easier
            if (this.crcDataFlag) {
                file.writeByte(4);
                file.writeInt(this.crcData);
            }
            if (this.tagRestriction) {
                // todo we need to finish this
                file.writeByte(1);
                buffer[0] = (byte) 0;
                if (this.tagRestriction) {
                    buffer[0] |= TagConstant.MASK_V24_TAG_SIZE_RESTRICTIONS;
                }
                file.writeByte(this.tagSizeRestriction);
                file.writeByte(this.textEncodingRestriction);
                file.writeByte(this.textFieldSizeRestriction);
                file.writeByte(this.imageEncodingRestriction);
                file.writeByte(this.imageSizeRestriction);
                file.writeByte(buffer[0]);
            }
        }

        // write all frames
        iterator = this.getFrameIterator();
        while (iterator.hasNext()) {
            frame = (ID3v2_4Frame) iterator.next();
            frame.write(file);
        }
    }

    /**
     * @param mp3tag
     */
    private void copyFromID3v2Tag(final AbstractID3v2 mp3tag) {
        // if the tag is id3v2_4
        if (mp3tag instanceof ID3v2_4) {
            final ID3v2_4 tag = (ID3v2_4) mp3tag;
            this.footer = tag.footer;
            this.tagRestriction = tag.tagRestriction;
            this.updateTag = tag.updateTag;
            this.imageEncodingRestriction = tag.imageEncodingRestriction;
            this.imageSizeRestriction = tag.imageSizeRestriction;
            this.tagSizeRestriction = tag.tagSizeRestriction;
            this.textEncodingRestriction = tag.textEncodingRestriction;
            this.textFieldSizeRestriction = tag.textFieldSizeRestriction;
        }
        if (mp3tag instanceof ID3v2_3) {
            // and id3v2_4 tag is an instance of id3v2_3 also ...
            final ID3v2_3 id3tag = (ID3v2_3) mp3tag;
            this.extended = id3tag.extended;
            this.experimental = id3tag.experimental;
            this.crcDataFlag = id3tag.crcDataFlag;
            this.crcData = id3tag.crcData;
            this.paddingSize = id3tag.paddingSize;
        }
        if (mp3tag instanceof ID3v2_2) {
            final ID3v2_2 id3tag = (ID3v2_2) mp3tag;
            this.compression = id3tag.compression;
            this.unsynchronization = id3tag.unsynchronization;
        }
        final AbstractID3v2 id3tag = mp3tag;
        final Iterator iterator = id3tag.getFrameIterator();
        AbstractID3v2Frame frame;
        ID3v2_4Frame newFrame;
        while (iterator.hasNext()) {
            frame = (AbstractID3v2Frame) iterator.next();
            newFrame = new ID3v2_4Frame(frame);
            this.setFrame(newFrame);
        }
    }
}