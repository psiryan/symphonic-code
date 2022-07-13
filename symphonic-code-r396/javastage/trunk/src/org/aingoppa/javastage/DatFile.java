/*
 * DatFile.java
 *
 * Created on October 10, 2007, 2:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.aingoppa.javastage;

import org.aingoppa.javastage.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author aingoppa
 */
public class DatFile {
	private String filepath = null;;
	private byte[] id = new byte[4];
	private final short version = 0x0200;
	//private byte countBlock = 0; // auto calculate this
	
	public class Block {
		private final byte[] id = new byte[4];
		//private int start; // auto calculate this
		//private int size; // auto calculate this
		private short countItem = 0;
		private short sizeItem = 0;
		private final byte[] reserved = new byte[8];
		private byte[] content = null;
		
		public void setContent(short countItem, byte[] reserved, byte[] content) throws Exception {
			this.countItem = countItem;
			if (null != reserved) {
				assert reserved.length == this.reserved.length : "Length of reserved mismatch!";
				System.arraycopy(reserved, 0, this.reserved, 0, reserved.length);
			}
			this.content = content;
		}
		
		public short getCountItem() {
			return countItem;
		}
		
		public short getSizeItem() {
			return sizeItem;
		}
		
		public byte[] getReserved() {
			return reserved;
		}
		
		public byte[] getContent() {
			return content;
		}
		
		private void readHead(DataInputStream dataIn) throws Exception {
			dataIn.read(id);
			int start = dataIn.readInt();
			int size = dataIn.readInt();
			if (0x10 > size)
				throw new Exception(
					String.format("Block size %d is too small", size));

			content = new byte[size - 0x10];
			
			dataIn.skipBytes(4);
		}
		
		private void writeHead(DataOutputStream dataOut, int start) throws Exception {
			dataOut.write(id);
			dataOut.writeInt(start);
			dataOut.writeInt(0x10 + ((null == content) ? 0 : content.length));
			dataOut.writeInt(0);
		}

		private void readBody(DataInputStream dataIn) throws Exception {
			final byte[] id = new byte[4];
			dataIn.read(id);
			if (!Arrays.equals(this.id, id))
				throw new Exception(
					"Id mismatch. '" +
					new String(this.id) +
					"' in head and '" +
					new String(id) +
					"' in body");
			
			countItem = dataIn.readShort();
			sizeItem = dataIn.readShort();
			dataIn.read(reserved);
			if (null != content)
				dataIn.read(content);
		}

		private void writeBody(DataOutputStream dataOut) throws Exception {
			dataOut.write(id);
			dataOut.writeShort(countItem);
			dataOut.writeShort(sizeItem);
			dataOut.write(reserved);
			if (null != content)
				dataOut.write(content);
		}
	}
	
	private Block[] blocks = null;
	
	public Block getBlock(int index) {
		return blocks[index];
	}
	
	public Block getBlock(int index, byte[] id) {
		Block block = blocks[index];
		
		assert Arrays.equals(id, block.id) :
				"requested id == '" +
				new String(id) +
				"'.  block id '" +
				new String(block.id) + "'.";
		
		return block;
	}
	
	private void load() throws Exception {
		File file = new File(filepath);
		if (!file.exists() || !file.isFile())
			throw new Exception("File '" + filepath + "' not found.");
		
		DataInputStream dataIn =
			new DataInputStream(new FileInputStream(file));
		
		byte[] id = new byte[4];
		dataIn.read(id);
		if (!Arrays.equals(id, this.id))
			throw new Exception(
				"Invalid Dat header " +
				new String(id) +
				" and " +
				new String(this.id));
		if (version != dataIn.readShort())
			throw new Exception(
				String.format(
				"Version %04x not acceptable. (%04x required)",
				version,
				Helper.metaVersion));
		
		dataIn.skipBytes(2);
		byte countBlock = dataIn.readByte();
		dataIn.skipBytes(7);
		
		blocks = new Block[countBlock];
		
		for (int i = 0; i < blocks.length; i++) {
			blocks[i] = new Block();
		}
		
		for(Block block : blocks) {
			block.readHead(dataIn);
		}
		
		for(Block block : blocks) {
			block.readBody(dataIn);
		}
	}
	
	public void save() throws Exception {
		File file = new File(filepath);
		if (!file.exists() || !file.isFile())
			throw new Exception("File '" + filepath + "' not found.");
		
		DataOutputStream dataOut =
			new DataOutputStream(new FileOutputStream(file));
		
		dataOut.write(id);
		dataOut.writeShort(version);
		dataOut.writeShort(0);
		dataOut.writeByte(blocks.length);
		dataOut.write(new byte[7]);
		
		int start =
			0x10 + // dat header
			0x10 * blocks.length; // each block header sizes
		for(Block block : blocks) {
			block.writeHead(dataOut, start);
			// total block body size
			if (null == block.content) {
				start += 0x10;
			} else {
				start += 0x10 + block.content.length; 
			}
		}

		for(Block block : blocks) {
			block.writeBody(dataOut);
		}
}
	
	/** Creates a new instance of DatFile */
	public DatFile(String filepath, byte[] id) throws Exception {
		this.filepath = filepath;
		System.arraycopy(id, 0, this.id, 0, id.length);
		load();
	}
	
	static public void main(String[] args) {
		try {
			DatFile datFile = new DatFile(
				"/Users/aingoppa/work/nwe/test/OMGAUDIO.angel/01TREE2D.DAT",
				new byte[] {'T', 'R', 'E', 'E'});
			datFile.save();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
