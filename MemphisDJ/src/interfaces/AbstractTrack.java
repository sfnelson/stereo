package interfaces;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
		return ((Long)persistentId).hashCode();
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
		
		switch (tag.type) {
		case Constants.BYTE: assert(value instanceof Byte); break;
		case Constants.SIGNED_BYTE: assert(value instanceof Byte); break;
		case Constants.SHORT: assert(value instanceof Short); break;
		case Constants.SIGNED_SHORT: assert(value instanceof Short); break;
		case Constants.INTEGER: assert(value instanceof Integer); break;
		case Constants.SIGNED_INTEGER: assert(value instanceof Integer); break;
		case Constants.LONG: assert(value instanceof Long); break;
		case Constants.SIGNED_LONG: assert(value instanceof Long); break;
		case Constants.STRING: assert(value instanceof String); break;
		case Constants.DATE: assert(value instanceof Calendar); break;
		case Constants.VERSION: assert(false); break; //versions should not be stored in tracks
		case Constants.COMPOSITE: assert(false); break; //composites should not be stored in tracks
		case Constants.LONG_LONG: assert(false); break; //long-longs should not be stored in tracks
		default: assert(false);
		}
		tags.put(tag, value);
	}

	public Iterable<Constants> getAllTags() {
		return tags.keySet();
	}
	
	public interface TrackFactory {
		public AbstractTrack create(int id, long persistentId);
	}
}
