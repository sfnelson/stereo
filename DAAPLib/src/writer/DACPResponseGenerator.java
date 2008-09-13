package writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;

import util.node.BooleanNode;
import util.node.ByteNode;
import util.node.Composite;
import util.node.ImageNode;
import util.node.IntegerNode;
import util.node.LengthVisitor;
import util.node.LongLongNode;
import util.node.LongNode;
import util.node.Node;
import util.node.StringNode;
import util.node.VersionNode;
import util.node.Visitor;

public class DACPResponseGenerator {

	private final LengthVisitor len;

	public DACPResponseGenerator() {

		len = new LengthVisitor();
	}

	public void visit(Node tree, OutputStream output) {

		PrintStream out = new PrintStream(output);

		if (tree == null) {
			out.print("HTTP/1.1 204 OK\r\n\r\n");
		}
		else {
			out.print("HTTP/1.1 200 OK\r\n");

			int length = new LengthVisitor().visit(tree);
			out.print("Content-Type: application/x-dmap-tagged\r\n");
			out.print("Content-Length: "+length+"\r\n");
			out.print("DAAP-Server: memphis-dj-0.1\r\n");
			out.print("Date: "+DateFormat.getDateInstance().format(new Date())+"\r\n");

			out.print("\r\n");

			new WriteVisitor(len, out).visit(tree);
		}

		out.flush();
	}

	public void error(String msg, OutputStream output) {
		PrintStream out = new PrintStream(output);
		out.printf("HTTP/1.1 %s\r\n\r\n", msg);
		out.flush();
	}

	public static void writeCode(int code, OutputStream out) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)(code>>24);
		bytes[1] = (byte)((code>>16)%256);
		bytes[2] = (byte)((code>>8)%256);
		bytes[3] = (byte)(code%256);
		try {
			out.write(bytes);
		}
		catch (IOException ex) {
			System.err.println("error writing to output stream");
			ex.printStackTrace();
		}
	}

	public static void writeInt(int code, OutputStream out) {
		writeCode(code, out);
	}

	public static void writeLong(long code, OutputStream out) {
		writeInt((int)(code>>32), out);
		long base = code & 0xFFFFFFFF;
		writeInt((int)base, out);
	}

	public static void writeString(String code, OutputStream out) {
		try {
			char[] chars = code.toCharArray();
			for (char c: chars) {
				out.write((byte)c);
			}
		}
		catch (IOException ex) {
			System.err.println("error writing to output stream");
			ex.printStackTrace();
		}
	}

	public static void writeVersion(byte[] b, OutputStream out) {
		try {
			for (byte bb: b) {
				out.write(bb);
			}
		} catch (IOException e) {
			System.err.println("error writing to output stream");
			//e.printStackTrace();
		}
	}

	public static void writeNodeHeader(Node node, int length, OutputStream out){
		writeCode(node.code, out);
		writeInt(length-8, out);
	}
	
	public static int writeBytes(byte[] b, OutputStream out){
		
		try {
			out.write(b);
		} catch (IOException e) {
			System.err.println("error writing to output stream");
			//e.printStackTrace();
		}
		
		return b.length;
	}

	private static class WriteVisitor implements Visitor {

		private LengthVisitor len;
		private OutputStream output;

		public WriteVisitor(LengthVisitor len, OutputStream output) {
			this.len = len;
			this.output = output;
		}

		public int visit(Node node) {
			return node.visit(this);
		}

		public int visitBooleanNode(BooleanNode node) {
			int length = len.visit(node);
			DACPResponseGenerator.writeNodeHeader(node, length, output);
			byte b = node.getValue()?(byte)1:(byte)0;
			try {
				output.write(b);
			} catch (IOException e) {
				System.err.println("Failed to write boolean");
				//e.printStackTrace();
			}
			return length;
		}

		public int visitByteNode(ByteNode node) {
			int length = len.visit(node);
			DACPResponseGenerator.writeNodeHeader(node, length, output);
			try {
				output.write(node.getValue());
			} catch (IOException e) {
				System.err.println("error writing to output stream");
				//e.printStackTrace();
			}
			return length;
		}

		public int visitComposite(Composite node) {
			int length = len.visit(node);
			DACPResponseGenerator.writeCode(node.code, output);
			DACPResponseGenerator.writeInt(length-8, output);
			//System.out.println("sending composite " + DAAPUtilities.intToString(node.code) + " length " + length);
			for (Node n: node.nodes) {
				this.visit(n);
			}
			return length;
		}

		public int visitIntegerNode(IntegerNode node) {
			int length = len.visit(node);
			DACPResponseGenerator.writeNodeHeader(node, length, output);
			DACPResponseGenerator.writeInt(node.getValue(), output);
			return length;
		}

		public int visitLongLongNode(LongLongNode node) {
			int length = len.visit(node);
			DACPResponseGenerator.writeNodeHeader(node, length, output);
			DACPResponseGenerator.writeLong(node.getValue(), output);
			DACPResponseGenerator.writeLong(node.getValue2(), output);
			return length;
		}

		public int visitLongNode(LongNode node) {
			int length = len.visit(node);
			DACPResponseGenerator.writeNodeHeader(node, length, output);
			DACPResponseGenerator.writeLong(node.getValue(), output);
			return length;
		}

		public int visitStringNode(StringNode node) {
			int length = len.visit(node);
			DACPResponseGenerator.writeNodeHeader(node, length, output);
			DACPResponseGenerator.writeString(node.getValue(), output);
			return length;
		}

		public int visitVersionNode(VersionNode node) {
			int length = len.visit(node);
			DACPResponseGenerator.writeNodeHeader(node, length, output);
			DACPResponseGenerator.writeVersion(node.getValue(), output);
			return length;
		}

		public int visitImageNode(ImageNode node) {
			return DACPResponseGenerator.writeBytes(node.image(), output);
		}
	}
}
