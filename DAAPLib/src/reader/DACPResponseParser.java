package reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import daap.DAAPConstants;

import util.DACPConstants;
import util.node.BooleanNode;
import util.node.ByteNode;
import util.node.Composite;
import util.node.IntegerNode;
import util.node.LongLongNode;
import util.node.LongNode;
import util.node.Node;
import util.node.StringNode;


public class DACPResponseParser {

	private final Handler reply;

	public DACPResponseParser() {
		reply = new Handler();

		addStatusUpdate(reply);
		addPlaylistRequest(reply);
	}

	private InputStream stream;

	public Composite parse(InputStream stream) {
		this.stream = stream;
		
		try {
			if (stream.available() == 0) return null;
		}
		catch (IOException ex) {
			return null;
		}

		int c = readInteger(stream);
		int b = readInteger(stream);
		
		Node tree = this.reply.visit(c, b);

		return (Composite) tree;
	}

	private void addStatusUpdate(Handler reply) {
		
		reply.register(DACPConstants.mstt, new IntegerNodeHandler());
		reply.register(DACPConstants.cmsr, new IntegerNodeHandler());
		reply.register(DACPConstants.caps, new ByteNodeHandler());
		reply.register(DACPConstants.cash, new ByteNodeHandler());
		reply.register(DACPConstants.carp, new ByteNodeHandler());
		reply.register(DACPConstants.caas, new IntegerNodeHandler());
		reply.register(DACPConstants.caar, new IntegerNodeHandler());
		reply.register(DACPConstants.canp, new LongLongNodeHandler());
		reply.register(DACPConstants.cann, new StringNodeHandler());
		reply.register(DACPConstants.cana, new StringNodeHandler());
		reply.register(DACPConstants.canl, new StringNodeHandler());
		reply.register(DACPConstants.cang, new StringNodeHandler());
		reply.register(DACPConstants.asai, new LongNodeHandler());
		reply.register(DACPConstants.cmmk, new IntegerNodeHandler());
		reply.register(DACPConstants.cant, new IntegerNodeHandler());
		reply.register(DACPConstants.cast, new IntegerNodeHandler());
	}

	private void addPlaylistRequest(Handler reply) {
		reply.register(DACPConstants.mstt, new IntegerNodeHandler());
		reply.register(DACPConstants.muty, new IntegerNodeHandler());
		reply.register(DACPConstants.mtco, new IntegerNodeHandler());
		reply.register(DACPConstants.mrco, new IntegerNodeHandler());

		Handler playlistHandler = new Handler();
		reply.register(DACPConstants.mlcl, playlistHandler);

		Handler trackHandler = new Handler();
		playlistHandler.register(DACPConstants.mlit, trackHandler);

		trackHandler.register(DAAPConstants.ITEM_KIND, new ByteNodeHandler());
		trackHandler.register(DAAPConstants.TRACK_ID, new IntegerNodeHandler()); 
		trackHandler.register(DAAPConstants.NAME, new StringNodeHandler());
		trackHandler.register(DAAPConstants.ARTIST, new StringNodeHandler());
		trackHandler.register(DAAPConstants.ALBUM, new StringNodeHandler());
		trackHandler.register(DAAPConstants.BITRATE, new ShortNodeHandler());
		trackHandler.register(DAAPConstants.COMPOSER, new StringNodeHandler());
		trackHandler.register(DAAPConstants.GENRE, new StringNodeHandler());
		trackHandler.register(DAAPConstants.SONG_TIME, new IntegerNodeHandler());
		trackHandler.register(DAAPConstants.START_TIME, new IntegerNodeHandler());
		trackHandler.register(DAAPConstants.STOP_TIME, new IntegerNodeHandler());

		trackHandler.register(DACPConstants.assr, new IntegerNodeHandler());

	}

