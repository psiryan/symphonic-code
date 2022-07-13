/*
 * Manager.java
 *
 * Created on September 26, 2007, 11:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.aingoppa.javastage;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import org.aingoppa.javastage.*;

import java.io.*;
import java.util.*;
import java.lang.*;

/*
 * ToDo
 * use new byte[4] {'T', 'A', 'G', 'S'}
 * static Oma.convertMp3ToOma(), return meta
 * Oma.getMeta(), remove Oma.getTitle(). use content information instead
 * no Meta member in Oma
 * Helper : option -eEUC-KR
 * string codec more precise
 * metaInfo02 -> metaArtist
 * exactly as SonicStage
 * list option - all [-v], album [album id], ...
 * exception in addMusic <- just skip
 * szab
 * random CID, ignore same music if has same cid
 * jacket
 * class Dat(read file), DatGroupTreeList, DatAll, DatArtist, DatAlbum, DatGenre, DatArtistAlbum, DatTreeInformation, DatContentInformation, DatContentIdList
 * index is 1 ~ 0xffff. this is not actually (signed)short
 * merge treeEach - profileEach
 * print progress for each music
 * sort before saving
 */
	
/**
 *
 * @author aingoppa
 */
public class Manager {
	private DatFile
		datGroupList,
		datTreeEach, datTreeArtist, datTreeAlbum, datTreeGenre, datTreeArtistAlbum,
		datTreeInformation,
		datGroupEach, datGroupArtist, datGroupAlbum, datGroupGenre, datGroupArtistAlbum,
		datContentInformation,
		datContentIdList;
		
	private Vector<Oma> musics = new Vector<Oma>();
	
	/** Creates a new instance of Manager */
	public Manager(String pathOmgaudio) throws Exception {
		Helper.setPathPlayer(pathOmgaudio);

		loadMusics();
		loadMeta();
	}
	
	private void initMeta() {
		
	}
	
	private class ContentInformation {
		private short magic1 = 0;
		private short magic2;
		private int format;
		private int lengthEa3;
		private String[] strings = new String[5];
	}
	private Vector<ContentInformation> contentInformations = new Vector<ContentInformation>();
	
	private class Profile {
		private int lengthEa3;
		private String[] strings;
		
		public Profile(int countString) {
			strings = new String[countString];
		}
	}
	private Vector<Profile> profileEach = new Vector<Profile>();
	private Vector<Profile> profileArtist = new Vector<Profile>();
	private Vector<Profile> profileAlbum = new Vector<Profile>();
	private Vector<Profile> profileGenre = new Vector<Profile>();
	private Vector<Profile> profileArtistAlbum = new Vector<Profile>();
	
	private class TreeInformation {
		private int length;
//		private String information;
	}
	private Vector<TreeInformation> treeInformations = new Vector<TreeInformation>();
	
	private class TreeItem {
		Vector<Short> indexes = null;
		Vector<TreeItem> treeItems = null;
		
		private byte getLevel() {
			if (Vector.isEmpty(indexes) && Vector.isEmpty(treeItems))
				return 0;
			else if (!Vector.isEmpty(indexes) && Vector.isEmpty(treeItems))
				return 1;
			else if (Vector.isEmpty(indexes) && !Vector.isEmpty(treeItems))
				assert false : "not implemented yet";
			
			return 0;
		}
	}
	
	private Vector<TreeItem> treeEach = new Vector<TreeItem>();
	private Vector<TreeItem> treeAlbum = new Vector<TreeItem>();
	
	private class ContentId {
		byte[] cid = new byte[0x30];
	}
		
	private Vector<ContentId> contentIds = new Vector<ContentId>();

	private Vector<TreeItem> loadTree(DatFile datFileTree) throws Exception
	{

		DatFile.Block blockGplb = datFileTree.getBlock(0, new byte[] {'G', 'P', 'L', 'B'});
		DatFile.Block blockTplb = datFileTree.getBlock(1, new byte[] {'T', 'P', 'L', 'B'});

		int countGplbTotal = blockGplb.getCountItem();
		byte[] contentGplb = blockGplb.getContent();
		int countTplbTotal = blockTplb.getCountItem();
		byte[] contentTplb = blockTplb.getContent();

		short countGplbReal = Helper.getShort(blockGplb.getReserved(), 2);
		short countTplbReal = Helper.getShort(blockTplb.getReserved(), 2);
		
		Vector<TreeItem> tree = new Vector<TreeItem>();
		
		for (int i = 0; i < countGplbTotal; i++) {
			int offset = 0x08 * i;
			short index = Helper.getShort(contentGplb, offset);
			byte type = contentGplb[offset + 0x02];
			short indexTplb = Helper.getShort(contentGplb, offset + 0x04);

			TreeItem treeItem = new TreeItem();
			switch (type) {
				case 0: // contains nothing
					break;
				case 1: // top layer
					if (0 != indexTplb) {
						treeItem.indexes = new Vector<Short>();

						short endIndex;
						if (i == countGplbReal -1) {
							endIndex = (short)(countTplbTotal + 1);
						} else {
							endIndex = Helper.getShort(contentGplb, offset + 0x08 + 0x04);
						}
						for (short j = indexTplb; j < endIndex; j++) {
							treeItem.indexes.set(
								j - indexTplb,
								new Short(Helper.getShort(contentTplb, 0x02 * (j -1))));
						}
					} else {
						treeItem.treeItems = new Vector<TreeItem>();
					assert false : "not implemented yet";
					}
					break;
				case 2: // second layer. used in 01TREE2D.DAT (artist-album)
					assert false : "not implemented yet";
				default:
					throw new Exception("invalid tree item type");
			}

			tree.set(index, treeItem);
		}
		return tree;
	}
	
