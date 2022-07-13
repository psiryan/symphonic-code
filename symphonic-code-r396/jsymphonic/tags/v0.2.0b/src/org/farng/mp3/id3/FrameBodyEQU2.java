/**
 * MP3 Tag library. It includes an implementation of the ID3 tags and Lyrics3
 * tags as they are defined at www.id3.org Copyright (C) Eric Farng 2003 This
 * program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to: Free
 * Software Foundation, Inc. 59 Temple Place, Suite 330 Boston, MA 02111-1307
 * USA
 */
/**
 * MP3 Tag library. It includes an implementation of the ID3 tags and Lyrics3
 * tags as they are defined at www.id3.org Copyright (C) Eric Farng 2003 This
 * program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to: Free
 * Software Foundation, Inc. 59 Temple Place, Suite 330 Boston, MA 02111-1307
 * USA
 */

package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.AbstractMP3Object;
import org.farng.mp3.object.ObjectGroupRepeated;
import org.farng.mp3.object.ObjectNumberFixedLength;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringNullTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.3 $
 */
public class FrameBodyEQU2 extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyEQU2 object.
     */
    public FrameBodyEQU2() {
        super();
    }

    /**
     * Creates a new FrameBodyEQU2 object.
     */
    public FrameBodyEQU2(final FrameBodyEQU2 body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyEQU2 object.
     */
    public FrameBodyEQU2(final byte interpolationMethod,
                         final String owner,
                         final short frequency,
                         final short volumeAdjustment) {
        setObject("Interpolation Method", new Byte(interpolationMethod));
        setObject("Owner", owner);
        this.addGroup(frequency, volumeAdjustment);
    }

    /**
     * Creates a new FrameBodyEQU2 object.
     */
    public FrameBodyEQU2(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        return "EQU2" + ((char) 0) + getOwner();
    }

    public String getOwner() {
        return (String) getObject("Owner");
    }

    /**
     * @param description
     */
    public void getOwner(final String description) {
        setObject("Owner", description);
    }

    /**
     * @param frequency
     * @param volumeAdjustment
     */
    public void addGroup(final short frequency, final short volumeAdjustment) {
        final ObjectGroupRepeated group = (ObjectGroupRepeated) this.getObject("Data");
        final AbstractMP3Object freq = new ObjectNumberFixedLength("Frequency", 2);
        final AbstractMP3Object volume = new ObjectNumberFixedLength("Volume Adjustment", 2);
        group.addObject(freq);
        group.addObject(volume);
        setObject("Data", group);
    }

    /**
     *
     */
    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap("Interpolation Method", 1));
        appendToObjectList(new ObjectStringNullTerminated("Owner"));
        final ObjectGroupRepeated group = new ObjectGroupRepeated("Data");
        group.addProperty(new ObjectNumberFixedLength("Frequency", 2));
        group.addProperty(new ObjectNumberFixedLength("Volume Adjustment", 2));
        appendToObjectList(group);
    }
}