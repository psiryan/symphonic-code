package org.farng.mp3.object;

/**
 * <p/>
 * Title: </p>
 * <p/>
 * <p/>
 * Description: </p>
 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2002 </p>
 * <p/>
 * <p/>
 * Company: </p>
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class ObjectStringDateTime extends ObjectStringSizeTerminated {

    /**
     * Creates a new ObjectStringDateTime object.
     */
    public ObjectStringDateTime(final String identifier) {
        super(identifier);
    }

    /**
     * Creates a new ObjectStringDateTime object.
     */
    public ObjectStringDateTime(final ObjectStringDateTime object) {
        super(object);
    }

    /**
     * @param value
     */
    public void setValue(final Object value) {
        if (value != null) {
            this.value = value.toString().replace(' ', 'T');
        }
    }

    public Object getValue() {
        if (this.value != null) {
            return this.value.toString().replace(' ', 'T');
        }
        return null;
    }

    public boolean equals(final Object obj) {
        if (obj instanceof ObjectStringDateTime == false) {
            return false;
        }
        return super.equals(obj);
    }
}