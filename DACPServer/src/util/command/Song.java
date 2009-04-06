package util.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import api.Response;

public class Song extends Response {

	private final byte[] song;
	
	public Song(byte[] song) {
		super(null, Response.OK);
		
		this.song = song;
	}
	
	public Song(InputStream in, int length) throws IOException {
		super(null, Response.OK);
		
		ByteArrayOutputStream str = new ByteArrayOutputStream(length);
		byte[] buf = new byte[256];
		while (true) {
			int read = in.read(buf, 0, 256);
			if (read > 0) str.write(buf, 0, read);
			else if (read == -1) break;
		}
		
		song = str.toByteArray();
	}

	public byte[] song() {
		return song;
	}
}
