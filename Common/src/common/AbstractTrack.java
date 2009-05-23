package common;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import api.Constants;
import api.tracks.Album;
import api.tracks.Track;

public abstract class AbstractTrack implements Track {
	
	private final int id;
	private final long persistentId;
	private Album album;
	private final Map<Constants, Object> tags = new HashMap<Constants, Object>();
	
	public AbstractTrack(int id, long persistentId) {
		this.id = id;
		this.persistentId = persistentId; 
		this.tags.put(Constants.dmap_itemid, id);
		this.tags.put(Constants.dmap_persistentid, persistentId);
	}
	
	public int id() {
		return id;
	}
	
	public long persistentId() {
		return persistentId;
	}
	
	public boolean equals(Object o) {
		if (o instanceof AbstractTrack) {
			return ((AbstractTrack)o).persistentId == this.persistentId;
		}
		return false;
	}
	
	public int hashCode() {
		return (int)(persistentId^persistentId>>>32);
	}
	
	public Album getAlbum() {
		return album;
	}
	
	public void setAlbum(Album album) {
		this.album = album;
		tags.put(Constants.daap_songalbumid, album.id());
	}
	
	public Object get(Constants tag) {
		return tags.get(tag);
	}
	
	public Object get(Constants tag, Object defaultValue) {
		Object ret = get(tag);
		if (ret == null) {
			return defaultValue;
		}
		else {
			return ret;
		}
	}
	
	public void put(Constants tag, Object value) {
		if (value == null) return;
		
		boolean correct = true;
		
		switch (tag.type) {
		case Constants.BYTE: if (value instanceof Byte) break;
		case Constants.SIGNED_BYTE: if (value instanceof Byte) break;
		case Constants.SHORT: if (value instanceof Short) break;
		case Constants.SIGNED_SHORT: if (value instanceof Short) break;
		case Constants.INTEGER: if (value instanceof Integer) break;
		case Constants.SIGNED_INTEGER: if (value instanceof Integer) break;
		case Constants.LONG: if (value instanceof Long) break;
		case Constants.SIGNED_LONG: if (value instanceof Long) break;
		case Constants.STRING: if (value instanceof String) break;
		case Constants.DATE: if (value instanceof Calendar) break;
		case Constants.VERSION: //versions should not be stored in tracks
		case Constants.COMPOSITE: //composites should not be stored in tracks
		case Constants.LONG_LONG: //long-longs should not be stored in tracks	
		default:
			assert false: "incorrect or unexpected type " + tag.type + " for " + tag.longName;
			correct = false;
		}
		
		if (correct) tags.put(tag, value);
	}

	public Iterable<Constants> getAllTags() {
		return tags.keySet();
	}
	
	public interface TrackFactory {
		public AbstractTrack create(int id, long persistentId);
	}
}
