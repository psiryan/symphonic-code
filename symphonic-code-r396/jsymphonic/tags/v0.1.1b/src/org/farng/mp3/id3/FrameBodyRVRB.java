package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectNumberFixedLength;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FrameBodyRVRB extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyRVRB object.
     */
    public FrameBodyRVRB() {
        super();
    }

    /**
     * Creates a new FrameBodyRVRB object.
     */
    public FrameBodyRVRB(final FrameBodyRVRB body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyRVRB object.
     */
    public FrameBodyRVRB(final short reverbLeft,
                         final short reverbRight,
                         final byte reverbBouncesLeft,
                         final byte reverbBouncesRight,
                         final byte reverbFeedbackLeftToLeft,
                         final byte reverbFeedbackLeftToRight,
                         final byte reverbFeedbackRightToRight,
                         final byte reverbFeedbackRightToLeft,
                         final byte premixLeftToRight,
                         final byte premixRightToLeft) {
        setObject("Reverb Left", new Short(reverbLeft));
        setObject("Reverb Right", new Short(reverbRight));
        setObject("Reverb Bounces Left", new Byte(reverbBouncesLeft));
        setObject("Reverb Bounces Right", new Byte(reverbBouncesRight));
        setObject("Reverb Feedback Left To Left", new Byte(reverbFeedbackLeftToLeft));
        setObject("Reverb Feedback Left To Right", new Byte(reverbFeedbackLeftToRight));
        setObject("Reverb Feedback Right To Right", new Byte(reverbFeedbackRightToRight));
        setObject("Reverb Feedback Right to Left", new Byte(reverbFeedbackRightToLeft));
        setObject("Premix Left To Right", new Byte(premixLeftToRight));
        setObject("Premix Right To Left", new Byte(premixRightToLeft));
    }

    /**
     * Creates a new FrameBodyRVRB object.
     */
    public FrameBodyRVRB(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        return "RVRB";
    }

    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberFixedLength("Reverb Left", 2));
        appendToObjectList(new ObjectNumberFixedLength("Reverb Right", 2));
        appendToObjectList(new ObjectNumberFixedLength("Reverb Bounces Left", 1));
        appendToObjectList(new ObjectNumberFixedLength("Reverb Bounces Right", 1));
        appendToObjectList(new ObjectNumberFixedLength("Reverb Feedback Left To Left", 1));
        appendToObjectList(new ObjectNumberFixedLength("Reverb Feedback Left To Right", 1));
        appendToObjectList(new ObjectNumberFixedLength("Reverb Feedback Right To Right", 1));
        appendToObjectList(new ObjectNumberFixedLength("Reverb Feedback Right to Left", 1));
        appendToObjectList(new ObjectNumberFixedLength("Premix Left To Right", 1));
        appendToObjectList(new ObjectNumberFixedLength("Premix Right To Left", 1));
    }
}