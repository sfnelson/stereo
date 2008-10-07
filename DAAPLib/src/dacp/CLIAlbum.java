package dacp;

import interfaces.Album;

import java.util.HashMap;
import java.util.Map;

import daap.DAAPConstants;

public class CLIAlbum implements Album {

	private Map<Integer, Object> tags = new HashMap<Integer, Object>();
	
	public void put(int tag, Object value) {
		this.tags.put(tag, value);
	}
	
	public Iterable<Integer> getAllTags() {
		return tags.keySet();
	}

	public Object get(int tagID) {
		return tags.get(tagID);
	}

	public int id() {
		return (Integer)tags.get(DAAPConstants.miid);
	}

	public long persistentId() {
		return (Long)tags.get(DAAPConstants.mper);
	}

}