	private class Handler {
		public Node visit(int code, int bytes) {

			Composite node = new Composite(code);

			int read = 0;
			while (read < bytes) {
				int c = readInteger(stream);
				int b = readInteger(stream);
				
				if (handlers.get(c) == null) {
					System.err.println("unexpected " + Node.intToCode(c) + " (" + c + ") in " + Node.intToCode(code) + " block");
					read += 8 + b;
					try {
						stream.skip(b);
					} catch (IOException e) {
						System.err.println("Error skipping unknown in DACP response");
						e.printStackTrace();
					}
				}

				else {
					Node n = handlers.get(c).visit(c, b);
					read += 8 + b;
					node.append(n);
				}

			}
			return node;
		}

		public void register(int code, Handler handler) {
			this.handlers.put(code, handler);
		}

		private Map<Integer, Handler> handlers = new HashMap<Integer, Handler>();
	}

	private class StringNodeHandler extends Handler {
		public Node visit(int code, int bytes) {

			String value = readString(stream, bytes);
			return new StringNode(code,value);
		}
	}

	private class IntegerNodeHandler extends Handler {
		public Node visit(int code, int bytes) {

			int value = readInteger(stream);
			Node node = new IntegerNode(code, value);

			return node;
		}
	}

	private class ShortNodeHandler extends Handler {
		public Node visit(int code, int bytes) {

			int value = readShort(stream);
			Node node = new IntegerNode(code, value);

			return node;
		}
	}

	private class BooleanNodeHandler extends Handler {
		public Node visit(int code, int bytes) {

			boolean value = readBoolean(stream) == 1;
			Node node = new BooleanNode(code, value);

			return node;
		}
	}

	private class ByteNodeHandler extends Handler {
		public Node visit(int code, int bytes) {

			byte value = readByte(stream);
			Node node = new ByteNode(code, value);

			return node;
		}
	}

	private class LongNodeHandler extends Handler {
		public Node visit(int code, int bytes) {

			long value = readLong(stream);
			Node node = new LongNode(code, value);

			return node;
		}
	}

	private class LongLongNodeHandler extends Handler {
		public Node visit(int code, int bytes) {

			int v0 = readInteger(stream);
			int v1 = readInteger(stream);
			int v2 = readInteger(stream);
			int v3 = readInteger(stream);
			Node node = new LongLongNode(code, v0, v1, v2, v3);

			return node;
		}
	}

	public static InputStream request(String host, int port, String request) throws IOException, UnknownHostException {

		Socket socket = new Socket(host, port);
		PrintStream out = new PrintStream(socket.getOutputStream());
		out.print("GET " + request + " HTTP/1.1\r\n\r\n");
		InputStream in = socket.getInputStream();

		String buffer = "";
		while (true) {
			char c = (char) in.read();
			buffer += c;
			if (c == '\n' && buffer.endsWith("\r\n\r\n"))
				break;
		}

		Scanner sc = new Scanner(buffer);

		String protocol = sc.next();
		int status = sc.nextInt();
		String code = sc.next();

		if (!protocol.equals("HTTP/1.1"))
			throw new IOException("Unsupported Protocol: " + protocol);
		if (status/100 != 2)
			throw new IOException("Error connecting to server: " + status + " " + code);

		if (!sc.nextLine().equals(""))
			throw new IOException("Expected empty line after header");

		//TODO use these fields for sanity check
		@SuppressWarnings("unused")
		String contentType, date, version = null;
		@SuppressWarnings("unused")
		int contentLength = 0;
		String line;

		while (sc.hasNextLine()) {
			line = sc.nextLine();
			if (line.equals(""))
				break;

			Scanner lineScanner = new Scanner(line);
			String token = lineScanner.next();
			if (token.equals("Content-Type:")) {
				contentType = lineScanner.next();
			}
			else if (token.equals("Content-Length:")) {
				contentLength = lineScanner.nextInt();
			}
			else if (token.equals("DAAP-Server:")) {
				version = lineScanner.next();
			}
			else if (token.equals("Date:")) {
				date = lineScanner.next();
			}
		}
		
		return in;
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

	private static int readBoolean(InputStream stream) {
		byte[] b = readBytes(stream, 1);
		return ((b[0] & 255));
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