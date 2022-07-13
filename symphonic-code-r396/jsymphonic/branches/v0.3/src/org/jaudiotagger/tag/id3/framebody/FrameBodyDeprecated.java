package org.jaudiotagger.tag.id3.framebody;

/**
 * Represents a framebody for a frame identifier that is not defined for the tag version but was valid for a for an
 * earlier tag version.
 * The body consists  of an array of bytes representing all the bytes in the body.
 */
public class FrameBodyDeprecated extends AbstractID3v2FrameBody implements ID3v24FrameBody, ID3v23FrameBody
{
    /* The original framebody is held so can be retrieved
     * when converting a DeprecatedFrameBody back to a normal Framebody */
    private AbstractID3v2FrameBody originalFrameBody;


    /**
     * Creates a new FrameBodyDeprecated wrapper around the frameBody
     */
    public FrameBodyDeprecated(AbstractID3v2FrameBody frameBody)
    {
        this.originalFrameBody = frameBody;
    }

    /**
     * Copy constructor
     *
     * @param copyObject a copy is made of this
     */
    public FrameBodyDeprecated(FrameBodyDeprecated copyObject)
    {
        super(copyObject);
    }


    /**
     * Return the frame identifier
     *
     * @return the identifier
     */
    public String getIdentifier()
    {
        return originalFrameBody.getIdentifier();
    }

    /**
     * Delgate size to size of original framebody, if framebody already exist will take this value from the frame header
     * but it is always recalculated before writing any changes back to disk.
     *
     * @return size in bytes of this frame body
     */
    public int getSize()
    {
        return originalFrameBody.getSize();
    }

    /**
     * @param obj
     * @return whether obj is equivalent to this object
     */
    public boolean equals(Object obj)
    {
        if ((obj instanceof FrameBodyDeprecated) == false)
        {
            return false;
        }

        FrameBodyDeprecated object = (FrameBodyDeprecated) obj;
        if (this.getIdentifier().equals(object.getIdentifier()) == false)
        {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Return the original framebody that was used to construct the DeprecatedFrameBody
     *
     * @return the original frameBody
     */
    public AbstractID3v2FrameBody getOriginalFrameBody()
    {
        return originalFrameBody;
    }

    /**
     * Because the contents of this frame are an array of bytes and could be large we just
     * return the identifier.
     *
     * @return a string representation of this frame
     */
    public String toString()
    {
        return getIdentifier();
    }

    /**
     * Setup the Object List.
     * <p/>
     * This is handled by the wrapped class
     */
    protected void setupObjectList()
    {

    }

    public String getBriefDescription()
    {
        //TO DO When is this null, it seems it can be but Im not sure why
        if (originalFrameBody != null)
        {
            return originalFrameBody.getBriefDescription();
        }
        return "";
    }
}
