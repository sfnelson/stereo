package dacp;

import java.io.IOException;
import java.io.InputStream;

import music.Track;
import daap.DAAPConstants;

public class CLITrack extends Track {
	
	public CLITrack(int id, long persistent) {
		super(id, persistent);
	}

	public InputStream getStream() throws IOException {
		return null;
	}
	
	public String toString() {
		String name = (String)get(DAAPConstants.NAME);
		String artist = (String)get(DAAPConstants.ARTIST);
		return name + " - " + artist;
	}
	
	void put(Integer tag, Object value) {
		super.put(tag, value);
	}
}
