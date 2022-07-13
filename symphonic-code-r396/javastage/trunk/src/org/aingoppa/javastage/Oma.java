/*
 * Oma.java
 *
 * Created on September 18, 2007, 8:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.aingoppa.javastage;

/**
 *
 * @author aingoppa
 */
import java.io.*;
import java.util.*;
import java.util.zip.DataFormatException;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;


public class Oma {
	private String pathname;
	
	public String getTitle() {
		
		if (null == meta)
		{
			try {
				loadMeta();
			} catch (Exception e) {
				return "Fail to read title";
			}
		}
		return meta.title;
	}
		
	private String filepathOma;
	
	/** ea3 header data */
	
	public static class Meta {
		// 'ea3' data
		public String title = "";
		public String artist = "";
		public String album = "";
		public String genre = "";
		public String length = "";
		public int trackTitle; // not correct
		public String composer;
		public int trackAlbum; // not correct
		public int picture; // wrong data type
		// 'EA3' data
		public byte type1;
		public short type2;
		public byte[] cidEa3;
		public int lengthEa3;
		public int formatEa3;
		public int framesEa3;
/*		public String artistTxxx = null; // "OMG_TPE1S" + artist
		public String trackTxxx = null; // "OMG_TRACK\0" + track_num
		public String year = null;
		public String yearTxxx = null; // "OMG_TRLDA\0" + year + "/01/01 00:00:00"
		public String albumTxxx = null; // "OMG_ALBMS" + album
		public String length = null;
*/		
		private String readFrameString(DataInputStream dataIn, int size, short version) throws java.io.IOException {
			byte typeString = dataIn.readByte();
			byte[] byteString = new byte[size -1];
			dataIn.read(byteString);
/*
			switch(version) {
				case 0x0300:
					switch(typeString) {
						case 0:
							return new String(byteString, "EUC-KR"); // sometimes not ISO-8859-1
						case 1:
						default:
							return new String(byteString, "UTF-16");
					}
				case 0x0400:
				default:
					switch(typeString) {
						case 0:
							return new String(byteString, "EUC-KR"); // sometimes not ISO-8859-1
						case 1:
							return new String(byteString, "UTF-16");
						case 2:
							return new String(byteString, "UTF-16BE");
						case 3:
						default:
							return new String(byteString, "UTF-8");
					}
			}
*/			
			// robust decoding
			switch(version) {
				case 0x0300:
				case 0x0400:
				default:
					switch(typeString) {
						case 0:
							return new String(byteString, Helper.charsetEUC_KR); // sometimes not ISO-8859-1
						case 1:
							return new String(byteString, Helper.charsetUTF_16);
						case 2:
							return new String(byteString, Helper.charsetUTF_16BE);
						case 3:
						default:
							return new String(byteString, Helper.charsetUTF_8);
					}
			}
		}
				
		private void readId3(DataInputStream dinID3) throws Exception {
			// "ID3"
			byte[] id = new byte[3];
			dinID3.read(id);
			
			// "ID3"
			if(0x49 != id[0] || 0x44 != id[1] || 0x33 != id[2])
				return;

			// allow version 2.3 or 2.4 only
			short version = dinID3.readShort();
			if(0x0300 != version && 0x0400 != version)
				return;

			// skip flags
			dinID3.skipBytes(1);

			int size = Helper.toNormalInt(dinID3.readInt());
			int sizeLeft = size;

			byte[] frameId = new byte[4];
			int frameSize;
			short frameFlags;
			String frameIdString;

			while(0 < sizeLeft) {
				dinID3.read(frameId); sizeLeft -= frameId.length;
				if (Arrays.equals(frameId, new byte[] {0x00, 0x00, 0x00, 0x00}))
					break;
				frameSize = dinID3.readInt(); sizeLeft -= 4;
				if(0x0400 == version) {
					frameSize = Helper.toNormalInt(frameSize);
				}
				if (0 == frameSize)
					break;
				frameFlags = dinID3.readShort(); sizeLeft -= 2;
				frameIdString = new String(frameId);
				if(frameIdString.equals("TIT2")) {
					title = readFrameString(dinID3, frameSize, version);
				} else if(frameIdString.equals("TPE1")) {
					artist = readFrameString(dinID3, frameSize, version);
				} else if(frameIdString.equals("TALB")) {
					album = readFrameString(dinID3, frameSize, version);
				} else if(frameIdString.equals("TCON")) {
					genre = readFrameString(dinID3, frameSize, version);
/*				} else if(frameIdString.equals("TLEN")) {
					length = readFrameString(dinID3, frameSize, version);
				} else if(frameIdString.equals("TLEN")) {
					tyer = readFrameString(dinID3, frameSize, version);
*/				} else {
					dinID3.skipBytes(frameSize);
				}

				sizeLeft -= frameSize;            
			}
        }
		