	private byte[] saveTree(Vector<TreeItem> tree, byte[] reserved)
	{
		return null;
	}
	
	private final byte[][] idsEach = new byte[][] {
		{'T', 'I', 'T', '2'},
		{'T', 'P', 'E', '1'},
		{'T', 'C', 'O', 'N'},
		{'T', 'S', 'O', 'P'},
		{'P', 'I', 'C', 'P'},
		{'P', 'I', 'C', '0'}};
	private final byte[][] idsAlbum = new byte[][] {
		{'T', 'I', 'T', '2'}
	};
	
	private Vector<Profile> loadGinf(byte[][] ids, DatFile datFileGroup) throws Exception
	{
		DatFile.Block blockGpfb = datFileGroup.getBlock(0, new byte[] {'G', 'P', 'F', 'B'});

		int countGpfb = blockGpfb.getCountItem();
		byte[] contentGpfb = blockGpfb.getContent();
		
		Vector<Profile> profiles = new Vector<Profile>();
		
		for (int i = 0; i < countGpfb; i++) {
			int offset = (0x10 + ids.length * 0x80) * i;
			Profile profile = new Profile(ids.length);
			profile.lengthEa3 = Helper.getInt(contentGpfb, offset + 0x08);
			for (int j = 0; j < ids.length; j++) {
				int subOffset = offset + 0x10 + 0x80 * j;
				
				profile.strings[j] = new String(
					contentGpfb, 
					subOffset + 0x06, 0x80 - 0x06,
					Helper.charsetUTF_16BE).trim();
			}
			profiles.set(i + 1, profile);
		}
		
		return profiles;
	}
	
	private byte[] saveGinf(Vector<Profile> strings, byte[] reserved)
	{
		return null;
	}
	
	private void loadMeta() throws Exception {
		System.out.println("Loading dat files");
		
		datGroupList = new DatFile(Helper.getPathPlayer() + "/00GTRLST.DAT", new byte[] {'G', 'T', 'L', 'T'});
		datTreeEach = new DatFile(Helper.getPathPlayer() + "/01TREE01.DAT", new byte[] {'T', 'R', 'E', 'E'});
		datTreeArtist = new DatFile(Helper.getPathPlayer() + "/01TREE02.DAT", new byte[] {'T', 'R', 'E', 'E'});
		datTreeAlbum = new DatFile(Helper.getPathPlayer() + "/01TREE03.DAT", new byte[] {'T', 'R', 'E', 'E'});
		datTreeGenre = new DatFile(Helper.getPathPlayer() + "/01TREE04.DAT", new byte[] {'T', 'R', 'E', 'E'});
		datTreeArtistAlbum = new DatFile(Helper.getPathPlayer() + "/01TREE2D.DAT", new byte[] {'T', 'R', 'E', 'E'});
		datTreeInformation = new DatFile(Helper.getPathPlayer() + "/02TREINF.DAT", new byte[] {'G', 'T', 'I', 'F'});
		datGroupEach = new DatFile(Helper.getPathPlayer() + "/03GINF01.DAT", new byte[] {'G', 'P', 'I', 'F'});
		datGroupArtist = new DatFile(Helper.getPathPlayer() + "/03GINF02.DAT", new byte[] {'G', 'P', 'I', 'F'});
		datGroupAlbum = new DatFile(Helper.getPathPlayer() + "/03GINF03.DAT", new byte[] {'G', 'P', 'I', 'F'});
		datGroupGenre = new DatFile(Helper.getPathPlayer() + "/03GINF04.DAT", new byte[] {'G', 'P', 'I', 'F'});
		datGroupArtistAlbum = new DatFile(Helper.getPathPlayer() + "/03GINF2D.DAT", new byte[] {'G', 'P', 'I', 'F'});
		datContentInformation = new DatFile(Helper.getPathPlayer() + "/04CNTINF.DAT", new byte[] {'C', 'N', 'I', 'F'});
		datContentIdList = new DatFile(Helper.getPathPlayer() + "/05CIDLST.DAT", new byte[] {'C', 'I', 'D', 'L'});


		profileEach = loadGinf(idsEach,	datGroupEach);
		treeEach = loadTree(datTreeEach);

		profileAlbum = loadGinf(idsAlbum, datGroupAlbum);
		treeAlbum = loadTree(datTreeAlbum);
		
		{
			DatFile.Block blockGtfb = datTreeInformation.getBlock(0, new byte[] {'G', 'T', 'F', 'B'});
			
			if (0x90 != blockGtfb.getSizeItem())
				throw new Exception("Tree information item length is not 0x90");
			
			byte[] content = blockGtfb.getContent();
			int[] listed = new int[] {0x01, 0x02, 0x03, 0x04, 0x2D};
			for (int i : listed) {
				int offset = 0x90 * (i -1);
				TreeInformation treeInformation = new TreeInformation();
				treeInformation.length = Helper.getInt(content, 0x08 + offset);
				
				if (0x01 != Helper.getShort(content, 0x0c + offset) ||
					0x80 != Helper.getShort(content, 0x0e + offset))
					throw new Exception("Tree information sub item count or size is not 0x05 or 0x80");
				
//				treeInformation.information = new String(content, 0x10 + 0x06 + offset, 0x80 - 0x06, Helper.charsetUTF_16BE).trim();
				
				treeInformations.set(i, treeInformation);
			}
		}
		
		{
			
			DatFile.Block blockCnfb = datContentInformation.getBlock(0, new byte[] {'C', 'N', 'F', 'B'});
			if (0x290 != blockCnfb.getSizeItem())
				throw new Exception("Content information item length is not 0x290");
		
			byte[] content = blockCnfb.getContent();
			for (int i = 0; i < blockCnfb.getCountItem(); i++) {
				int offset = 0x290 * i;
				ContentInformation contentInformation = new ContentInformation();
				contentInformation.magic2 = Helper.getShort(content, 0x02 + offset);
				contentInformation.format = Helper.getInt(content, 0x04 + offset);
				contentInformation.lengthEa3 = Helper.getInt(content, 0x08 + offset);
				
				if (0x05 != Helper.getShort(content, 0x0c + offset) ||
					0x80 != Helper.getShort(content, 0x0e + offset))
					throw new Exception("Content information sub item count or size is not 0x05 or 0x80");
				
				for (int j = 0; j < 5; j++) {
					int subOffset = 0x10 + (0x80 * j) + offset;
					
					if (0x02 != content[0x05 + subOffset])
						throw new Exception ("Invalid encoding");
					
					contentInformation.strings[j] = new String(content, 0x06 + subOffset, 0x80 - 0x06, Helper.charsetUTF_16BE).trim();					
				}
				
				contentInformations.set(i + 1, contentInformation);
			}
		}

		{
			DatFile.Block blockCilb = datContentIdList.getBlock(0, new byte[] {'C', 'I', 'L', 'B'});
			if (0x30 != blockCilb.getSizeItem())
				throw new Exception("CID item length is not 0x30");
		
			byte[] content = blockCilb.getContent();
			for (int i = 0; i < blockCilb.getCountItem(); i++) {
				ContentId contentId = new ContentId();
				System.arraycopy(content, i * 0x30, contentId.cid, 0, contentId.cid.length);
				contentIds.set(i + 1, contentId);
			}
		}
	}
	
