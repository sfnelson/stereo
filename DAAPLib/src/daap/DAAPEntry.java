package daap;

import interfaces.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DAAPEntry {
	
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
	
	public static DAAPEntry parseStream(InputStream stream) throws IOException {
		
		//if we throw our toys here then all bets are off
		int code = DAAPEntry.readInteger(read(stream, 4), 0);
		int length = DAAPEntry.readInteger(read(stream, 4), 0);
		
		byte[] bytes = read(stream, length);
		
		DAAPEntry entry = new DAAPEntry(code, length, bytes, 0);
		
		return entry;
	}
	
	private final Constants code;
	private final int length;
	private final Object value;
	private final List<DAAPEntry> children = new ArrayList<DAAPEntry>();
	
	public DAAPEntry(int code, int length, byte[] bytes, int pos) throws IOException {
		
		this.code = Constants.get(code);
		this.length = length;
		
		if (this.code == null) {
			throw new IOException("parser: unknown entry '" + code + "' (" + length + ")");
		}
		
		int type = this.code.type;

		switch (type) {
		case LONG:
			this.value = readLong(bytes, pos);
			break;
		case INTEGER:
			this.value = readInteger(bytes, pos);
			break;
		case SHORT:
			this.value = readShort(bytes, pos);
			break;
		case BYTE:
			this.value = readByte(bytes, pos);
			break;
		case VERSION:
			String version = "";
			version += readByte(bytes, pos);
			for (int i = 1; i < 4; i++) {
				version += "." + readByte(bytes, pos+i);
			}
			this.value = version;
			break;
		case LIST:
			int offset = 0;
			while (offset < length) {
				int c = readInteger(bytes, pos+offset);
				int l = readInteger(bytes, pos+offset+4);
				try {
					children.add(new DAAPEntry(c, l, bytes, pos+offset+8));
				}
				catch (IOException ex) {
					ex.printStackTrace();
				}
				offset += 8 + l;
			}
			value = "";
			break;
		case STRING:
			this.value = readString(bytes, pos, length);
			break;
		default:
			System.err.println("Unknown type: " + type + " of length " + length + " for " + this.code.longName);
			this.value = readString(bytes, pos, length);
			break;
		}
	}
	
	private static byte[] read(InputStream stream, int length) throws IOException {
		
		byte[] buffer = new byte[length];
		
		int read = 0;
		
		while (read < length) {
			read += stream.read(buffer, read, length-read);
		}
		
		return buffer;
	}
	
	public Constants code() {
		return code;
	}
	
	public int length() {
		return length;
	}
	
	public Object value() {
		return value;
	}
	
	public Iterable<DAAPEntry> children() {
		return children;
	}
	
	public String toString() {
		String out = code.shortName + " " + length + " " + value + "\n";
		for (DAAPEntry e: children()) {
			Scanner sc = new Scanner(e.toString());
			while (sc.hasNextLine()) {
				out += "  " + sc.nextLine() + "\n";
			}
		}
		return out;
	}
	
	private static String readString(byte[] buffer, int pos, int length) {
		return new String(buffer, pos, length);
	}
	
	private static long readLong(byte[] buffer, int pos) {
        int value = 0;
        for (int i = 0; i < 8; i++) {
            int shift = (8 - 1 - i) * 8;
            value += (buffer[pos+i] & 0x000000FF) << shift;
        }
        return value;
    }
	
	private static int readInteger(byte[] buffer, int pos) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (buffer[pos+i] & 0x000000FF) << shift;
        }
        return value;
    }
	
	private static short readShort(byte[] buffer, int pos) {
        short value = 0;
        for (int i = 0; i < 2; i++) {
            int shift = (2 - 1 - i) * 8;
            value += (buffer[pos+i] & 0x000000FF) << shift;
        }
        return value;
    }
	
	private static byte readByte(byte[] buffer, int pos) throws IOException {
        return buffer[pos];
    }
}