		private void readEa3(DataInputStream dinEa3) throws Exception{
			// "ea3"
			byte[] id = new byte[3];
			dinEa3.read(id);
			if(0x65 != id[0] || 0x61 != id[1] || 0x33 != id[2])
				throw new DataFormatException("not ea3 tag");

			// allow version 0x0300 only
			short version = dinEa3.readShort();
			if(0x0300 != version)
				throw new DataFormatException("version " + version + "not allowed");

			// skip flags
			dinEa3.skipBytes(1);

			int size = Helper.toNormalInt(dinEa3.readInt());
			int sizeLeft = size;

			byte[] frameId = new byte[4];
			int frameSize;
			short frameFlags;
			String frameIdString;

			while(0 < sizeLeft) {
				dinEa3.read(frameId); sizeLeft -= frameId.length;
				if (Arrays.equals(frameId, new byte[] {0x00, 0x00, 0x00, 0x00}))
					break;
				frameSize = dinEa3.readInt(); sizeLeft -= 4;
				frameFlags = dinEa3.readShort(); sizeLeft -= 2;
				frameIdString = new String(frameId);
				if(frameIdString.equals("TIT2")) {
					title = readFrameString(dinEa3, frameSize, version);
				} else if(frameIdString.equals("TPE1")) {
					artist = readFrameString(dinEa3, frameSize, version);
				} else if(frameIdString.equals("TALB")) {
					album = readFrameString(dinEa3, frameSize, version);
				} else if(frameIdString.equals("TCON")) {
					genre = readFrameString(dinEa3, frameSize, version);
				} else if(frameIdString.equals("TLEN")) {
					length = readFrameString(dinEa3, frameSize, version);
				} else {
					dinEa3.skipBytes(frameSize);
				}

				sizeLeft -= frameSize;
			}
			
			dinEa3.skipBytes(sizeLeft);
			dinEa3.read(id);
			if(0x45 != id[0] || 0x41 != id[1] || 0x33 != id[2])
				throw new DataFormatException("not EA3 tag");
			if(0x0200 != dinEa3.readShort())
				throw new DataFormatException("invalid EA3 version");
			type1 = dinEa3.readByte();
			type2 = dinEa3.readShort();
			cidEa3 = new byte[0x18];
			dinEa3.read(cidEa3);
			formatEa3 = dinEa3.readInt();
			lengthEa3 = dinEa3.readInt();
			framesEa3 = dinEa3.readInt();
			dinEa3.skipBytes(4); // fixme
			dinEa3.skipBytes(0x30); // fixme
		}
		
		private void writeEa3Frame(
			DataOutputStream doutEa3,
			String frameId,
			String frame) throws IOException {
			if(null == frame || 0 == frame.length())
				return;
			byte[] byteFrame = frame.getBytes(Helper.charsetUTF_16BE);
			
			doutEa3.write(frameId.getBytes(Helper.charsetISO_8859_1)); // Frame ID
			doutEa3.writeInt(byteFrame.length +1); // Size + encoding
			doutEa3.writeShort(0); // Flags
			doutEa3.writeByte(2); // Encoding UTF-16BE
			doutEa3.write(byteFrame);
		}
		
