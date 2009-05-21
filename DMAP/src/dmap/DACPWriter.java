package dmap;

import interfaces.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.List;

import api.Node;
import api.Writer;

public class DACPWriter implements Writer {

	private final OutputStream stream;
	private int bytes = 0;
	private int nodes = 0;

	public DACPWriter(OutputStream stream) {
		this.stream = stream;
	}

	public void appendBoolean(Constants code, boolean value) {
		check(code, new int[] {Constants.BYTE, Constants.SIGNED_BYTE});
		write(code.code, 1, value?1:0);
	}

	public void appendByte(Constants code, byte value) {
		check(code, new int[] {Constants.BYTE, Constants.SIGNED_BYTE});
		write(code.code, 1, value);
	}
	
	public void appendShort(Constants code, short value) {
		check(code, new int[] {Constants.SHORT, Constants.SIGNED_SHORT});
		write(code.code, 2, value);
	}

	public void appendInteger(Constants code, int value) {
		check(code, new int[] {Constants.INTEGER, Constants.SIGNED_INTEGER});
		write(code.code, 4, value);
	}

	public void appendLong(Constants code, long value) {
		check(code, new int[] {Constants.LONG, Constants.SIGNED_LONG});
		write(code.code, 8, value);
	}

	public void appendLongLong(Constants code, int[] value) {
		check(code, new int[] {Constants.LONG_LONG});
		write(code.code, 16, value);
	}

	public void appendNode(Node node) {
		check(node.type(), new int[] { Constants.COMPOSITE });
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Writer w = new DACPWriter(stream);
		node.write(w);

		try {
			writeNum(node.type().code, 4);
			writeNum(stream.size(), 4);
			stream.writeTo(this.stream);
		}
		catch (IOException ex) {
			System.err.println("error writing to output stream");
			ex.printStackTrace();
		}
	}

	public void appendString(Constants code, String value) {
		check(code, new int[] { Constants.STRING, Constants.COMPOSITE });
		byte[] bytes;
		if (value != null) {
			try {
				bytes = value.getBytes("UTF-8");
			}
			catch (UnsupportedEncodingException ex) {
				bytes = value.getBytes();
				System.err.println("UTF-8 not supported: DACPWriter.java");
			}
		}
		else {
			bytes = new byte[0]; 
		}
		write(code.code, bytes.length, bytes);
	}

	public void appendVersion(Constants code, byte[] value) {
		check(code, new int[] { Constants.VERSION });
		write(code.code, value.length, value);
	}
	
	public void appendDate(Constants code, Calendar date) {
		check(code, new int[] { Constants.DATE });
		write(code.code, 4, (int)(date.getTimeInMillis()/1000));
	}

	public void appendList(final Constants code, final byte type, final List<? extends Node> list) {
		check(code, new int[] { Constants.COMPOSITE });
		
		appendByte(Constants.dmap_updatetype, type);
		appendInteger(Constants.dmap_specifiedtotalcount, list.size());
		appendInteger(Constants.dmap_returnedcount, list.size());

		appendNode(new Node() {
			public Constants type() {
				return code;
			}
			public void write(Writer writer) {
				for (Node n: list) {
					writer.appendNode(n);
				}
			}
		});
	}
	
	private void check(Constants code, int[] types) {
		for (int t: types) {
			if (code.type == t) return;
		}
		throw new RuntimeException(code.longName + " appended as incorrect type, expected " + code.type);
	}

	private void write(int code, int length, int value) {
		try {
			writeNum(code, 4);
			writeNum(length, 4);
			writeNum(value, length);

			nodes++;
		}
		catch (IOException ex) {
			System.err.println("error writing to output stream");
			ex.printStackTrace();
		}
	}

	private void write(int code, int length, byte[] value) {
		try {
			writeNum(code, 4);
			writeNum(length, 4);
			stream.write(value);
			bytes += length;

			nodes++;
		}
		catch (IOException ex) {
			System.err.println("error writing to output stream");
			ex.printStackTrace();
		}
	}

	private void write(int code, int length, long value) {
		try {
			writeNum(code, 4);
			writeNum(length, 4);
			writeNum((int)(value>>32), 4);
			writeNum((int)(value&0xFFFFFFFF), 4);

			nodes++;
		}
		catch (IOException ex) {
			System.err.println("error writing to output stream");
			ex.printStackTrace();
		}
	}

	private void write(int code, int length, int[] value) {
		try {
			writeNum(code, 4);
			writeNum(length, 4);
			for (int i: value) {
				writeNum(i, 4);
			}

			nodes++;
		}
		catch (IOException ex) {
			System.err.println("error writing to output stream");
			ex.printStackTrace();
		}
	}

	private void writeNum(int var, int num) throws IOException {
		switch (num) {
		case 4:
			stream.write(var>>24);
			stream.write((var>>16)%256);
		case 2:
			stream.write((var>>8)%256);
		case 1:
			stream.write(var%256);
			break;
		default:
			throw new IOException("Unsupported number length: " + num);
		}

		bytes += num;
	}

}
