package org.farng.mp3.id3;

import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.TagException;
import org.farng.mp3.TagNotFoundException;
import org.farng.mp3.TagOptionSingleton;
import org.farng.mp3.TagUtility;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

/**
 * Title: ID3v1_1 Description: This class is for a ID3v1.1 Tag Copyright: Copyright (c) 2002 Company:
 *
 * @author Eric Farng
 * @version $Revision: 1.3 $
 */
public class ID3v1_1 extends ID3v1 {

    /**
     *
     */
    protected byte track = -1;

    /**
     * Creates a new ID3v1_1 object.
     */
    public ID3v1_1() {
        super();
    }

    /**
     * Creates a new ID3v1_1 object.
     */
    public ID3v1_1(final ID3v1_1 copyObject) {
        super(copyObject);
        this.track = copyObject.track;
    }

    /**
     * Creates a new ID3v1_1 object.
     */
    public ID3v1_1(final AbstractMP3Tag mp3tag) {
        if (mp3tag != null) {
            if (mp3tag instanceof ID3v1) {
                if (mp3tag instanceof ID3v1_1) {
                    throw new UnsupportedOperationException("Copy Constructor not called. Please type cast the argument");
                }

                // id3v1_1 objects are also id3v1 objects
                final ID3v1 id3old = (ID3v1) mp3tag;
                this.title = new String(id3old.title);
                this.artist = new String(id3old.artist);
                this.album = new String(id3old.album);
                this.comment = new String(id3old.comment);
                this.year = new String(id3old.year);
                this.genre = id3old.genre;
            } else {
                // first change the tag to ID3v2_4 tag.
                // id3v2_4 can take any tag.
                final ID3v2_4 id3tag;
                id3tag = new ID3v2_4(mp3tag);
                ID3v2_4Frame frame;
                String text;
                if (id3tag.hasFrame("TIT2")) {
                    frame = (ID3v2_4Frame) id3tag.getFrame("TIT2");
                    text = ((FrameBodyTIT2) frame.getBody()).getText();
                    this.title = TagUtility.truncate(text, 30);
                }
                if (id3tag.hasFrame("TPE1")) {
                    frame = (ID3v2_4Frame) id3tag.getFrame("TPE1");
                    text = ((FrameBodyTPE1) frame.getBody()).getText();
                    this.artist = TagUtility.truncate(text, 30);
                }
                if (id3tag.hasFrame("TALB")) {
                    frame = (ID3v2_4Frame) id3tag.getFrame("TALB");
                    text = ((FrameBodyTALB) frame.getBody()).getText();
                    this.album = TagUtility.truncate(text, 30);
                }
                if (id3tag.hasFrame("TDRC")) {
                    frame = (ID3v2_4Frame) id3tag.getFrame("TDRC");
                    text = ((FrameBodyTDRC) frame.getBody()).getText();
                    this.year = TagUtility.truncate(text, 4);
                }
                if (id3tag.hasFrameOfType("COMM")) {
                    final Iterator iterator = id3tag.getFrameOfType("COMM");
                    text = "";
                    while (iterator.hasNext()) {
                        frame = (ID3v2_4Frame) iterator.next();
                        text += (((FrameBodyCOMM) frame.getBody()).getText() + " ");
                    }
                    this.comment = TagUtility.truncate(text, 28);
                }
                if (id3tag.hasFrame("TCON")) {
                    frame = (ID3v2_4Frame) id3tag.getFrame("TCON");
                    text = ((FrameBodyTCON) frame.getBody()).getText();
                    try {
                        this.genre = (byte) TagUtility.findNumber(text);
                    } catch (TagException ex) {
                        this.genre = 0;
                    }
                }
                if (id3tag.hasFrame("TRCK")) {
                    frame = (ID3v2_4Frame) id3tag.getFrame("TRCK");
                    text = ((FrameBodyTRCK) frame.getBody()).getText();
                    try {
                        this.track = (byte) TagUtility.findNumber(text);
                    } catch (TagException ex) {
                        this.track = 0;
                    }
                }
            }
        }
    }

    /**
     * Creates a new ID3v1_1 object.
     */
    public ID3v1_1(final RandomAccessFile file) throws TagNotFoundException, IOException {
        this.read(file);
    }

    /**
     * @param comment
     */
    public void setComment(final String comment) {
        this.comment = TagUtility.truncate(comment, 28);
    }

    public String getComment() {
        return this.comment;
    }

    public String getIdentifier() {
        return "ID3v1_1.10";
    }

