package dmap;


import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Iterator;
import java.util.NoSuchElementException;

import api.Constants;
import api.Reader;

public class DACPReader implements Reader {
	
	public DACPReader(InputStream input, int available) {
		this.stream = input;
		this.consumed = 0;
		this.available = available;
	}

	public boolean hasNextBoolean(Constants code) {
		assert code.type == Constants.BYTE;
		return available(code, 1);
	}

	public boolean hasNextByte(Constants code) {
		assert code.type == Constants.BYTE || code.type == Constants.SIGNED_BYTE;
		return available(code, 1);
	}

	public boolean hasNextComposite(Constants code) {
		assert code.type == Constants.COMPOSITE;
		if (length != 0 && length < 8) return false;
		return available(code, length);
	}
	
	public boolean hasNextDate(Constants code) {
		assert code.type == Constants.DATE;
		return available(code, 4);
	}

	public boolean hasNextInteger(Constants code) {
		assert code.type == Constants.INTEGER || code.type == Constants.SIGNED_INTEGER;
		return available(code, 4);
	}

	public boolean hasNextLong(Constants code) {
		assert code.type == Constants.LONG || code.type == Constants.SIGNED_LONG;
		return available(code, 8);
	}

	public boolean hasNextLongLong(Constants code) {
		assert code.type == Constants.LONG_LONG;
		return available(code, 16);
	}

	public boolean hasNextShort(Constants code) {
		assert code.type == Constants.SHORT || code.type == Constants.SIGNED_SHORT;
		return available(code, 2);
	}

	public boolean hasNextString(Constants code) {
		assert code.type == Constants.STRING;
		return available(code, length);
	}

	public boolean hasNextVersion(Constants code) {
		assert code.type == Constants.VERSION;
		return available(code, 4);
	}
	
	public boolean nextBoolean(Constants code) {
		if (!hasNextBoolean(code)) throw new NoSuchElementException(code.longName);
		read(code, 1);
		return readBoolean(stream);
	}

	public byte nextByte(Constants code) {
		if (!hasNextByte(code)) throw new NoSuchElementException(code.longName);
		read(code, 1);
		return readByte(stream);
	}
	
	public Calendar nextDate(Constants code) {
		if (!hasNextDate(code)) throw new NoSuchElementException(code.longName);
		read(code, 4);
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(1000l * readInteger(stream));
		return date;
	}

	public int nextInteger(Constants code) {
		if (!hasNextInteger(code)) throw new NoSuchElementException(code.longName);
		read(code, 4);
		return readInteger(stream);
	}

	public long nextLong(Constants code) {
		if (!hasNextLong(code)) throw new NoSuchElementException(code.longName);
		read(code, 8);
		return readLong(stream);
	}

	public int[] nextLongLong(Constants code) {
		if (!hasNextLongLong(code)) throw new NoSuchElementException(code.longName);
		read(code, 4*4);
		return new int[] {
				readInteger(stream),
				readInteger(stream),
				readInteger(stream),
				readInteger(stream)
		};
	}
	
	public int nextShort(Constants code) {
		if (!hasNextShort(code)) throw new NoSuchElementException(code.longName);
		read(code, 2);
		return readShort(stream);
	}

	public String nextString(Constants code) {
		if (!hasNextString(code)) throw new NoSuchElementException(code.longName);
		read(code, length);
		return readString(stream, length);
	}

	public byte[] nextVersion(Constants code) {
		if (!hasNextVersion(code)) throw new NoSuchElementException(code.longName);
		read(code, 4);
		return readBytes(stream, 4);
	}

	public Reader nextComposite(Constants code) {
		if (!hasNextComposite(code)) throw new NoSuchElementException(code.longName);
		read(code, length);
		return new DACPReader(stream, length);
	}
	
	public Iterator<Constants> iterator() {
		return new Iterator<Constants>() {
			public boolean hasNext() {
				return available - consumed - 8 > 0;
			}
			public Constants next() {
				if (!hasNext()) {
					code = null;
					length = 0;
					throw new NoSuchElementException();
				}
				
				if (!read) {
					skip(code);
				}
				read = false;
				
				code = Constants.get(readInteger(stream));
				length = readInteger(stream);

				consumed += 8 + length;
				
				return code;
			}
			public void remove() {}
		};
	}
	
	private final InputStream stream;
	private final int available;
	
	private Constants code;
	private int length;
	
	private boolean read = false;
	
	private int consumed;
	
	private boolean available(Constants code, int amount) {
		if (code != this.code) return false;
		if (read) return false;
		if (consumed-length > available) return false;
		if (amount != length) return false;
		
		switch (code.type) {
		case 1: return amount == 1;
		case 3: return amount == 2;
		case 5: return amount == 4;
		case 7: return amount == 8;
		case 10: return amount == 4;
		case 11: return amount == 4;
		default:
			return true; //all other codes are variable length
		}
	}
	
	private void skip(Constants code) {
		read(code, length);
		readBytes(stream, length);
	}
	
	private void read(Constants code, int amount) {
		if (read) {
			throw new RuntimeException("Already Read!");
		}
		read = true;
		if (amount != length) {
			System.err.printf("Read invalid amount: expected %d, read %d\n", amount, length);
		}
	}
	

	private static byte[] readBytes(InputStream stream, int num) {
		byte[] b = new byte[num];
		try {
			for (int i = 0; i < num;) {
				i += stream.read(b, i, num-i);
			}
		}
		catch (IOException e) {
			System.err.println("Error reading bytes from DACPResponse");
			e.printStackTrace();
		}
		return b;
	}

	private static int readShort(InputStream stream) {
		byte[] b = readBytes(stream, 2);
		return ((b[0] & 255) << 8) + (b[1] & 255);
	}

	private static boolean readBoolean(InputStream stream) {
		byte[] b = readBytes(stream, 1);
		return ((b[0] & 255)) == 1;
	}

	private static byte readByte(InputStream stream) {
		byte[] b = readBytes(stream, 1);
		return b[0];
	}

	private static int readInteger(InputStream is) {

		byte[] b = readBytes(is, 4);

		int size = 0;
		for (int i = 0; i < 4; i++) {
			size <<= 8;
			size += b[i] & 255;
		}
		return size;
	}

	private static long readLong(InputStream stream) {
		byte[] b = readBytes(stream, 8);
		long value = 0;
		for (int i = 0; i < 8; i++) {
			value <<= 8;
			value += b[i] & 255;
		}
		return value;
	}

	private static String readString(InputStream stream, int num) {
		byte[] b = readBytes(stream, num);
		String value = "";
		for (int i = 0; i < num; i++) {
			value += (char)b[i];
		}
		return value;
	}
}
