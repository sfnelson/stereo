package dacp;

import interfaces.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
		write(code.code, 1, value?1:0);
	}

	public void appendByte(Constants code, byte value) {
		write(code.code, 1, value);
	}

	public void appendBytes(Constants code, byte[] value) {
		write(code.code, value.length, value);
	}

	public void appendInteger(Constants code, int value) {
		write(code.code, 4, value);
	}

	public void appendLong(Constants code, long value) {
		write(code.code, 8, value);
	}

	public void appendLongLong(Constants code, int[] value) {
		write(code.code, 16, value);
	}

	public void appendNode(Node node) {
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

	public void appendShort(Constants code, short value) {
		write(code.code, 2, value);
	}

	public void appendString(Constants code, String value) {
		byte[] bytes;
		try {
			bytes = value.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			bytes = value.getBytes();
			System.err.println("UTF-8 not supported: DACPWriter.java");
		}
		write(code.code, bytes.length, bytes);
	}

	public void appendVersion(Constants code, byte[] value) {
		write(code.code, value.length, value);
	}

	public void appendList(final Constants code, final int type, final List<? extends Node> list) {
		appendInteger(Constants.dmap_updatetype, type);
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
