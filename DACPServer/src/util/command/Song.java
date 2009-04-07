package util.command;

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
		
		byte[] buf = new byte[length];
		for (int i = 0; i < length;) {
			int read = in.read(buf, i, in.available());
			if (read > 0) i += read;
			else if (read == -1) break;
		}
		
		song = buf;
	}

	public byte[] song() {
		return song;
	}
}