		private void writeEa3(DataOutputStream doutEa3) throws IOException {
			doutEa3.writeBytes(new String("ea3")); // tag
			doutEa3.writeByte(0x03); // version
			doutEa3.writeShort(0); // flags
			doutEa3.writeInt(Helper.toSafeInt(getEa3LengthFit())); // size
		
			writeEa3Frame(doutEa3, "TIT2", title);
			writeEa3Frame(doutEa3, "TPE1", artist);
			writeEa3Frame(doutEa3, "TALB", album);
			writeEa3Frame(doutEa3, "TCON", genre);
			
			doutEa3.write(new byte[getEa3LengthFit() - getEa3Length()]);
		}
		
		private int getEa3FrameLength(String frame) {
			// Frame ID + Size + Flags + frame w/encoding
			if (null == frame || 0 == frame.length())
				return 0;

			int lengthFrame = 0;
			try {
				lengthFrame = frame.getBytes(Helper.charsetUTF_16BE).length;
			} catch (UnsupportedEncodingException e) {
				lengthFrame = frame.length();
			}
			return 4 + 4 + 2 + 1 + lengthFrame;
		}
		private int getEa3Length() {
			int length = 0;

			length += getEa3FrameLength(title);
			length += getEa3FrameLength(artist);
			length += getEa3FrameLength(album);
			length += getEa3FrameLength(genre);

			return length;
		}
		private int getEa3LengthFit() {
			/*
			 * 0x0000 ~ 0x0bff : 0x0c00 - 10
			 * 0x0c00 ~ 0x17ff : 0x1800 - 10
			 * 0x1800 ~ 0x23ff : 0x2400 - 10
			 * 0x2400 ~ 0x2fff : 0x3000 - 10
			 * ... 
			 */
			int totalLength = getEa3Length() + 0x0a;
			return (((totalLength / 0xc00) +1) * 0xc00) - 0x0a;
		}
	}
	
	Meta meta = null;
	
	public Meta getMeta() throws Exception {
		if (null == meta)
			loadMeta();
		return meta;
	}
	
	private void loadMeta() throws Exception {
		if (null != meta)
			return;
		
		meta = new Meta();
		File fileEa3 = new File(filepathOma);
		DataInputStream dinOma = new DataInputStream(new FileInputStream(fileEa3));
		meta.readEa3(dinOma);
		dinOma.close();
	}
	
	/** Creates a new instance of OMAFile */
	public Oma(String filepathOma) {
		this.filepathOma = filepathOma;
	}
	
	private static class Tag {
		String title = null;
		String album = null;
		String artist = null;
		String genre = null;
	}
	
	private static Tag readId3(DataInputStream din) {
		Tag tag = new Tag();
		
		return tag;
	}

	public static void convertMp3ToOma(String filepathMp3, String filepathOma)
			throws Exception {
		File fileOma = new File(filepathOma);
		DataOutputStream doutOma = 
				new DataOutputStream(new FileOutputStream(fileOma));

		File fileMp3 = new File(filepathMp3);

		InputStream inMp3 = new FileInputStream(fileMp3);
		Bitstream stream = new Bitstream(inMp3);
		
		{
			InputStream inputStream = stream.getRawID3v2();
			if (null != inputStream) {
				DataInputStream dinId3 = new DataInputStream(inputStream);
				Tag tag = readId3(dinId3);
				dinId3.close();
			}
		}
	}
	
