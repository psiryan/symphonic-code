package org.farng.mp3.filename;

/**
 * This class is a delimiter which remains a part of the token, located at the end.
 *
 * @author Eric Farng
 * @version $Revision: 1.1 $
 */
public class FilenameEndWordDelimiter extends FilenameDelimiter {

    /**
     * Creates a new FilenameEndWordDelimiter object.
     */
    public FilenameEndWordDelimiter() {
        super();
    }

    /**
     * Creates a new FilenameEndWordDelimiter object.
     */
    public FilenameEndWordDelimiter(final FilenameEndWordDelimiter delimiter) {
        super(delimiter);
    }

    /**
     * Reconstruct the filename that is represented by this composite.
     *
     * @return the filename that is represented by this composite.
     */
    public String composeFilename() {
        final StringBuilder builder = new StringBuilder(128);
        if (getBeforeComposite() != null) {
            builder.append(getBeforeComposite().composeFilename());
            builder.append(' ');
        }
        if (getAfterComposite() != null) {
            builder.append(getAfterComposite().composeFilename());
        }
        return builder.toString().trim();
    }
}