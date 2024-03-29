package ru.bhms.bfctool;

import java.io.*;
import java.util.Scanner;

public class BFCTool {
	public static final String MAIN_DIR = "C:/Program Files/BFCTool/";
	public static final String BFC_DIR = MAIN_DIR + "bfc/";
	public static final String EXT_DIR = MAIN_DIR + "ext_bfc/";
	public static final int[] ENCODE_TABLE = createTable(4129);
	public static final String BFC_FILE_FORMAT = ".bfc";
	public static final String[] CFG_KEYS = new String[] {
		"NUM_FILES",
		"SHORT_FN",
		"RAM_STATE",
		"LOCATION",
		"PATH"
	};
	public static short[] filenames;
	
	private static final void welcomeScene() {
		System.out.println("BFCTool v0.4 Alpha. Early access.\n"
				+ "\n"
				+ "Welcome amigo!\n"
				+ "Thank you for testing this program.\n");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		welcomeScene();
		File dir;
		if(!(dir = new File(MAIN_DIR)).exists()) dir.mkdir();
		if(!(dir = new File(BFC_DIR)).exists()) dir.mkdir();
		if(!(dir = new File(EXT_DIR)).exists()) dir.mkdir();
		final Scanner INPUT = new Scanner(System.in);
		String command = null;
		do {
			System.out.print("BFCTool> ");
			try {
				String keyop = getValueop((command = INPUT.nextLine()),false);
				if(keyop.equals("extract")) {
					command(0,null);
				} else if(keyop.equals("save")) {
					command(1,null);
				} else if(keyop.equals("updatefn")) {
					command(2,null);
				} else if(keyop.equals("findfn")) {
					command(3,getValueop(command,true));
				} else if(keyop.equals("find")) {
					command(4,getValueop(command,true));
				} else if(keyop.equals("encodefn")) {
					command(5,getValueop(command,true));
				} else if(keyop.equals("addbfc")) {
					command(6,getValueop(command,true));
				} else if(keyop.equals("exit")) {
					command(-1,null);
				} else {
					command(-2,null);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		} while(!command.equals("exit"));
		INPUT.close();
	}
	
	private static final int[] createTable(int xor) {
		int[] table = new int[256];
		for(int i = 0;i < 256;i++) {
			int set = 0;
			int x = i << 8;
			
			for(int k = 0;k < 8;k++) {
				if(((set ^ x) & 0x8000) != 0) {
					set = set << 1 ^ xor;
				} else {
					set <<= 1;
				}
				
				x <<= 1;
				set &= 0xffff;
			}
			
			table[i] = set;
		}
		
		return table;
	}
	
	public static short encodeFilename(String filename) {
		int begin = 0xffff;
		
		for(int sym = 0;sym < filename.length();sym++) {
			char ch;
			byte bch = (byte)((ch = filename.charAt(sym)) >> 8);
			begin = (ENCODE_TABLE[(bch ^ begin >> 8) & 0xff] ^ begin << 8) & 0xffff;
			bch = (byte)(ch & 0xff);
			begin = (ENCODE_TABLE[(bch ^ begin >> 8) & 0xff] ^ begin << 8) & 0xffff;
		}
		
		return (short)(begin & 0xffff);
	}
	
	private static String getValueop(String src,boolean isValue) {
		String result = null;
		if(isValue) {
			StringBuilder build = new StringBuilder();
			char ch;
			int spaceIndex = 0;
			for(int index = 0;(ch = src.charAt(index)) != ' ';index++) {
				spaceIndex = index;
			}
			for(int index = spaceIndex + 2;index < src.length();index++) {
				build.append(src.charAt(index));
			}
			result = build.toString();
		} else {
			StringBuilder build = new StringBuilder();
			try {
				char ch;
				for(int index = 0;(ch = src.charAt(index)) != ' ';index++) {
					build.append(ch);
				}
			} catch(StringIndexOutOfBoundsException e) {
			}
			result = build.toString();
		}
		return result;
	}
	
	public static int getFileIndex(short filename,short[] filenames) {
		for(int index = 0;index < filenames.length;index++) {
			if(filenames[index] == filename) return index;
		}
		
		return -1;
	}
	
	private static void command(int command,String valueop) throws Exception {
		switch(command) {
			case 0: {
				DataInputStream is = new DataInputStream(new FileInputStream(MAIN_DIR + "head" + BFC_FILE_FORMAT));
				PrintWriter out = new PrintWriter(new FileOutputStream(MAIN_DIR + "head.cfg"));
				int numFiles = is.readUnsignedShort();
				out.println(CFG_KEYS[0] + "=" + Integer.toString(numFiles));
				out.println();
				for(int fileIndex = 0;fileIndex < numFiles;fileIndex++) {
					out.println(CFG_KEYS[1] + "=" + is.readShort());
					out.println(CFG_KEYS[2] + "=" + Integer.toString(is.readByte()));
					int offset = is.readUnsignedByte() << 16 | is.readUnsignedByte() << 8 | is.readUnsignedByte();
					out.println("#OFFSET=" + Integer.toString(offset));
					byte location = is.readByte();
					out.println(CFG_KEYS[3] + "=" + location);
					int size = is.readUnsignedByte() << 16 | is.readUnsignedByte() << 8 | is.readUnsignedByte();
					out.println("#SIZE=" + Integer.toString(size));
					File bfcDir;
					StringBuilder path = new StringBuilder();
					path.append(location);
					path.append(BFC_FILE_FORMAT + "/");
					if(!(bfcDir = new File(EXT_DIR + path.toString())).exists()) bfcDir.mkdir();
					path.append(fileIndex);
					path.append(".bin");
					System.out.println("[" + fileIndex + " " + ((fileIndex + 1) * 100 / numFiles) + "%] - Extracting file: " + path);
					OutputStream binOut = new FileOutputStream(EXT_DIR + path.toString());
					InputStream bfcIn = new FileInputStream(BFC_DIR + location + BFC_FILE_FORMAT);
					bfcIn.skip(offset);
					byte[] storage = new byte[size];
					bfcIn.read(storage);
					bfcIn.close();
					binOut.write(storage);
					out.println(CFG_KEYS[4] + "=" + path.toString());
					out.println();
					binOut.close();
				}
				out.close();
				is.close();
				break;
			}
			
			case 1: {
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(MAIN_DIR + "head.cfg")));
				String hdrItem;
				InputStream binIn;
				int numFiles = 0;
				while((hdrItem = in.readLine()).length() != 0) {
					if(hdrItem.charAt(0) == '#') {
					} else {
						if(get(hdrItem,false).equals(CFG_KEYS[0])) {
							numFiles = Integer.parseInt(get(hdrItem,true));
						} else {
							System.err.println("[WARNING]: Found unknown key name in the configuration header, it's been ignored. Use '@' annotation symbol before configuration item to forcibly ignore a item.");
						}
					}
				}
				short[] filenames = new short[numFiles];
				byte[] types = new byte[numFiles];
				int[] offsets = new int[numFiles];
				byte[] locations = new byte[numFiles];
				int[] sizes = new int[numFiles];
				String[] paths = new String[numFiles];
				try {
					for(int i = 0;i < numFiles;i++) {
						String item;
						while((item = in.readLine()).length() != 0) {
							if(item.charAt(0) != '#') {
								String key = get(item,false);
								String value = get(item,true);
								if(key.equals(CFG_KEYS[1])) filenames[i] = (short)Integer.parseInt(value);
								else if(key.equals(CFG_KEYS[2])) types[i] = (byte)Integer.parseInt(value);
								else if(key.equals(CFG_KEYS[3])) locations[i] = (byte)Integer.parseInt(value);
								else if(key.equals(CFG_KEYS[4])) paths[i] = value;
								else System.err.println("[WARNING]: Found unknown key name in the configuration body, it's been ignored. Use '#' before configuration item, to completely ignore a item.");
							}
						}
					}
				} catch(EOFException e) {
					System.err.println("Unexpected end of file (head.cfg).");
				} catch(NullPointerException e) {
					System.err.println("Unexpected null pointer exception (head.cfg).");
				}
				in.close();
				for(int i = 0;i < numFiles;i++) {
					sizes[i] = (int)new File(EXT_DIR + paths[i]).length();
				}
				offsets = fixOffsets(numFiles,sizes,locations);
				constructHead(numFiles,filenames,types,offsets,locations,sizes);
				OutputStream[] bfc = new OutputStream[maxLocation(locations) + 1];
				for(int i = 0;i < numFiles;i++) {
					System.out.println("[" + i + " " + ((i + 1) * 100 / numFiles) + "%] - Saving file: " + paths[i]);
					int loc = locations[i];
					if(bfc[loc] == null) bfc[loc] = new FileOutputStream(BFC_DIR + loc + BFC_FILE_FORMAT);
					byte[] data = new byte[sizes[i]];
					binIn = new FileInputStream(EXT_DIR + paths[i]);
					binIn.read(data);
					binIn.close();
					bfc[loc].write(data);
				}
				closeStreams(bfc);
				System.out.println("Written " + numFiles + " files successfully!");
				break;
			}
			
			case 2: {
				DataInputStream head = new DataInputStream(new FileInputStream(MAIN_DIR + "head" + BFC_FILE_FORMAT));
				filenames = new short[head.readUnsignedShort()];
				for(int file = 0;file < filenames.length;file++) {
					filenames[file] = head.readShort();
					head.skip(8);
				}
				head.close();
				System.out.println("Filenames updated successfully!");
				break;
			}
			
			case 3: {
				command(4,Integer.toString(encodeFilename(valueop)));
				break;
			}
			
			case 4: {
				if(filenames == null) {
					command(2,null);
				}
				System.out.println("Index: " + getFileIndex((short)Integer.parseInt(valueop),filenames));
				break;
			}
			
			case 5: {
				System.out.println("Encode: " + encodeFilename(valueop));
				break;
			}
			
			case 6: {
				DataInputStream head = new DataInputStream(new FileInputStream(MAIN_DIR + "head" + BFC_FILE_FORMAT));
				int numFiles;
				short[] filenames = new short[(numFiles = head.readUnsignedShort())];
				byte[] types = new byte[numFiles];
				int[] offsets = new int[numFiles];
				byte[] locations = new byte[numFiles];
				int[] sizes = new int[numFiles];
				for(int i = 0;i < numFiles;i++) {
					filenames[i] = head.readShort();
					types[i] = head.readByte();
					offsets[i] = head.readUnsignedByte() << 16 | head.readUnsignedByte() << 8 | head.readUnsignedByte();
					locations[i] = head.readByte();
					sizes[i] = head.readUnsignedByte() << 16 | head.readUnsignedByte() << 8 | head.readUnsignedByte();
				}
				head.close();
				int op = Integer.parseInt(valueop);
				for(int i = 0;i < numFiles;i++) {
					if(locations[i] >= op) {
						locations[i]++;
					}
				}
				constructHead(numFiles,filenames,types,offsets,locations,sizes);
				File[] bfcs = new File[maxLocation(locations)];
				for(int i = 0;i < bfcs.length;i++) {
					bfcs[i] = new File(BFC_DIR + i + BFC_FILE_FORMAT);
				}
				for(int i = bfcs.length - 1;i >= 0;i--) {
					String name = bfcs[i].getName();
					int num = Integer.parseInt(removeExtension(name));
					if(op <= num) {
						bfcs[i].renameTo(new File(BFC_DIR + (num + 1) + BFC_FILE_FORMAT));
					}
				}
				new File(BFC_DIR + op + BFC_FILE_FORMAT).createNewFile();
				System.out.println(valueop + BFC_FILE_FORMAT + " successfully added to head.bfc and between BFCs.");
				break;
			}
			
			case -1: break;
			
			default: 
				System.err.println("[ERROR]: Unknown command or command with parameters.\n\n");
				System.err.println("Command value should be:\n"
						+ "   extract                         - extract all files from BFCs and create head.cfg (requires head.bfc in the program directory);\n"
						+ "   save                            - save files to BFCs (requires head.cfg in the program directory);\n"
						+ "   updatefn                        - load filenames from head.bfc to memory or update old filenames (requires head.bfc in the program directory);\n"
						+ "   exit                            - terminate the program.\n"
						+ "\n"
						+ "Command with parameters should be:\n"
						+ "   findfn <filename>               - find a specified filename in all BFCs and return index of file from zero;\n"
						+ "   find <short_int_filename>       - find a specified short int filename in all BFCs and return index of file from zero;\n"
						+ "   encodefn <filename>             - encode a filename into a short int value;\n"
						+ "   addbfc <new_number>             - add a new BFC file with specified number and shift other BFCs.");
		}
		System.out.println();
	}
	
