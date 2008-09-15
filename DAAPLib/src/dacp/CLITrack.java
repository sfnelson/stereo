package dacp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import daap.DAAPConstants;

import interfaces.Track;

public class CLITrack implements Track {

	public Map<Integer, Object> getAllTags() {
		return tags;
	}

	public InputStream getStream() throws IOException {
		return null;
	}

	public Object getTag(int tagID) {
		return tags.get(tagID);
	}

	public int getTrackId() {
		return id;
	}
	
	public String toString() {
		String name = (String)tags.get(DAAPConstants.NAME);
		String artist = (String)tags.get(DAAPConstants.ARTIST);
		return name + " - " + artist;
	}
	
	void putTag(Integer i, Object o) {
		tags.put(i, o);
	}
	
	void setId(int id) {
		this.id = id;
	}
	
	private Map<Integer, Object> tags = new HashMap<Integer, Object>();
	private int id; //package visibility

}
