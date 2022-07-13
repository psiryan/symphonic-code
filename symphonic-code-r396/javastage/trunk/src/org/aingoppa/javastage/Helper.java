/*
 * Helper.java
 *
 * Created on September 18, 2007, 11:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.aingoppa.javastage;

import java.lang.String;
import java.nio.charset.Charset;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import org.aingoppa.javastage.Java6ToolBox;
/**
 *
 * @author aingoppa
 */
public class Helper {
	static public final String INVALID_FORMAT = "Invalid format";
        static public final String charsetUTF_8 = "UTF-8";
	static public final String charsetUTF_16BE = "UTF-16BE";
	static public final String charsetUTF_16LE = "UTF-16LE";
	static public final String charsetUTF_16 = "UTF-16";
	static public final String charsetISO_8859_1 = "ISO-8859-1";
	static public final String charsetEUC_KR = "EUC-KR";
/*	static public final Charset charsetUTF_8 = Charset.forName("UTF-8");
	static public final Charset charsetUTF_16BE = Charset.forName("UTF-16BE");
	static public final Charset charsetUTF_16LE = Charset.forName("UTF-16LE");
	static public final Charset charsetUTF_16 = Charset.forName("UTF-16");
	static public final Charset charsetISO_8859_1 = Charset.forName("ISO-8859-1");
	static public final Charset charsetEUC_KR = Charset.forName("EUC-KR");
*/	static public final short metaVersion = 0x0200;
	
	static public boolean useFilenameAsTitle = false;
	static public boolean useDirectoryAsAlbum = false;
	static public String enforcedAlbumName = null;
	
	/** Creates a new instance of Helper */
	public Helper() {
	}
	
	static private String pathOmgAudio = "./OMGAUDIO";
	static public void setPathPlayer(String pathOmgAudio) {
		Helper.pathOmgAudio = pathOmgAudio;
	}
	static public String getPathPlayer() {
		return pathOmgAudio;
	}
	
	static public String getPathOma(int index) {
		return String.format(
			"%s/10F%02X/1000%04X.OMA",
			getPathPlayer(),
			index >>> 8,
			index);
	}

	static byte[] bytesCopy = new byte[4096]; // copy 4KB each time
	
	// fix me : to locked function
	static public void bulkCopy(InputStream in, OutputStream out) throws IOException
	{
		int countCopy = 0;

		while (-1 != (countCopy = in.read(bytesCopy)))
		{
			out.write(bytesCopy, 0, countCopy);
		}
	}

	static public void printUtf8(String stringToPrint) {
		try {
			System.out.write(stringToPrint.getBytes(charsetUTF_8));
		} catch (IOException e) {
			System.out.printf("%s", stringToPrint);
		}
	}
	
    static public String normalizeNFC(String str) {
		return Java6ToolBox.normalizeNFC(str);
	}
	
	static public short getShort(byte[] bytes, int offset) {
		short value = 0;
		value = (short)(bytes[offset] & 0xff);
		value <<= 8;
		value |= (short)(bytes[offset +1] & 0xff);
		return value;
	}

	static public int getInt(byte[] bytes, int offset) {
		int value = 0;
		value = (int)(bytes[offset] & 0xff);
		value <<= 8;
		value |= (int)(bytes[offset +1] & 0xff);
		value <<= 8;
		value |= (int)(bytes[offset +2] & 0xff);
		value <<= 8;
		value |= (int)(bytes[offset +3] & 0xff);
		return value;
	}
	
	static public void setShort(byte[] bytes, int offset, short value) {
		bytes[offset] = (byte)((value >>> 8) & 0xff);
		bytes[offset +1] = (byte)(value & 0xff);
	}
	
	static public void setInt(byte[] bytes, int offset, int value) {
		bytes[offset] = (byte)((value >>> 24) & 0xff);
		bytes[offset +1] = (byte)((value >>> 16) & 0xff);
		bytes[offset +2] = (byte)((value >>> 8) & 0xff);
		bytes[offset +3] = (byte)(value & 0xff);
	}

	static public int toNormalInt(int normalInt) {
        return
            ((normalInt & 0x7f000000) >>> 3) |
            ((normalInt & 0x007f0000) >>> 2) |
            ((normalInt & 0x00007f00) >>> 1) |
            ((normalInt & 0x0000007f) >>> 0);
    }
    static public int toSafeInt(int safeInt) {
        return
            ((safeInt & 0x0fe00000) << 3) |
            ((safeInt & 0x001fc000) << 2) |
            ((safeInt & 0x00003f80) << 1) |
            ((safeInt & 0x0000007f) << 0);
    }
	
	public static void main(final String args[]) {
		out_out:
			for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
				if (i == 10 && j == 10)
					break out_out;
			}
			}
	}
	
}
