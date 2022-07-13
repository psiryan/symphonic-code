package org.farng.mp3.lyrics3;

import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.InvalidTagException;
import org.farng.mp3.TagException;
import org.farng.mp3.TagNotFoundException;
import org.farng.mp3.TagOptionSingleton;
import org.farng.mp3.id3.AbstractID3v2Frame;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v2_4;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Title: Lyrics3v2 Description: This class is a Lyrics3 2.00 tag Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class Lyrics3v2 extends AbstractLyrics3 {

    /**
     *
     */
    private Map fieldMap = new HashMap(8);

    /**
     * Creates a new Lyrics3v2 object.
     */
    public Lyrics3v2() {
        super();
    }

    /**
     * Creates a new Lyrics3v2 object.
     */
    public Lyrics3v2(final Lyrics3v2 copyObject) {
        super(copyObject);
        final Iterator iterator = copyObject.fieldMap.keySet().iterator();
        String oldIdentifier;
        String newIdentifier;
        Lyrics3v2Field newObject;
        while (iterator.hasNext()) {
            oldIdentifier = iterator.next().toString();
            newIdentifier = oldIdentifier;
            newObject = new Lyrics3v2Field((Lyrics3v2Field) copyObject.fieldMap.get(newIdentifier));
            fieldMap.put(newIdentifier, newObject);
        }
    }

    /**
     * Creates a new Lyrics3v2 object.
     */
    public Lyrics3v2(final AbstractMP3Tag mp3tag) {
        super();
        if (mp3tag != null) {
            // upgrade the tag to lyrics3v2
            if (mp3tag instanceof Lyrics3v2) {
                throw new UnsupportedOperationException("Copy Constructor not called. Please type cast the argument");
            } else if (mp3tag instanceof Lyrics3v1) {
                final Lyrics3v1 lyricOld = (Lyrics3v1) mp3tag;
                final Lyrics3v2Field newField = new Lyrics3v2Field(new FieldBodyLYR(lyricOld.getLyric()));
                fieldMap.put(newField.getIdentifier(), newField);
            } else {
                Lyrics3v2Field newField;
                final Iterator iterator;
                iterator = (new ID3v2_4(mp3tag)).iterator();
                while (iterator.hasNext()) {
                    try {
                        newField = new Lyrics3v2Field((AbstractID3v2Frame) iterator.next());
                        this.fieldMap.put(newField.getIdentifier(), newField);
                    } catch (TagException ex) {
                        //invalid frame to create lyrics3 field. ignore and
                        // keep going
                    }
                }
            }
        }
    }

    /**
     * Creates a new Lyrics3v2 object.
     */
    public Lyrics3v2(final RandomAccessFile file) throws TagNotFoundException, IOException {
        this.read(file);
    }

    /**
     * @param field
     */
    public void setField(final Lyrics3v2Field field) {
        this.fieldMap.put(field.getIdentifier(), field);
    }

    /**
     * Gets the value of the frame identified by identifier
     *
     * @param identifier The three letter code
     *
     * @return The value associated with the identifier
     */
    public Lyrics3v2Field getField(final String identifier) {
        return (Lyrics3v2Field) this.fieldMap.get(identifier);
    }

    public int getFieldCount() {
        return this.fieldMap.size();
    }

    public String getIdentifier() {
        return "Lyrics3v2.00";
    }

    public int getSize() {
        int size = 0;
        final Iterator iterator = this.fieldMap.values().iterator();
        Lyrics3v2Field field;
        while (iterator.hasNext()) {
            field = (Lyrics3v2Field) iterator.next();
            size += field.getSize();
        }

        // include LYRICSBEGIN, but not 6 char size or LYRICSEND
        return 11 + size;
    }

    /**
     * @param tag
     */
    public void append(final AbstractMP3Tag tag) {
        final Lyrics3v2 oldTag = this;
        final Lyrics3v2 newTag;
        if (tag != null) {
            if (tag instanceof Lyrics3v2) {
                newTag = (Lyrics3v2) tag;
            } else {
                newTag = new Lyrics3v2(tag);
            }
            Iterator iterator = newTag.fieldMap.values().iterator();
            Lyrics3v2Field field;
            AbstractLyrics3v2FieldBody body;
            while (iterator.hasNext()) {
                field = (Lyrics3v2Field) iterator.next();
                if (oldTag.hasField(field.getIdentifier()) == false) {
                    oldTag.setField(field);
                } else {
                    body = (AbstractLyrics3v2FieldBody) oldTag.getField(field.getIdentifier()).getBody();
                    final boolean save = TagOptionSingleton.getInstance().getLyrics3SaveField(field.getIdentifier());
                    if ((body.getSize() == 0) && save) {
                        oldTag.setField(field);
                    }
                }
            }

            // reset tag options to save all current fields.
            iterator = oldTag.fieldMap.keySet().iterator();
            String id;
            while (iterator.hasNext()) {
                id = (String) iterator.next();
                TagOptionSingleton.getInstance().setLyrics3SaveField(id, true);
            }
        }
    }

    public boolean equals(final Object obj) {
        if ((obj instanceof Lyrics3v2) == false) {
            return false;
        }
        final Lyrics3v2 lyrics3v2 = (Lyrics3v2) obj;
        if (this.fieldMap.equals(lyrics3v2.fieldMap) == false) {
            return false;
        }
        return super.equals(obj);
    }

    public boolean hasField(final String identifier) {
        return this.fieldMap.containsKey(identifier);
    }

    public Iterator iterator() {
        return this.fieldMap.values().iterator();
    }

    /**
     * @param tag
     */
    public void overwrite(final AbstractMP3Tag tag) {
        final Lyrics3v2 oldTag = this;
        final Lyrics3v2 newTag;
        if (tag != null) {
            if (tag instanceof Lyrics3v2) {
                newTag = (Lyrics3v2) tag;
            } else {
                newTag = new Lyrics3v2(tag);
            }
            Iterator iterator = newTag.fieldMap.values().iterator();
            Lyrics3v2Field field;
            while (iterator.hasNext()) {
                field = (Lyrics3v2Field) iterator.next();
                if (TagOptionSingleton.getInstance().getLyrics3SaveField(field.getIdentifier())) {
                    oldTag.setField(field);
                }
            }

            // reset tag options to save all current fields.
            iterator = oldTag.fieldMap.keySet().iterator();
            String id;
            while (iterator.hasNext()) {
                id = (String) iterator.next();
                TagOptionSingleton.getInstance().setLyrics3SaveField(id, true);
            }
        }
    }

    /**
     * @param file
     *
     * @throws TagNotFoundException
     * @throws IOException
     */
    public void read(final RandomAccessFile file) throws TagNotFoundException, IOException {
        final long filePointer;
        final int lyricSize;
        if (seek(file)) {
            lyricSize = seekSize(file);
        } else {
            throw new TagNotFoundException("Lyrics3v2.00 Tag Not Found");
        }

        // reset file pointer to the beginning of the tag;
        seek(file);
        filePointer = file.getFilePointer();
        this.fieldMap = new HashMap();
        Lyrics3v2Field lyric;

        // read each of the fields
        while ((file.getFilePointer() - filePointer) < (lyricSize - 11)) {
            try {
                lyric = new Lyrics3v2Field(file);
                setField(lyric);
            } catch (InvalidTagException ex) {
                // keep reading until we're done
            }
        }
    }

    /**
     * @param identifier
     */
    public void removeField(final String identifier) {
        this.fieldMap.remove(identifier);
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public boolean seek(final RandomAccessFile file) throws IOException {
        final byte[] buffer = new byte[11];
        String lyricEnd;
        final String lyricStart;
        long filePointer;
        final long lyricSize;

        // check right before the ID3 1.0 tag for the lyrics tag
        file.seek(file.length() - 128 - 9);
        file.read(buffer, 0, 9);
        lyricEnd = new String(buffer, 0, 9);
        if (lyricEnd.equals("LYRICS200")) {
            filePointer = file.getFilePointer();
        } else {
            // check the end of the file for a lyrics tag incase an ID3
            // tag wasn't placed after it.
            file.seek(file.length() - 9);
            file.read(buffer, 0, 9);
            lyricEnd = new String(buffer, 0, 9);
            if (lyricEnd.equals("LYRICS200")) {
                filePointer = file.getFilePointer();
            } else {
                return false;
            }
        }

        // read the 6 bytes for the length of the tag
        filePointer -= (9 + 6);
        file.seek(filePointer);
        file.read(buffer, 0, 6);
        lyricSize = Integer.parseInt(new String(buffer, 0, 6));

        // read the lyrics begin tag if it exists.
        file.seek(filePointer - lyricSize);
        file.read(buffer, 0, 11);
        lyricStart = new String(buffer, 0, 11);
        return lyricStart.equals("LYRICSBEGIN") == true;
    }

    public String toString() {
        final Iterator iterator = this.fieldMap.values().iterator();
        Lyrics3v2Field field;
        String str = getIdentifier() + " " + this.getSize() + "\n";
        while (iterator.hasNext()) {
            field = (Lyrics3v2Field) iterator.next();
            str += (field.toString() + "\n");
        }
        return str;
    }

    /**
     * @param identifier
     */
    public void updateField(final String identifier) {
        Lyrics3v2Field lyrField;
        if (identifier.equals("IND")) {
            final boolean lyricsPresent = this.fieldMap.containsKey("LYR");
            boolean timeStampPresent = false;
            if (lyricsPresent) {
                lyrField = (Lyrics3v2Field) this.fieldMap.get("LYR");
                final FieldBodyLYR lyrBody = (FieldBodyLYR) lyrField.getBody();
                timeStampPresent = lyrBody.hasTimeStamp();
            }
            lyrField = new Lyrics3v2Field(new FieldBodyIND(lyricsPresent, timeStampPresent));
            setField(lyrField);
        }
    }

    /**
     * @param tag
     */
    public void write(final AbstractMP3Tag tag) {
        final Lyrics3v2 oldTag = this;
        final Lyrics3v2 newTag;
        if (tag != null) {
            if (tag instanceof Lyrics3v2) {
                newTag = (Lyrics3v2) tag;
            } else {
                newTag = new Lyrics3v2(tag);
            }
            final Iterator iterator = newTag.fieldMap.values().iterator();
            Lyrics3v2Field field;
            oldTag.fieldMap.clear();
            while (iterator.hasNext()) {
                field = (Lyrics3v2Field) iterator.next();
                oldTag.setField(field);
            }
        }
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public void write(final RandomAccessFile file) throws IOException {
        int offset = 0;
        final long filePointer;
        final byte[] buffer = new byte[6 + 9];
        String str;
        Lyrics3v2Field field;
        final Iterator iterator;
        ID3v1 id3v1tag = new ID3v1();
        id3v1tag = id3v1tag.getID3tag(file);
        delete(file);
        file.seek(file.length());
        filePointer = file.getFilePointer();
        str = "LYRICSBEGIN";
        for (int i = 0; i < str.length(); i++) {
            buffer[i] = (byte) str.charAt(i);
        }
        file.write(buffer, 0, str.length());

        // IND needs to go first. lets create/update it and write it first.
        updateField("IND");
        field = (Lyrics3v2Field) this.fieldMap.get("IND");
        field.write(file);
        iterator = this.fieldMap.values().iterator();
        while (iterator.hasNext()) {
            field = (Lyrics3v2Field) iterator.next();
            final String id = field.getIdentifier();
            final boolean save = TagOptionSingleton.getInstance().getLyrics3SaveField(id);
            if ((id.equals("IND") == false) && save) {
                field.write(file);
            }
        }
        final long size;
        size = file.getFilePointer() - filePointer;
        str = Long.toString(size);
        for (int i = 0; i < (6 - str.length()); i++) {
            buffer[i] = (byte) '0';
        }
        offset += (6 - str.length());
        for (int i = 0; i < str.length(); i++) {
            buffer[i + offset] = (byte) str.charAt(i);
        }
        offset += str.length();
        str = "LYRICS200";
        for (int i = 0; i < str.length(); i++) {
            buffer[i + offset] = (byte) str.charAt(i);
        }
        offset += str.length();
        file.write(buffer, 0, offset);
        if (id3v1tag != null) {
            id3v1tag.write(file);
        }
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    private int seekSize(final RandomAccessFile file) throws IOException {
        final byte[] buffer = new byte[11];
        String lyricEnd;
        long filePointer;

        // check right before the ID3 1.0 tag for the lyrics tag
        file.seek(file.length() - 128 - 9);
        file.read(buffer, 0, 9);
        lyricEnd = new String(buffer, 0, 9);
        if (lyricEnd.equals("LYRICS200")) {
            filePointer = file.getFilePointer();
        } else {
            // check the end of the file for a lyrics tag incase an ID3
            // tag wasn't placed after it.
            file.seek(file.length() - 9);
            file.read(buffer, 0, 9);
            lyricEnd = new String(buffer, 0, 9);
            if (lyricEnd.equals("LYRICS200")) {
                filePointer = file.getFilePointer();
            } else {
                return -1;
            }
        }

        // read the 6 bytes for the length of the tag
        filePointer -= (9 + 6);
        file.seek(filePointer);
        file.read(buffer, 0, 6);
        return Integer.parseInt(new String(buffer, 0, 6));
    }
}