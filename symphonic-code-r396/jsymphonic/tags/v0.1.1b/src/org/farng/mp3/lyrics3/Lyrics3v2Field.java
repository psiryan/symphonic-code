package org.farng.mp3.lyrics3;

import org.farng.mp3.AbstractMP3Fragment;
import org.farng.mp3.InvalidTagException;
import org.farng.mp3.TagException;
import org.farng.mp3.TagOptionSingleton;
import org.farng.mp3.TagUtility;
import org.farng.mp3.id3.AbstractFrameBodyTextInformation;
import org.farng.mp3.id3.AbstractID3v2Frame;
import org.farng.mp3.id3.FrameBodyCOMM;
import org.farng.mp3.id3.FrameBodySYLT;
import org.farng.mp3.id3.FrameBodyUSLT;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Lyrics3v2Field Description: This class is an individual field within a Lyrics3 tag Copyright: Copyright (c)
 * 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class Lyrics3v2Field extends AbstractMP3Fragment {

    /**
     * Creates a new Lyrics3v2Field object.
     */
    public Lyrics3v2Field() {
        // base empty constructor
    }

    /**
     * Creates a new Lyrics3v2Field object.
     */
    public Lyrics3v2Field(final Lyrics3v2Field copyObject) {
        super(copyObject);
    }

    /**
     * Creates a new Lyrics3v2Field object.
     */
    public Lyrics3v2Field(final AbstractLyrics3v2FieldBody body) {
        super(body);
    }

    /**
     * Creates a new Lyrics3v2Field object.
     */
    public Lyrics3v2Field(final AbstractID3v2Frame frame) throws TagException {
        final AbstractFrameBodyTextInformation textFrame;
        final String text;
        final String frameIdentifier = frame.getIdentifier();
        if (frameIdentifier.startsWith("USLT")) {
            this.setBody(new FieldBodyLYR(""));
            ((FieldBodyLYR) this.getBody()).addLyric((FrameBodyUSLT) frame.getBody());
        } else if (frameIdentifier.startsWith("SYLT")) {
            this.setBody(new FieldBodyLYR(""));
            ((FieldBodyLYR) this.getBody()).addLyric((FrameBodySYLT) frame.getBody());
        } else if (frameIdentifier.startsWith("COMM")) {
            text = new String(((FrameBodyCOMM) frame.getBody()).getText());
            this.setBody(new FieldBodyINF(text));
        } else if (frameIdentifier.equals("TCOM")) {
            textFrame = (AbstractFrameBodyTextInformation) frame.getBody();
            this.setBody(new FieldBodyAUT(""));
            if ((textFrame != null) && (((textFrame.getText())).length() > 0)) {
                this.setBody(new FieldBodyAUT((textFrame.getText())));
            }
        } else if (frameIdentifier.equals("TALB")) {
            textFrame = (AbstractFrameBodyTextInformation) frame.getBody();
            if ((textFrame != null) && ((textFrame.getText()).length() > 0)) {
                this.setBody(new FieldBodyEAL((textFrame.getText())));
            }
        } else if (frameIdentifier.equals("TPE1")) {
            textFrame = (AbstractFrameBodyTextInformation) frame.getBody();
            if ((textFrame != null) && ((textFrame.getText()).length() > 0)) {
                this.setBody(new FieldBodyEAR((textFrame.getText())));
            }
        } else if (frameIdentifier.equals("TIT2")) {
            textFrame = (AbstractFrameBodyTextInformation) frame.getBody();
            if ((textFrame != null) && ((textFrame.getText()).length() > 0)) {
                this.setBody(new FieldBodyETT((textFrame.getText())));
            }
        } else {
            throw new TagException("Cannot create Lyrics3v2 field from given ID3v2 frame");
        }
    }

    /**
     * Creates a new Lyrics3v2Field object.
     */
    public Lyrics3v2Field(final RandomAccessFile file) throws InvalidTagException, IOException {
        this.read(file);
    }

    public String getIdentifier() {
        if (this.getBody() == null) {
            return "";
        }
        return this.getBody().getIdentifier();
    }

    public int getSize() {
        return this.getBody().getSize() + 5 + getIdentifier().length();
    }

    /**
     * @param file
     *
     * @throws InvalidTagException
     * @throws IOException
     */
    public void read(final RandomAccessFile file) throws InvalidTagException, IOException {
        final byte[] buffer = new byte[6];

        // lets scan for a non-zero byte;
        long filePointer;
        byte b;
        do {
            filePointer = file.getFilePointer();
            b = file.readByte();
        } while (b == 0);
        file.seek(filePointer);

        // read the 3 character ID
        file.read(buffer, 0, 3);
        final String identifier = new String(buffer, 0, 3);

        // is this a valid identifier?
        if (TagUtility.isLyrics3v2FieldIdentifier(identifier) == false) {
            throw new InvalidTagException(identifier + " is not a valid ID3v2.4 frame");
        }
        this.setBody(readBody(identifier, file));
    }

    public String toString() {
        if (this.getBody() == null) {
            return "";
        }
        return this.getBody().toString();
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public void write(final RandomAccessFile file) throws IOException {
        if (((this.getBody()).getSize() > 0) || TagOptionSingleton.getInstance().isLyrics3SaveEmptyField()) {
            final byte[] buffer = new byte[3];
            final String str = getIdentifier();
            for (int i = 0; i < str.length(); i++) {
                buffer[i] = (byte) str.charAt(i);
            }
            file.write(buffer, 0, str.length());
            this.getBody().write(file);
        }
    }

    /**
     * @param identifier
     * @param file
     *
     * @throws InvalidTagException
     * @throws IOException
     */
    private AbstractLyrics3v2FieldBody readBody(final String identifier, final RandomAccessFile file)
            throws InvalidTagException, IOException {
        final AbstractLyrics3v2FieldBody newBody;
        if (identifier.equals("AUT")) {
            newBody = new FieldBodyAUT(file);
        } else if (identifier.equals("EAL")) {
            newBody = new FieldBodyEAL(file);
        } else if (identifier.equals("EAR")) {
            newBody = new FieldBodyEAR(file);
        } else if (identifier.equals("ETT")) {
            newBody = new FieldBodyETT(file);
        } else if (identifier.equals("IMG")) {
            newBody = new FieldBodyIMG(file);
        } else if (identifier.equals("IND")) {
            newBody = new FieldBodyIND(file);
        } else if (identifier.equals("INF")) {
            newBody = new FieldBodyINF(file);
        } else if (identifier.equals("LYR")) {
            newBody = new FieldBodyLYR(file);
        } else {
            newBody = new FieldBodyUnsupported(file);
        }
        return newBody;
    }
}