	private void loadMusics() throws IOException {
		for (int index = 0; index < 0x10000; index++) {
            if (0 == (index % 0x100))
			{
                // check if directory "OMGAUDIO/10F??/" exists
				File folder = new File(
					String.format("%s/10F%02X", Helper.getPathPlayer(), index >>> 8));
				if(!folder.exists())
					break;
				if(!folder.isDirectory())
					throw new IOException(Helper.INVALID_FORMAT);
				folder = null;
			}
			
			File file = new File(Helper.getPathOma(index));
			if (!file.exists() || !file.isFile())
				continue;

			Oma oma = new Oma(file.getAbsolutePath());
			musics.set(index, oma);
			file = null;
		}
	}
/*	
	public String[][] getGroupInformationStrings(Meta metaGroupInformation) {
		String[][] strings = null;

		if (null == metaGroupInformation) {
			strings = new String[0][];

			return strings;
		}

		Meta.Section section = metaGroupInformation.sections[0];
		int count = section.size();

		strings = new String[count][];
		
		for (int i = 0; i < count; i++) {
			byte[] item = section.getItem(i);

			short countSubItem = Helper.getShort(item, 12);
			short sizeSubItem = Helper.getShort(item, 14);
			strings[i] = new String[countSubItem];
			for (short j = 0; j < countSubItem; j++) {
				try {
					strings[i][j] = new String(
						item,
						0x10 + sizeSubItem * j + 0x06,
						sizeSubItem - 0x06,
						"UTF-16BE").trim();
				} catch (Exception e) {
					strings[i][j] = "Invalid encoded string";
				}
			}
		}
		return strings;
	}
*/
	public void saveMeta() throws Exception {
		System.out.println("Saving dat files");
		
		{
			datGroupList.save();
		}
		
		{
			if (0 == treeEach.sizeNotNull()) {
				datTreeEach.getBlock(0).setContent((short)0, new byte[8], null);
			} else {
				byte[] contentGplb = new byte[0x4000];
				
				byte[] reserved = new byte[8];
				short indexTplb = 1;
				
				short i = 0;
				for (Enumeration e = treeEach.elements(); e.hasMoreElements();) {
					TreeItem treeItem = (TreeItem)e.nextElement();
					if (null == treeItem)
						continue;
					if (null == treeItem.indexes || 0 == treeItem.indexes.sizeNotNull())
						continue;
					
					int offset = 0x08 * i++;
					short indexGroup = (short)treeEach.indexOf(treeItem);
					Helper.setShort(contentGplb, offset + 0x00, indexGroup);
					contentGplb[offset + 0x02] = treeItem.getLevel();
					Helper.setShort(contentGplb, offset + 0x04, indexTplb);
					indexTplb += treeItem.indexes.sizeNotNull();
				}
				Helper.setShort(reserved, 0x02, (short)i);
				for (Enumeration e = treeEach.elements(); e.hasMoreElements();) {
					TreeItem treeItem = (TreeItem)e.nextElement();
					if (null == treeItem)
						continue;
					if (null != treeItem.indexes && 0 != treeItem.indexes.sizeNotNull())
						continue;

					int offset = 0x08 * i++;
					short indexGroup = (short)treeEach.indexOf(treeItem);
					Helper.setShort(contentGplb, offset + 0x00, indexGroup);
					contentGplb[offset + 0x02] = treeItem.getLevel();
					Helper.setShort(contentGplb, offset + 0x04, (short)0x00);
				}
				
				datTreeEach.getBlock(0).setContent((short)i, reserved, contentGplb);

				byte[] contentTplb = new byte[(((indexTplb -1) +7) / 8) * 0x10];
				i = 0;
				for (Enumeration e = treeEach.elements(); e.hasMoreElements();) {
					TreeItem treeItem = (TreeItem)e.nextElement();
					if (null == treeItem)
						continue;
					if (null == treeItem.indexes || 0 == treeItem.indexes.sizeNotNull())
						continue;
					for (Enumeration e2 = treeItem.indexes.elements(); e2.hasMoreElements();) {
						Short id = (Short)e2.nextElement();
						if (null == id)
							continue;
						Helper.setShort(contentTplb, (i++) * 2, id);
					}
				}
				reserved = new byte[8];
				Helper.setShort(reserved, 0x02, (short)(i));
				
				datTreeEach.getBlock(1).setContent((short)(i), reserved, contentTplb);
				
				byte[] contentGpfb = new byte[profileEach.sizeNotNull() * (0x10 + 6 * 0x80)];
				i = 0;
				for (Enumeration e = profileEach.elements(); e.hasMoreElements();) {
					Profile profile = (Profile)e.nextElement();
					if (null == profile)
						continue;
					int offset = i++ * (0x10 + 6 * 0x80);
					Helper.setInt(contentGpfb, offset + 0x08, profile.lengthEa3);
					Helper.setShort(contentGpfb, offset + 0x0c, (short)0x06);
					Helper.setShort(contentGpfb, offset + 0x0e, (short)0x80);
					System.arraycopy(new byte[] {'T', 'I', 'T', '2', 0x00, 0x02}, 0, contentGpfb, offset + 0x10 + 0 * 0x80, 6);
					System.arraycopy(new byte[] {'T', 'P', 'E', '1', 0x00, 0x02}, 0, contentGpfb, offset + 0x10 + 1 * 0x80, 6);
					System.arraycopy(new byte[] {'T', 'C', 'O', 'N', 0x00, 0x02}, 0, contentGpfb, offset + 0x10 + 2 * 0x80, 6);
					System.arraycopy(new byte[] {'T', 'S', 'O', 'P', 0x00, 0x02}, 0, contentGpfb, offset + 0x10 + 3 * 0x80, 6);
					System.arraycopy(new byte[] {'P', 'I', 'C', 'P', 0x00, 0x02}, 0, contentGpfb, offset + 0x10 + 4 * 0x80, 6);
					System.arraycopy(new byte[] {'P', 'I', 'C', '0', 0x00, 0x02}, 0, contentGpfb, offset + 0x10 + 5 * 0x80, 6);
					for (int j = 0; j < profile.strings.length; j++) {
						int subOffset = offset + 0x10 + j * 0x80;
						String text = profile.strings[j];

						byte[] byteText = text.getBytes(Helper.charsetUTF_16BE); 
						System.arraycopy(byteText, 0, contentGpfb, subOffset + 0x06, java.lang.Math.min(byteText.length, 0x80 - 6));
					}
				}
				datGroupEach.getBlock(0).setContent((short)(i), null, contentGpfb);;
			}
			datTreeEach.save();
			datGroupEach.save();
		}
		
		{
			if (0 == treeAlbum.sizeNotNull()) {
				datTreeAlbum.getBlock(0).setContent((short)0, new byte[8], null);
			} else {
				byte[] contentGplb = new byte[0x4000];
				
				byte[] reserved = new byte[8];
				short indexTplb = 1;
				
				short i = 0;
				for (Enumeration e = treeAlbum.elements(); e.hasMoreElements();) {
					TreeItem treeItem = (TreeItem)e.nextElement();
					if (null == treeItem)
						continue;
					if (null == treeItem.indexes || 0 == treeItem.indexes.sizeNotNull())
						continue;
					
					int offset = 0x08 * i++;
					short indexGroup = (short)treeAlbum.indexOf(treeItem);
					Helper.setShort(contentGplb, offset + 0x00, indexGroup);
					contentGplb[offset + 0x02] = treeItem.getLevel();
					Helper.setShort(contentGplb, offset + 0x04, indexTplb);
					indexTplb += treeItem.indexes.sizeNotNull();
				}
				Helper.setShort(reserved, 0x02, (short)i);
				for (Enumeration e = treeAlbum.elements(); e.hasMoreElements();) {
					TreeItem treeItem = (TreeItem)e.nextElement();
					if (null == treeItem)
						continue;
					if (null != treeItem.indexes && 0 != treeItem.indexes.sizeNotNull())
						continue;

					int offset = 0x08 * i++;
					short indexGroup = (short)treeAlbum.indexOf(treeItem);
					Helper.setShort(contentGplb, offset + 0x00, indexGroup);
					contentGplb[offset + 0x02] = treeItem.getLevel();
					Helper.setShort(contentGplb, offset + 0x04, (short)0x00);
				}
				
				datTreeAlbum.getBlock(0).setContent((short)(i), reserved, contentGplb);

				byte[] contentTplb = new byte[(((indexTplb -1) +7) / 8) * 0x10];
				i = 0;
				for (Enumeration e = treeAlbum.elements(); e.hasMoreElements();) {
					TreeItem treeItem = (TreeItem)e.nextElement();
					if (null == treeItem)
						continue;
					if (null == treeItem.indexes || 0 == treeItem.indexes.sizeNotNull())
						continue;
					for (Enumeration e2 = treeItem.indexes.elements(); e2.hasMoreElements();) {
						Short id = (Short)e2.nextElement();
						if (null == id)
							continue;
						Helper.setShort(contentTplb, (i++) * 2, id);
					}
				}
				reserved = new byte[8];
				Helper.setShort(reserved, 0x02, (short)(i));
				
				datTreeAlbum.getBlock(1).setContent((short)(i), reserved, contentTplb);
				
				byte[] contentGpfb = new byte[profileAlbum.sizeNotNull() * (0x10 + 1 * 0x80)];
				i = 0;
				for (Enumeration e = profileAlbum.elements(); e.hasMoreElements();) {
					Profile profile = (Profile)e.nextElement();
					if (null == profile)
						continue;
					
					int offset = i++ * (0x10 + 1 * 0x80);
					Helper.setInt(contentGpfb, offset + 0x08, profile.lengthEa3);
					Helper.setShort(contentGpfb, offset + 0x0c, (short)0x01);
					Helper.setShort(contentGpfb, offset + 0x0e, (short)0x80);
					System.arraycopy(new byte[] {'T', 'I', 'T', '2', 0x00, 0x02}, 0, contentGpfb, offset + 0x10, 6);
					assert 1 == profile.strings.length;
					for (int j = 0; j < profile.strings.length; j++) {
						int subOffset = offset + 0x10 + j * 0x80;
						String text = profile.strings[j];

						byte[] byteText = text.getBytes(Helper.charsetUTF_16BE); 
						System.arraycopy(byteText, 0, contentGpfb, subOffset + 0x06, java.lang.Math.min(byteText.length, 0x80 - 6));
					}
				}
				datGroupAlbum.getBlock(0).setContent((short)(i), null, contentGpfb);;
			}
			datTreeAlbum.save();
			datGroupAlbum.save();
		}
		
		{
			DatFile.Block blockGtfb = datTreeInformation.getBlock(0);
			
			byte[] content = blockGtfb.getContent();
			int[] listed = new int[] {0x01, 0x02, 0x03, 0x04, 0x2D};
			for (Enumeration e = treeInformations.elements(); e.hasMoreElements();) {
				TreeInformation treeInformation = (TreeInformation)e.nextElement();
				if (null == treeInformation)
					continue;
				int index = treeInformations.indexOf(treeInformation);
				Helper.setInt(content, 0x08 + 0x90 * (index -1), treeInformation.length);
			}

			datTreeInformation.save();
		}
		
		{
			byte[] content = new byte[contentInformations.sizeNotNull() *(0x10 + 5 * 0x80)];
			int offset = 0;
			for (Enumeration e = contentInformations.elements(); e.hasMoreElements();) {
				ContentInformation contentInformation = (ContentInformation)e.nextElement();
				if (null == contentInformation)
					continue;
				
				Helper.setShort(content, offset + 0x00, contentInformation.magic1);
				Helper.setShort(content, offset + 0x02, contentInformation.magic2);
				Helper.setInt(content, offset + 0x04, contentInformation.format);
				Helper.setInt(content, offset + 0x08, contentInformation.lengthEa3);
				Helper.setShort(content, offset + 0x0c, (short)0x05);
				Helper.setShort(content, offset + 0x0e, (short)0x80);
				System.arraycopy(new byte[] {'T', 'I', 'T', '2', 0x00, 0x02}, 0, content, offset + 0x10 + 0 * 0x80, 6);
				System.arraycopy(new byte[] {'T', 'P', 'E', '1', 0x00, 0x02}, 0, content, offset + 0x10 + 1 * 0x80, 6);
				System.arraycopy(new byte[] {'T', 'A', 'L', 'B', 0x00, 0x02}, 0, content, offset + 0x10 + 2 * 0x80, 6);
				System.arraycopy(new byte[] {'T', 'C', 'O', 'N', 0x00, 0x02}, 0, content, offset + 0x10 + 3 * 0x80, 6);
				System.arraycopy(new byte[] {'T', 'S', 'O', 'P', 0x00, 0x02}, 0, content, offset + 0x10 + 4 * 0x80, 6);
				
				for (int i = 0; i < 0x05; i++) {
					byte[] stringItem = contentInformation.strings[i].getBytes(Helper.charsetUTF_16BE);
					System.arraycopy(
						stringItem,
						0,
						content,
						offset + 0x10 + i * 0x80 + 0x06,
						java.lang.Math.min(0x80 - 0x06, stringItem.length));
					
				}
				
				offset += 0x10 + 5 * 0x80;
			}
			
			datContentInformation.getBlock(0).setContent((short)contentInformations.sizeNotNull(), new byte[8], content);
			datContentInformation.save();
		}
		
		{
			byte[] content = new byte[contentIds.sizeNotNull() * 0x30];
			int offset = 0;
			for (Enumeration e = contentIds.elements(); e.hasMoreElements();) {
				ContentId contentId = (ContentId)e.nextElement();
				if (null == contentId)
					continue;
				
				System.arraycopy(contentId.cid, 0, content, offset, contentId.cid.length);
				offset += 0x30;
			}
			datContentIdList.getBlock(0).setContent((short)contentIds.sizeNotNull(), new byte[8], content);
			datContentIdList.save();
		}
	}
	
