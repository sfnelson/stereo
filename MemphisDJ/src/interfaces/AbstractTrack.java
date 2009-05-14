package interfaces;

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
		tags.put(tag, value);
	}

	public Iterable<Constants> getAllTags() {
		return tags.keySet();
	}
	
	public interface TrackFactory {
		public AbstractTrack create(int id, long persistentId);
	}
}
