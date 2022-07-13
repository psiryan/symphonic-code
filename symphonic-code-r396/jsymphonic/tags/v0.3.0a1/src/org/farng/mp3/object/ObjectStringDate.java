package org.farng.mp3.object;

import org.farng.mp3.TagUtility;

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
public class ObjectStringDate extends ObjectStringFixedLength {

    /**
     * Creates a new ObjectStringDate object.
     */
    public ObjectStringDate(final String identifier) {
        super(identifier, 8);
    }

    /**
     * Creates a new ObjectStringDate object.
     */
    public ObjectStringDate(final ObjectStringDate object) {
        super(object);
    }

    /**
     * @param value
     */
    public void setValue(final Object value) {
        if (value != null) {
            this.value = TagUtility.stripChar(value.toString(), '-');
        }
    }

    public Object getValue() {
        if (this.value != null) {
            return TagUtility.stripChar(this.value.toString(), '-');
        }
        return null;
    }

    public boolean equals(final Object obj) {
        if (obj instanceof ObjectStringDate == false) {
            return false;
        }
        return super.equals(obj);
    }
}