	public void addMusic(String filename) throws Exception{
		File file = new File(filename);
		
		if (!file.exists()) {
//			throw new FileNotFoundException(filename);
			Helper.printUtf8("File '" + filename + "' is not found. Check filename\n");
			return;
		}
		
		if (file.isDirectory()) {
			String[] filesInDirectory = file.list();
			for (String fileInDirectory : filesInDirectory) {
				addMusic(filename + "/" + fileInDirectory);
			}
		}
		
		if(!filename.toLowerCase().endsWith(".mp3")) {
			return;
		}
		
		// now, we assume this file is mp3
		short index = (short)musics.indexNull(1);
		String filepathOma = Helper.getPathOma(index);

		{
			// if directory is missing, create it.
			File fileOma = new File(filepathOma);
			if(!fileOma.getParentFile().exists()) {
				fileOma.getParentFile().mkdir();
			}
		}

		Oma oma;
		try {
			Helper.printUtf8("Adding : " + filename + " ... ");
			oma = new Oma(filepathOma);
			oma.convertMp3ToOma(filename);
			System.out.println("SUCCESS");
		} catch (Exception e) {
			e.printStackTrace();
//			Helper.printUtf8("skipping '" + filename + "' for " + filepathOma + "\n");
			System.out.println("FAILED");
			
			File fileOma = new File(filepathOma);
			if (fileOma.exists()) {
				fileOma.delete();
			}

			return;
		}
		
		// add to meta
		musics.set(index, oma);
		addMusicToMeta(index, oma.getMeta());
	}
	