    /**
     * @param track
     */
    public void setTrack(final byte track) {
        this.track = track;
    }

    public byte getTrack() {
        return this.track;
    }

    /**
     * @param tag
     */
    public void append(final AbstractMP3Tag tag) {
        final ID3v1_1 oldTag = this;
        final ID3v1_1 newTag;
        if (tag != null) {
            if (tag instanceof ID3v1_1) {
                newTag = (ID3v1_1) tag;
            } else {
                newTag = new ID3v1_1(tag);
            }
            if (tag instanceof org.farng.mp3.lyrics3.AbstractLyrics3) {
                TagOptionSingleton.getInstance().setId3v1SaveTrack(false);
            }
            oldTag.track = (TagOptionSingleton.getInstance().isId3v1SaveTrack() && (oldTag.track <= 0)) ?
                           newTag.track :
                           oldTag.track;

            // we don't need to reset the tag options because
            // we want to save all fields (default)
        }

        // we can't send newTag here because we need to keep the lyrics3
        // class type ... check super.append and you'll see what i mean.
        super.append(tag);
    }

    public boolean equals(final Object obj) {
        if ((obj instanceof ID3v1_1) == false) {
            return false;
        }
        final ID3v1_1 id3v1_1 = (ID3v1_1) obj;
        if (this.track != id3v1_1.track) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * @param tag
     */
    public void overwrite(final AbstractMP3Tag tag) {
        final ID3v1_1 oldTag = this;
        ID3v1_1 newTag = null;
        if (tag != null) {
            if (tag instanceof ID3v1_1) {
                newTag = (ID3v1_1) tag;
            } else {
                newTag = new ID3v1_1(tag);
            }
            if (tag instanceof org.farng.mp3.lyrics3.AbstractLyrics3) {
                TagOptionSingleton.getInstance().setId3v1SaveTrack(false);
            }
            oldTag.track = TagOptionSingleton.getInstance().isId3v1SaveTrack() ? newTag.track : oldTag.track;

            // we don't need to reset the tag options because
            // we want to save all fields (default)
        }
        super.overwrite(newTag);
    }

    /**
     * 
     * @param buffer
     * @param bufferLength
     * @return String 
     * @author Michael Chen (michaelrchen@gmail.com)
     * Date: 2008/3/23
     * Because when function read String from ID3v1_1, problem will not know which charset is used by those string.
     * This function will check the string, if it's not ISO8859-1 charset. This function will base on system
     * locale information, try to find better charset fot the String.  
     */
    private String DecodeCharsetString(byte[] buffer, int bufferLength) throws IOException{
    	java.util.Locale DefaultLC = java.util.Locale.getDefault();  // Get the system locale information.
    	String FineString= new String(buffer,0,bufferLength).trim();  // Default the string is ISO8859-1
    	
    	for(int checkPoint=0;checkPoint<bufferLength;checkPoint++){															// Check the string is ISO8859-1?
    		if(buffer[checkPoint]<0){																														// If the String isn't ISO8859-1 then tyr to decode it.
    			if(DefaultLC.getLanguage()=="zh" && DefaultLC.getCountry()=="TW"){ // check the Country and Language then decide charset
    				FineString = new String(buffer,"Big5").trim();	                   // for Traditional Chinese.
    				break;
    			}else if(DefaultLC.getLanguage()=="zh" && DefaultLC.getCountry()=="CN"){
    				FineString = new String(buffer,"gb2312").trim();												// for Simplified Chinese
    				break;
    			}else if(DefaultLC.getLanguage()=="ja" && DefaultLC.getCountry()=="JP"){
    				FineString = new String(buffer,"Shift_JIS").trim();               // for Japan, sometime the charset will be ujis.(need test)
    				break;
    			}else if(DefaultLC.getLanguage()=="ko" && DefaultLC.getCountry()=="KR"){
    				FineString = new String(buffer,"euckr").trim();	                  // for Korean, need test.
    			}
    			else{
    				break;
    				}
    		}
    	}
    	return FineString;
    }


    /**
     * @param file
     *
     * @throws TagNotFoundException
     * @throws IOException
     */
    public void read(final RandomAccessFile file) throws TagNotFoundException, IOException {
        final byte[] buffer = new byte[30];
        if (this.seek(file) == false) {
            throw new TagNotFoundException("ID3v1.1 tag not found");
        }

        file.read(buffer, 0, 30);
        //this.artist = new String(buffer, 0, 30).trim();//Marked by Michael for not ISO8859-1 case  2008/3/23
        this.title = DecodeCharsetString(buffer,30);  // added by Michael 2008/3/23
        
        file.read(buffer, 0, 30);
        //this.artist = new String(buffer, 0, 30).trim();//Marked by Michael for not ISO8859-1 case  2008/3/23
        this.artist = DecodeCharsetString(buffer,30); // added by Michael 2008/3/23
        
        file.read(buffer, 0, 30);
        //this.album = new String(buffer, 0, 30).trim();//Marked by Michael for not ISO8859-1 case  2008/3/23
        this.album = DecodeCharsetString(buffer,30); // added by Michael 2008/3/23
                
        
        file.read(buffer, 0, 4);
        //this.year = new String(buffer, 0, 4).trim();//Marked by Michael for not ISO8859-1 case  2008/3/23
        this.year = DecodeCharsetString(buffer,4); // added by Michael 2008/3/23
        
        
        file.read(buffer, 0, 28);
        //this.comment = new String(buffer, 0, 28).trim();//Marked by Michael for not ISO8859-1 case  2008/3/23
        this.comment = DecodeCharsetString(buffer,28); // added by Michael 2008/3/23


        // if this value is zero, then check the next value
        // to see if it's the track number. ID3v1.1
        file.read(buffer, 0, 2);
        if (buffer[0] == 0) {
            this.track = buffer[1];
        } else {
            throw new TagNotFoundException("ID3v1.1 Tag Not found");
        }
        file.read(buffer, 0, 1);
        this.genre = buffer[0];
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public boolean seek(final RandomAccessFile file) throws IOException {
        final byte[] buffer = new byte[3];
        if (file.length() < 128) {
            return false;
        }

        // Check for the empty byte before the TRACK
        file.seek(file.length() - 3);
        buffer[0] = file.readByte();
        if (buffer[0] != 0) {
            return false;
        }

        // If there's a tag, it's 128 bytes long and we'll find the tag
        file.seek(file.length() - 128);

        // read the TAG value
        file.read(buffer, 0, 3);
        final String tag = new String(buffer, 0, 3);
        return tag.equals("TAG");
    }

    public String toString() {
        String str = getIdentifier() + " " + this.getSize() + "\n";
        str += ("Title = " + this.title + "\n");
        str += ("Artist = " + this.artist + "\n");
        str += ("Album = " + this.album + "\n");
        str += ("Comment = " + this.comment + "\n");
        str += ("Year = " + this.year + "\n");
        str += ("Genre = " + this.genre + "\n");
        str += ("Track = " + this.track + "\n");
        return str;
    }

    /**
     * @param tag
     */
    public void write(final AbstractMP3Tag tag) {
        final ID3v1_1 oldTag = this;
        ID3v1_1 newTag = null;
        if (tag != null) {
            if (tag instanceof ID3v1_1) {
                newTag = (ID3v1_1) tag;
            } else {
                newTag = new ID3v1_1(tag);
            }
            oldTag.track = newTag.track;
        }
        super.write(newTag);
    }

    /**
     * @param file
     *
     * @throws IOException
     */
    public void write(final RandomAccessFile file) throws IOException {
        final byte[] buffer = new byte[128];
        int i;
        int offset = 3;
        String str;
        delete(file);
        file.seek(file.length());
        buffer[0] = (byte) 'T';
        buffer[1] = (byte) 'A';
        buffer[2] = (byte) 'G';
        str = TagUtility.truncate(this.title, 30);
        for (i = 0; i < str.length(); i++) {
            buffer[i + offset] = (byte) str.charAt(i);
        }
        offset += 30;
        str = TagUtility.truncate(this.artist, 30);
        for (i = 0; i < str.length(); i++) {
            buffer[i + offset] = (byte) str.charAt(i);
        }
        offset += 30;
        str = TagUtility.truncate(this.album, 30);
        for (i = 0; i < str.length(); i++) {
            buffer[i + offset] = (byte) str.charAt(i);
        }
        offset += 30;
        str = TagUtility.truncate(this.year, 4);
        for (i = 0; i < str.length(); i++) {
            buffer[i + offset] = (byte) str.charAt(i);
        }
        offset += 4;
        str = TagUtility.truncate(this.comment, 28);
        for (i = 0; i < str.length(); i++) {
            buffer[i + offset] = (byte) str.charAt(i);
        }
        offset += 28;
        offset++;
        buffer[offset] = this.track; // skip one byte extra blank for 1.1

        // definition
        offset++;
        buffer[offset] = this.genre;
        file.write(buffer);
    }
}