	public static String removeExtension(String file) {
		StringBuilder bfcNum = new StringBuilder();
		char ch = 0;
		for(int index = 0;(ch = file.charAt(index)) != '.';index++) {
			bfcNum.append(ch);
		}
		return bfcNum.toString();
	}
	
	private static int[] fixOffsets(int numFiles,int[] sizes,byte[] locations) {
		int[] targetOffsets = new int[numFiles];
		int[] currentOffsets = new int[maxLocation(locations) + 1];
		for(int i = 0;i < targetOffsets.length;i++) {
			targetOffsets[i] = currentOffsets[locations[i]];
			currentOffsets[locations[i]] += sizes[i];
		}
		return targetOffsets;
	}
	
	public static void constructHead(int numFiles,short[] filenames,byte[] types,int[] offsets,byte[] locations,int[] sizes) throws IOException {
		System.out.println("\nBeginning to construct BFC headers...");
		DataOutputStream head = new DataOutputStream(new FileOutputStream(MAIN_DIR + "head" + BFC_FILE_FORMAT));
		System.out.println("File count: " + numFiles);
		head.writeShort(numFiles);
		for(int i = 0;i < numFiles;i++) {
			System.out.println("[" + i + " " + ((i + 1) * 100 / numFiles) + "%] - Writing " + filenames[i] + " file");
			head.writeShort(filenames[i]);
			head.writeByte(types[i]);
			head.write((offsets[i] & 0xff0000) >> 16);
			head.write((offsets[i] & 0xff00) >> 8);
			head.write(offsets[i] & 0xff);
			head.writeByte(locations[i]);
			head.write((sizes[i] & 0xff0000) >> 16);
			head.write((sizes[i] & 0xff00) >> 8);
			head.write(sizes[i] & 0xff);
		}
		head.close();
		System.out.println("Written " + numFiles + " headers successfully!");
	}
	
	public static byte maxLocation(byte[] locations) {
		byte result = 0;
		for(int i = 0;i < locations.length;i++) {
			if(locations[i] > result) result = locations[i];
		}
		return result;
	}
	
	private static String get(String str,boolean isValue) {
		String result = null;
		if(!isValue) {
			StringBuilder keyBuilder = new StringBuilder();
			char ch;
			for(int i = 0;(ch = str.charAt(i)) != '=';i++) {
				keyBuilder.append(ch);
			}
			result = keyBuilder.toString();
		} else {
			StringBuilder keyBuilder = new StringBuilder();
			int begin = 0;
			for(int i = 0;str.charAt(i) != '=';i++) {
				begin = i;
			}
			begin += 2;
			for(int i = begin;i < str.length();i++) {
				keyBuilder.append(str.charAt(i));
			}
			result = keyBuilder.toString();
		}
		return result;
	}
	
	private static void closeStreams(Closeable[] streams) throws IOException {
		for(Closeable closeable : streams) {
			closeable.close();
		}
	}
}
