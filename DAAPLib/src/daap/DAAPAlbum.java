package daap;

import java.util.HashMap;
import java.util.Map;

import util.DACPConstants;

import interfaces.Album;

public class DAAPAlbum implements Album {

	private int id;
	private Map<Integer, Object> tags;
	
	private DAAPAlbum(int id) {
		this.id = id;
		this.tags = new HashMap<Integer, Object>();
	}
	
	public int getItems() {
		return (Integer)tags.get(DACPConstants.mimc);
	}
	
	public void setItems(int size) {
		tags.put(DACPConstants.mimc, size);
	}
	
	public Map<Integer, Object> getAllTags() {
		return tags;
	}

	public Object getTag(int keyId) {
		return tags.get(keyId);
	}
	
	private void addTag(int tagId, Object value) {
		tags.put(tagId, value);
	}
	
	private static int lastId = 0;
	static DAAPAlbum createAlbum(String name, String artist) {
		
		DAAPAlbum a = new DAAPAlbum(++lastId);
		a.addTag(DACPConstants.miid, a.id); //album id (local)
		
		long per = (((long)name.hashCode())<<31)+((long)artist.hashCode());
		a.addTag(DACPConstants.asai, per); //album id (persistant)
		a.addTag(DACPConstants.ALBUM, name);
		a.addTag(DACPConstants.ARTIST, artist);
		
		return a;
	}

}
