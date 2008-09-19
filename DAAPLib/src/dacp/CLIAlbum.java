package dacp;

import java.util.HashMap;
import java.util.Map;

import interfaces.Album;

public class CLIAlbum implements Album {

	private Map<Integer, Object> tags = new HashMap<Integer, Object>();
	
	public void put(int tag, Object value) {
		this.tags.put(tag, value);
	}
	
	public Map<Integer, Object> getAllTags() {
		return tags;
	}

	public Object getTag(int tagID) {
		return tags.get(tagID);
	}

}