	public void removeMusic(short index) throws Exception {
		String filepathOma = Helper.getPathOma(index);

		File fileOma = new File(filepathOma);
		if (!fileOma.exists() || !fileOma.isFile())
			throw new Exception("File not found : " + filepathOma);
		
		removeMusicFromMeta(index);
		
		fileOma.delete();
		Helper.printUtf8("Removed : " + filepathOma + "\n");
	}
	
	private void addMusicToMeta(short index, Oma.Meta meta) throws Exception {
		{
			byte[] content = datGroupList.getBlock(1).getContent();
			content[(0x01 - 1) * 0x50 + 0x11] = 1; // each
			content[(0x02 - 1) * 0x50 + 0x11] = 1; // disable artist for now
			content[(0x03 - 1) * 0x50 + 0x11] = 1; // album
			content[(0x04 - 1) * 0x50 + 0x11] = 1; // disable genre for now
			content[(0x06 - 1) * 0x50 + 0x11] = 2; // disable artist-album for now
		}
		
		{
			short indexGpfbHole = -1;
			boolean added = false;
			for (Enumeration e = treeEach.elements(); e.hasMoreElements();) {
				TreeItem treeItem = (TreeItem)e.nextElement();
				if (null == treeItem)
					continue;
				short indexGpfb = (short)treeEach.indexOf(treeItem);

				if (0 == treeItem.getLevel()) {
					if (-1 == indexGpfbHole) {
						indexGpfbHole = indexGpfb;
					}
					continue;
				}
				
				Profile profile = profileEach.get(indexGpfb);
				
				if (profile.strings[0].equals(meta.album) &&
					profile.strings[1].equals(meta.artist) &&
					profile.strings[2].equals(meta.genre)) {
					// set here
					profile.lengthEa3 += meta.lengthEa3;
					treeItem.indexes.add(index);
					added = true;
					break;
				}
			}
			if (false == added) {
				if (-1 != indexGpfbHole) {
					TreeItem treeItem = treeEach.get(indexGpfbHole);
					// set here
					treeItem.indexes = new Vector<Short>();
					treeItem.indexes.add(index);

					Profile profile = profileEach.get(indexGpfbHole);
					profile.lengthEa3 = meta.lengthEa3;
					profile.strings[0] = meta.album;
					profile.strings[1] = meta.artist;
					profile.strings[2] = meta.genre;
					profile.strings[3] = "";
					profile.strings[4] = "";
					profile.strings[5] = "";
				} else {
					Profile profileNew = new Profile(6);

					profileNew.lengthEa3 = meta.lengthEa3;
					profileNew.strings[0] = meta.album;
					profileNew.strings[1] = meta.artist;
					profileNew.strings[2] = meta.genre;
					profileNew.strings[3] = "";
					profileNew.strings[4] = "";
					profileNew.strings[5] = "";

					if (0 == profileEach.sizeNotNull()) {
						profileEach.set(1, profileNew);
					} else {
						profileEach.add(profileNew);
					}

					short indexGpfbNew = (short)(profileEach.indexOf(profileNew));

					TreeItem treeItemNew = new TreeItem();
					treeItemNew.indexes = new Vector<Short>();
					treeItemNew.indexes.add(index);

					treeEach.set(indexGpfbNew, treeItemNew);
				}
			}
		}
		

		{
			short indexGpfbHole = -1;
			boolean added = false;
			for (Enumeration e = treeAlbum.elements(); e.hasMoreElements();) {
				TreeItem treeItem = (TreeItem)e.nextElement();
				if (null == treeItem)
					continue;
				short indexGpfb = (short)treeAlbum.indexOf(treeItem);

				if (0 == treeItem.getLevel()) {
					if (-1 == indexGpfbHole) {
						indexGpfbHole = indexGpfb;
					}
					continue;
				}
				
				Profile profile = profileAlbum.get(indexGpfb);
				
				if (profile.strings[0].equals(meta.album)) {
					// set here
					profile.lengthEa3 += meta.lengthEa3;
					treeItem.indexes.add(index);
					added = true;
					break;
				}
			}

			if (false == added) {
				if (-1 != indexGpfbHole) {
					TreeItem treeItem = treeAlbum.get(indexGpfbHole);
					// set here
					treeItem.indexes = new Vector<Short>();
					treeItem.indexes.add(index);

					Profile profile = profileAlbum.get(indexGpfbHole);
					profile.lengthEa3 = meta.lengthEa3;
					profile.strings[0] = meta.album;
				} else {
					// add to tail
					Profile profileNew = new Profile(1);

					profileNew.lengthEa3 = meta.lengthEa3;
					profileNew.strings[0] = meta.album;

					if (0 == profileAlbum.sizeNotNull()) {
						profileAlbum.set(1, profileNew);
					} else {
						profileAlbum.add(profileNew);
					}

					short indexGpfbNew = (short)(profileAlbum.indexOf(profileNew));

					TreeItem treeItemNew = new TreeItem();
					treeItemNew.indexes = new Vector<Short>();
					treeItemNew.indexes.add(index);

					treeAlbum.set(indexGpfbNew, treeItemNew);
				}
			}
		}
		
		{
			for (Enumeration e = treeInformations.elements(); e.hasMoreElements();) {
				TreeInformation treeInformation = (TreeInformation)e.nextElement();
				if (null == treeInformation)
					continue;
				treeInformation.length += meta.lengthEa3;
			}
		}
		
		{
			ContentInformation contentInformation = contentInformations.get(index);
			if (null == contentInformation) {
				contentInformation = new ContentInformation();
				contentInformations.set(index, contentInformation);
			}
			contentInformation.magic2 = (short)0xffff;
			contentInformation.format = meta.formatEa3;
			contentInformation.lengthEa3 = meta.lengthEa3;
			contentInformation.strings[0] = meta.title;
			contentInformation.strings[1] = meta.artist;
			contentInformation.strings[2] = meta.album;
			contentInformation.strings[3] = meta.genre;
			contentInformation.strings[4] = "";
		}
		
		{
			ContentId contentId = contentIds.get(index);
			if (null == contentId) {
				contentId = new ContentId();
				contentIds.set(index, contentId);
			}
			System.arraycopy(meta.cidEa3, 0, contentId.cid, 0, meta.cidEa3.length);
		}
	}

