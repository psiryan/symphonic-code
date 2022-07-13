package org.farng.mp3.filename;

import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagConstant;
import org.farng.mp3.TagException;
import org.farng.mp3.TagUtility;
import org.farng.mp3.id3.AbstractID3v2Frame;
import org.farng.mp3.id3.ID3v2_4;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

/**
 * This class represents the filename. To create it, invoke <code>FilenameTagBuilder.createFilenameTagFromMP3File</code>
 * which returns a complete parsed, evaluated, and matched FilenameTag.
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FilenameTag extends AbstractMP3Tag {

    /**
     * parsed composite
     */
    private AbstractFilenameComposite composite = null;
    /**
     * id3v2_4 tag created from composite
     */
    private ID3v2_4 id3tag = null;
    /**
     * mp3file used to create this tag
     */
    private MP3File mp3file = null;
    /**
     *
     */
    private String extension = null;

    /**
     * Creates a new FilenameTag object.
     */
    public FilenameTag(final FilenameTag copyObject) {
        super(copyObject);
        composite = (AbstractFilenameComposite) TagUtility.copyObject(copyObject.composite);
        id3tag = new ID3v2_4(copyObject.id3tag);
        mp3file = new MP3File(copyObject.mp3file);
        extension = copyObject.extension;
    }

    /**
     * Creates a new FilenameTag object.
     */
    protected FilenameTag() {
        super();
    }

    /**
     * Sets the composite that this tag will use.
     *
     * @param composite the composite that this tag will use.
     */
    public void setComposite(final AbstractFilenameComposite composite) {
        this.composite = composite;
    }

    /**
     * Returns the composite that this tag will use.
     *
     * @return the composite that this tag will use.
     */
    public AbstractFilenameComposite getComposite() {
        return composite;
    }

    /**
     * @param extension
     */
    public void setExtension(final String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    /**
     * Sets the frame of this tag
     *
     * @param frame the frame to set
     */
    public void setFrame(final AbstractID3v2Frame frame) {
        if (frame != null) {
            if (id3tag == null) {
                id3tag = new ID3v2_4();
            }
            id3tag.setFrame(frame);
            if (composite != null) {
                composite.setFrame(frame);
            }
        }
    }

    /**
     * Returns a frame of this tag
     *
     * @param identifier ID3v2_4 ID of frame to get
     *
     * @return a frame of this tag
     */
    public AbstractID3v2Frame getFrame(final String identifier) {
        AbstractID3v2Frame frame = null;
        if (id3tag != null) {
            frame = id3tag.getFrame(identifier);
        }
        return frame;
    }

    public Iterator getFrameOfType(final String identifier) {
        return id3tag.getFrameOfType(identifier);
    }

    /**
     * Sets the ID3v2_4 representation of this tag.
     *
     * @param id3tag the ID3v2_4 representation of this tag
     */
    public void setId3tag(final ID3v2_4 id3tag) {
        this.id3tag = id3tag;
        if (id3tag != null) {
            final Iterator iterator = id3tag.iterator();
            while (iterator.hasNext()) {
                composite.setFrame((AbstractID3v2Frame) iterator.next());
            }
            if (composite != null) {
                composite.matchAgainstTag(id3tag);
            }
        }
    }

    /**
     * Returns the ID3v2_4 representation of this tag
     *
     * @return the ID3v2_4 representation of this tag
     */
    public ID3v2_4 getId3tag() {
        return id3tag;
    }

    public String getIdentifier() {
        return "FilenameTagv1.00";
    }

    /**
     * @param mp3file
     */
    public void setMp3file(final MP3File mp3file) {
        this.mp3file = mp3file;
    }

    public MP3File getMp3file() {
        return mp3file;
    }

    public int getSize() {
        return composeFilename().length();
    }

    /**
     * @param abstractMP3Tag
     *
     * @throws UnsupportedOperationException
     */
    public void append(final AbstractMP3Tag abstractMP3Tag) {
        //todo Implement this org.farng.mp3.AbstractMP3Tag abstract method
        throw new UnsupportedOperationException("Method append() not yet implemented.");
    }

    /**
     * @param file
     *
     * @throws UnsupportedOperationException
     */
    public void append(final RandomAccessFile file) {
        //todo Implement this org.farng.mp3.AbstractMP3Tag abstract method
        throw new UnsupportedOperationException("Method append() not yet implemented.");
    }

    public String composeFilename() {
        final StringBuilder filename = new StringBuilder(128);
        if (composite != null) {
            filename.append(composite.composeFilename().trim());
            filename.append('.');
            filename.append(extension);
        }
        return filename.toString();
    }

    /**
     * @param file
     *
     * @throws UnsupportedOperationException
     */
    public void delete(final RandomAccessFile file) {
        //todo Implement this org.farng.mp3.AbstractMP3Tag abstract method
        throw new UnsupportedOperationException("Method delete() not yet implemented.");
    }

    public boolean hasFrame(final String identifier) {
        if (id3tag != null) {
            return id3tag.hasFrame(identifier);
        }
        return false;
    }

    public boolean hasFrameOfType(final String identifier) {
        if (id3tag != null) {
            return id3tag.hasFrameOfType(identifier);
        }
        return false;
    }

    public Iterator iterator() {
        Iterator iterator = null;
        if (composite != null) {
            iterator = composite.iterator();
        }
        return iterator;
    }

    /**
     * @param abstractMP3Tag
     *
     * @throws UnsupportedOperationException
     */
    public void overwrite(final AbstractMP3Tag abstractMP3Tag) {
        //todo Implement this org.farng.mp3.AbstractMP3Tag abstract method
        throw new UnsupportedOperationException("Method overwrite() not yet implemented.");
    }

    /**
     * @param file
     *
     * @throws TagException
     * @throws IOException
     */
    public void overwrite(final RandomAccessFile file) throws TagException, IOException {
        write(file);
    }

    /**
     * @param file
     *
     * @throws UnsupportedOperationException
     */
    public void read(final RandomAccessFile file) {
        //todo Implement this org.farng.mp3.AbstractMP3Tag abstract method
        throw new UnsupportedOperationException("Method read() not yet implemented.");
    }

    /**
     * @param file
     *
     * @throws UnsupportedOperationException
     */
    public boolean seek(final RandomAccessFile file) {
        //todo Implement this org.farng.mp3.AbstractMP3Tag abstract method
        throw new UnsupportedOperationException("Method seek() not yet implemented.");
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder(128);
        final Iterator iterator = iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next().toString());
            builder.append(TagConstant.SEPERATOR_LINE);
        }
        return builder.toString();
    }

    /**
     * @param abstractMP3Tag
     *
     * @throws UnsupportedOperationException
     */
    public void write(final AbstractMP3Tag abstractMP3Tag) {
        //todo Implement this org.farng.mp3.AbstractMP3Tag abstract method
        throw new UnsupportedOperationException("Method write() not yet implemented.");
    }

    /**
     * @param file
     *
     * @throws IOException
     * @throws TagException
     */
    public void write(final RandomAccessFile file) throws IOException, TagException {
        final File originalFile = getMp3file().getMp3file();
        final File newFile = new File(originalFile.getParentFile(), composeFilename());
        if (!newFile.getName().equals(originalFile.getName())) {
            file.getFD().sync();
            file.getChannel().close();
            file.close();

            // copy, then delete
            TagUtility.copyFile(originalFile, newFile);
            if (!originalFile.delete()) {
                throw new TagException("Unable to delete original file: " + originalFile.getName());
            }
        }
    }
}