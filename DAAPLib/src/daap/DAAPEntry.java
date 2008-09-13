package daap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DAAPEntry implements Iterable<DAAPEntry> {
	
	// see http://tapjam.net/daap/
	
	public static final short BYTE = 1;			// byte
	public static final short UBYTE = 2;		// (unsigned byte)
	public static final short SHORT = 3;		// short
	public static final short USHORT = 4;		// (unsigned short)
	public static final short INTEGER = 5;		// int
	public static final short UINTEGER = 6;		// (unsigned int)
	public static final short LONG = 7; 		// long
	public static final short ULONG = 8;		// (unsigned long)
	public static final short STRING = 9;		// string
	public static final short DATE = 10;		// date (represented as a 4 byte integer)
	public static final short VERSION = 11;		// version (represented as either 4 single bytes, e.g. 0.1.0.0 or
												// as two shorts, e.g. 1.0)
	public static final short LIST = 12;		// list
	
	public static DAAPEntry parseStream(InputStream stream, Map<Integer, Short> types) throws IOException {
		
		DAAPEntry.types = types;
		
		DAAPEntry.stream = new BufferedInputStream(stream);
		
		DAAPEntry entry = new DAAPEntry();
		entry.init();
		
		return entry;
	}
	
	private static byte[] read(int length) throws IOException {
		
		byte[] buffer = new byte[length];
		
		int read = 0;
		
		while (read < length) {
			read += stream.read(buffer, read, length-read);
		}
		
		return buffer;
	}
	
	private static String readString(int length) throws IOException {
		
		byte[] buffer = read(length);
		
		return new String(buffer);
	}
	
	private static long readLong() throws IOException {
		
		byte[] buffer = read(8);
		
        int value = 0;
        for (int i = 0; i < 8; i++) {
            int shift = (8 - 1 - i) * 8;
            value += (buffer[i] & 0x000000FF) << shift;
        }
        return value;
    }
	
	private static int readInteger() throws IOException {
		
		byte[] buffer = read(4);
		
		return parseInteger(buffer);
    }
	
	private static int parseInteger(byte[] buffer) throws IOException {
		
		if (buffer.length != 4) return 0;
		
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (buffer[i] & 0x000000FF) << shift;
        }
        return value;
    }
	
	private static short readShort() throws IOException {
		
		byte[] buffer = read(2);
		
        short value = 0;
        for (int i = 0; i < 2; i++) {
            int shift = (2 - 1 - i) * 8;
            value += (buffer[i] & 0x000000FF) << shift;
        }
        return value;
    }
	
	private static byte readByte() throws IOException {
		
		byte[] buffer = read(1);
		
        return buffer[0];
    }

	private static BufferedInputStream stream;
	private static Map<Integer,Short> types;
	
	/*
	 * Instance Methods
	 */
	
	public int getName() {
		return this.name;
	}
	
	public int getNumChildren() {
		return this.length;
	}
	
	public short getType() {
		return this.type;
	}
	
	public Object getValue() {
		return this.value;
	}
	
	public boolean hasChildren() {
		return this.type == DAAPEntry.LIST;
	}
	
	public Iterator<DAAPEntry> iterator() {
		
		if (this.type != DAAPEntry.LIST) {
			return null;
		}
		
		return this.iterator;
	}
	
	public Map<Integer,Object> getValueMap() {
		
		if (!hasChildren()) return null;
		
		if (this.valueMap != null) {
			while (!this.valueMap.isEmpty()) {
				this.valueMap.remove(this.valueMap.keySet().iterator().next());
			}
		}
		else {
			this.valueMap = new HashMap<Integer, Object>();
		}
		
		for (DAAPEntry entry: this) {
			if (entry == null) continue;
			
			if (!this.valueMap.containsKey(entry.name)) {
				this.valueMap.put(entry.name, entry.value);
			}
		}
		
		return this.valueMap;
	}
	
	/**
	 * Private constructor enforces singleton pattern
	 *
	 */
	private DAAPEntry() {
		//initialization is performed elsewhere
	}
	
	protected boolean init() throws IOException {
		
		int name = DAAPEntry.readInteger();
		int length = DAAPEntry.readInteger();
		
		Short type = DAAPEntry.types.get(name);
		
		if (type == null) {
			System.err.println("parser: entry '" + DAAPUtilities.intToString(name) + "' of length "+ length + "(" + DAAPUtilities.intToString(length) + ") not found");
			return false;
		}

		this.name = name;
		this.length = length;
		this.type = type;

		readValue();
		
		return true;
	}
	
	private void readValue() throws IOException {
		
		switch (this.type) {
		case LONG:
			this.value = readLong();
			break;
		case INTEGER:
			this.value = readInteger();
			break;
		case SHORT:
			this.value = readShort();
			break;
		case BYTE:
			this.value = readByte();
			break;
		case VERSION:
			String version = "";
			version += readByte();
			for (int i = 1; i < 4; i++) {
				version += "." + readByte();
			}
			this.value = version;
			break;
		case LIST:
			this.iterator = new DaapEntryIterator(this);
			break;
		case STRING:
			this.value = readString(this.length);
			break;
		default:
			System.err.println("unknown type: " + this.type + " of length " + this.length + " for " + DAAPUtilities.intToString(this.name));
		this.value = readString(this.length);
			break;
		}
	}
	
	protected int recover(int remaining) throws IOException {
		
		System.err.println("parser: parsing error, trying to recover with " + remaining + " bytes to go.");
		
		stream.reset();
		
		byte[] bytes = new byte[4];
		
		int read = 0;
		
		while (read < remaining) {

			for (int i = 0; i < 3; i++) {
				bytes[i] = bytes[i+1];
			}
			
			bytes[3] = readByte();
			read += 1;
			
			int value = parseInteger(bytes);
			
	        if (DAAPEntry.types.containsKey(value)) {
	        	
	        	read = read-4;
	        	
	        	this.name = value;
	        	this.length = DAAPEntry.readInteger();
	        	this.type = DAAPEntry.types.get(value);
	        	readValue();
	        	
	        	System.err.println("recovered after " + read + " bytes: " + DAAPUtilities.intToString(value) + " length " + this.length + ", type " + this.type + ": " + value);
	        		
	        	return read;
	        }
		}
		return -1;
	}
	
	protected int getLength() {
		return this.length;
	}
	
	protected static BufferedInputStream getStream() {
		return DAAPEntry.stream;
	}
	
	private int name;
	private int length;
	private short type;
	
	private Object value;
	
	private DaapEntryIterator iterator;
	private Map<Integer, Object> valueMap;
	
	private class DaapEntryIterator implements Iterator<DAAPEntry> {
		
		private DAAPEntry entry;
		private int length;
		private int consumed;
		
		public DaapEntryIterator(DAAPEntry entry) {
			this.entry = entry;
			this.length = entry.getLength();
		}
		
		public boolean hasNext() {
			return this.length > this.consumed;
		}
		
		public DAAPEntry next() {
			
			if (this.length <= this.consumed) {
				return null;
			}

			//System.out.print("new entry: ");
			try {
				DAAPEntry.getStream().mark(64);
				
				if (this.entry.init()) {
					this.consumed += this.entry.getLength() + 8;
				}
				else {
					int error = this.entry.recover(this.length-this.consumed);
					if (error > 0) {
						this.consumed += error + this.entry.getLength() + 8;
					}
					else {
						this.consumed = this.length;
						return null;
					}
				}
			}
			catch (IOException ex) {
				//ex.printStackTrace();
				System.err.println("Failed to get DAAP stream");
				return null;
			}
			
			return this.entry;
		}
		
		public void remove() {
			throw new NotImplementedException();
		}
	}
}