	private void removeMusicFromMeta(short index) throws Exception {
		Oma.Meta meta = null;
		ContentInformation contentInformation = contentInformations.get(index);
		{
			// just leave it for now
			byte[] content = datGroupList.getBlock(1).getContent();
			content[(0x01 - 1) * 0x50 + 0x11] = 1; // each
			content[(0x02 - 1) * 0x50 + 0x11] = 1; // disable artist for now
			content[(0x03 - 1) * 0x50 + 0x11] = 1; // album
			content[(0x04 - 1) * 0x50 + 0x11] = 1; // disable genre for now
			content[(0x06 - 1) * 0x50 + 0x11] = 2; // disable artist-album for now
		}
		
		{
			find_music:
				for (Enumeration e = treeEach.elements(); e.hasMoreElements();) {
					TreeItem treeItem = (TreeItem)e.nextElement();
					if (null == treeItem ||
						null == treeItem.indexes ||
						0 == treeItem.indexes.sizeNotNull())
						continue;
					
					for (Enumeration e2 = treeItem.indexes.elements(); e2.hasMoreElements();) {
						Short indexMusic = (Short)e2.nextElement();
						if (null == indexMusic)
							continue;
						
						if (!indexMusic.equals(index))
							continue;
						treeItem.indexes.set(treeItem.indexes.indexOf(indexMusic), null);
						int indexProfile = treeEach.indexOf(treeItem);
						Profile profile = profileEach.get(indexProfile);
						if (profile.lengthEa3 >= contentInformation.lengthEa3)
							profile.lengthEa3 -= contentInformation.lengthEa3;
						else
							profile.lengthEa3 = 0;
						break find_music;
					}
					
				}
		}
		{
			find_music:
				for (Enumeration e = treeAlbum.elements(); e.hasMoreElements();) {
					TreeItem treeItem = (TreeItem)e.nextElement();
					if (null == treeItem ||
						null == treeItem.indexes ||
						0 == treeItem.indexes.sizeNotNull())
						continue;
					
					for (Enumeration e2 = treeItem.indexes.elements(); e2.hasMoreElements();) {
						Short indexMusic = (Short)e2.nextElement();
						if (null == indexMusic)
							continue;
						
						if (!indexMusic.equals(index))
							continue;
						treeItem.indexes.set(treeItem.indexes.indexOf(indexMusic), null);
						int indexProfile = treeAlbum.indexOf(treeItem);
						Profile profile = profileAlbum.get(indexProfile);
						if (profile.lengthEa3 >= contentInformation.lengthEa3)
							profile.lengthEa3 -= contentInformation.lengthEa3;
						else
							profile.lengthEa3 = 0;
						break find_music;
					}
					
				}
		}

		
		{
			for (Enumeration e = treeInformations.elements(); e.hasMoreElements();) {
				TreeInformation treeInformation = (TreeInformation)e.nextElement();
				if (null == treeInformation)
					continue;
				treeInformation.length -= contentInformation.lengthEa3;
			}
		}
		
		{
			// nothing to do with content contentInformations
		}
		
		{
			// nothing to do with content contentIds
		}
	}

/*	
	public String[][] getFullInformation() {
		return getGroupInformationStrings(meta03GINF01);
	}
	
	public String[][] getArtists() {
		return getGroupInformationStrings(meta03GINF02);
	}
	
	public String[][] getAlbums() {
		return getGroupInformationStrings(meta03GINF03);
	}
	
	public String[][] getGenres() {
		return getGroupInformationStrings(meta03GINF04);
	}
*/
	public void list(String groupType) {
		if (groupType.equals("album")) {
			for (Enumeration e = treeAlbum.elements(); e.hasMoreElements();) {
				TreeItem treeItem = (TreeItem)e.nextElement();
				if (null == treeItem || Vector.isEmpty(treeItem.indexes))
					continue;
				
				int indexProfile = treeAlbum.indexOf(treeItem);
				Profile profile = profileAlbum.get(indexProfile);
				
				System.out.printf("\n[Group 0X%04X : ", indexProfile);
				Helper.printUtf8(profile.strings[0] + "]\n");
				
				if(null == treeItem.indexes)
					continue;
				
				for (Enumeration e2 = treeItem.indexes.elements(); e2.hasMoreElements();) {
					Short indexMusic = (Short)e2.nextElement();
					if (null == indexMusic)
						continue;
					
					ContentInformation contentInformation = contentInformations.get(indexMusic);
					if (null == contentInformation) {
						System.out.println("");
					}
					System.out.printf("1000%04X.OMA(%05d) : ", indexMusic, indexMusic);
					Helper.printUtf8(contentInformation.strings[0] + "\n");
				}
			}
		}
	}
/*	
	public void dump() {
		System.out.println("-- Musics --\n");
		for (Enumeration e = musics.elements(); e.hasMoreElements();) {
			Oma oma = (Oma)e.nextElement();
			if (null == oma)
				continue;
			
			System.out.printf("OMAFile %04X : ", musics.indexOf(oma));
			Helper.printUtf8(oma.getTitle());
			System.out.println("");
		}

		System.out.println("-- Albums --\n");
		String[][] albums = getAlbums();
		for(String[] album : albums) {
			for(String item : album) {
				Helper.printUtf8(item);
				System.out.printf(" / ");
			}
			System.out.println("");
		}
		albums = null;

		System.out.println("-- Artist --\n");
		String[][] artists = getArtists();
		for(String[] artist : artists) {
			for(String item : artist) {
				Helper.printUtf8(item);
				System.out.printf(" / ");
			}
			System.out.println("");
		}
		artists = null;

		System.out.println("-- Genres --\n");
		String[][] genres = getGenres();
		for(String[] genre : genres) {
			for(String item : genre) {
				Helper.printUtf8(item);
				System.out.printf(" / ");
			}
			System.out.println("");
		}
		genres = null;
		
		System.out.println("-- Full Informations --\n");
		String[][] informations = getFullInformation();
		for(String[] information : informations) {
			for(String item : information) {
				Helper.printUtf8(item);
				System.out.printf(" / ");
			}
			System.out.println("");
		}
		informations = null;
	}
	*/
	private static void usage(String message) {
		if (null != message) {
			System.out.println(message + "\n");
		}
		
		usage();
	}
	private static void usage() {
		System.out.println(
			"javastage v0.05\n" +
			"\n" +
			"Usage : [OPTION]... command\n" +
			"\n" +
			"commands\n" +	
			"add [filenames | directory] ...      : add music files into player\n" +
			"remove [id] ...                      : remove music from player\n" +
			"list album | artist | genre          : list music grouped by type\n" +
			"init                                 : initialize database\n" +
			"\n" +
			"options\n" +
			"-pOMGAUDIO_PATH                      : use OMGAUDIO_PATH as player path\n" +
			"-d                                   : use directory name as album name\n" +
			"-f                                   : use file name as title\n" +
			"-aALBUM_NAME                         : use ALBUM_NAME as album name\n"
			);
	}
	