	public void convertMp3ToOma(String filepathMp3) throws Exception{
//		Helper.printUtf8("Adding : " + filepathMp3 + "\n");
		
		File fileOma = new File(filepathOma);
		File fileMp3 = new File(filepathMp3);
		
		DataOutputStream doutOma = null;
		doutOma = new DataOutputStream(new FileOutputStream(fileOma));
		
		/*
		 * write ea3
		 */
		InputStream inMp3 = new FileInputStream(fileMp3);
		Bitstream stream = new Bitstream(inMp3);

		meta = new Meta();
		{
			InputStream inputStream = stream.getRawID3v2();
			if (null != inputStream) {
				DataInputStream dinId3 = new DataInputStream(inputStream);
				meta.readId3(dinId3);
				dinId3.close();
			}
			if (null == meta.title || 0 == meta.title.length() || Helper.useFilenameAsTitle) {
				String title = fileMp3.getName();
				meta.title = title.substring(0, title.lastIndexOf("."));
//				meta.title = sun.text.Normalizer.normalize(meta.title, sun.text.Normalizer.COMPOSE, 0);
				meta.title = Helper.normalizeNFC(meta.title);
			}
			if (null != Helper.enforcedAlbumName) {
				meta.album = Helper.enforcedAlbumName;
			} else if (null == meta.album || 0 == meta.album.length() || Helper.useDirectoryAsAlbum) {
				String album = fileMp3.getParentFile().getName();
				meta.album = album;
//				meta.album = sun.text.Normalizer.normalize(meta.album, sun.text.Normalizer.COMPOSE, 0);
				meta.album = Helper.normalizeNFC(meta.album);
			}
		}
		meta.writeEa3(doutOma);
		
				
		/*
		 * write EA3 0x60 bytes
		 */
		// byte 0x00 - 0x07 : header
		doutOma.writeBytes(new String("EA3")); // tag
		doutOma.writeShort(Helper.metaVersion); // meta version.
		meta.type1 = 0x60;
		doutOma.writeByte(meta.type1); // fix me
		meta.type2 = (short)0xffff;
		doutOma.writeShort(meta.type2); // fix me
		
		// byte 0x08 - 0x1f : matches 05CIDLST.DAT. DRM or sonicstage value?
		meta.cidEa3 = new byte[] {
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x01, (byte)0x0F, (byte)0x50, (byte)0x00,
			(byte)0x00, (byte)0x04, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0xD6, (byte)0x35, (byte)0xB7,
			(byte)0xCC, (byte)0x1F, (byte)0xFA, (byte)0x03,
			(byte)0x1F, (byte)0xC9, (byte)0xE2, (byte)0x79
		}; // fix me
		doutOma.write(meta.cidEa3);
		

		// MPEG info
		{
			Header header = stream.readFrame();
			stream.unreadFrame();

			int mpegInfo;
			
			// byte 0x20 : source file type
			mpegInfo = 0x03; // 1 : atrac, 3 : mp3, 5 : wma
			meta.formatEa3 = mpegInfo << 24;

			// byte 0x21 : bitrate type
			mpegInfo = (header.vbr()) ? (int)0x90 : (int)0x80;
			meta.formatEa3 |= mpegInfo << 16;

			// byte 0x22 : version & layer
			mpegInfo =
				(
					(header.version() == 0) ? 0x80 : // V2
					(header.version() == 1) ? 0xc0 : // V1
					(header.version() == 2) ? 0x00 : 0x40 // V2.5 : reserved
				) | (
					(header.layer() == 1) ? 0x30 : // L1
					(header.layer() == 2) ? 0x20 : // L2
					(header.layer() == 3) ? 0x10 : 0x00 // L3 : reserved
				) | header.bitrate_index();
			meta.formatEa3 |= mpegInfo << 8;

			// byte 0x23 : frequency and mode
			mpegInfo =
				(header.sample_frequency() << 6) | header.mode() << 4;
			meta.formatEa3 |= mpegInfo;

			doutOma.writeInt(meta.formatEa3);

			// byte 0x24 - 0x27 : length in millisecond
			stream.readFrame();

			meta.lengthEa3 =
				(int)((fileMp3.length() - stream.header_pos()) * 8 * 1000 /
				header.bitrate());
			doutOma.writeInt(meta.lengthEa3); // length in millisecond
			
			// 'ea3' tag
			meta.length = String.format("%d", (meta.lengthEa3 / 1000) * 1000);
			
			// byte 0x28 - 0x0x2b : frame count
			meta.framesEa3 =
				(int)((fileMp3.length() - stream.header_pos()) /
				header.framesize);
			doutOma.writeInt(meta.framesEa3);
			
			// byte 0x2c - 0x2f : unknown
			doutOma.writeInt(0); // fix me
			
			// byte 0x30 - 0x5f : unknown
			doutOma.write(new byte[0x30]); // fix me
		}

		// copy all mp3 frames
		try {
			Helper.bulkCopy(inMp3, doutOma);
		} catch (IOException e) {
			fileOma.delete();
			throw e;
		}

		// write frames
		doutOma.close();
	}
	public String toString() {
		return getTitle();
	}
	public static void main(String args[]) {
	}
}