	public static void main(final String args[]) {
		String pathOmgAudio = new File("OMGAUDIO").getAbsolutePath();
		java.util.LinkedList<String> listParams = new java.util.LinkedList<String>();
		Manager manager;
		
		String command = null;

		for (String arg : args) {
			if (arg.startsWith("-p")) {
				// path
				pathOmgAudio = arg.substring(2);
			} else if (arg.startsWith("-d")) {
				// use directory as album
				Helper.useDirectoryAsAlbum = true;
			} else if (arg.startsWith("-a")) {
				// enforce album name
				Helper.enforcedAlbumName = arg.substring(2);
			} else if (arg.startsWith("-f")) {
				// use filename as title
				Helper.useFilenameAsTitle = true;
			} else if (arg.startsWith("-")) {
				// unknown option
				usage();
				return;
			} else if (null != command) {
				listParams.add(arg);
			} else if (
				arg.equalsIgnoreCase("add") || 
				arg.equalsIgnoreCase("remove") || 
				arg.equalsIgnoreCase("list") || 
				arg.equalsIgnoreCase("init")) {
				// add music files
				command = arg.toLowerCase();
			} else {
				usage();
				return;
			}
		}
		
		if (null == command)
		{
			usage("Command required");
			return;
		}

		try {
			manager = new Manager(pathOmgAudio);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		if (command.equalsIgnoreCase("add")) {
			while (0 != listParams.size()) {
				try {
					manager.addMusic(listParams.poll());
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			try {
				manager.saveMeta();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (command.equalsIgnoreCase("remove")) {
			while (0 != listParams.size()) {
				try {
					short index = Short.decode(listParams.poll());
					manager.removeMusic(index);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			try {
				manager.saveMeta();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (command.equalsIgnoreCase("list")) {
/*			if (1 != listParams.size()) {
				usage();
				return;
			}
*/			String param = listParams.poll();
			
			if (param.equals("album")) {
				manager.list(param);
			} else {
				System.out.println(param + "Not supported yet");
			}
		}
	}
}
