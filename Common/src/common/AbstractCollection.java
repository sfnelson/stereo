package common;

import java.util.HashMap;
import java.util.Map;

import api.Constants;
import api.collections.Collection;
import api.tracks.Track;

public abstract class AbstractCollection<T extends Track> implements Collection<T> {

	private Map<Constants, Object> tags = new HashMap<Constants, Object>();
	private final int id;
	private final long persistentId;
	
	public AbstractCollection(int id, long persistentId) {
		this.id = id;
		this.persistentId = persistentId;
		
		put(Constants.dmap_itemid, id);
		put(Constants.dmap_persistentid, id);
	}
	
	protected void put(Constants tag, Object value) {
		switch (tag) {
		case dmap_itemid:
		case dmap_persistentid:
			//these are read-only
			assert false: "attempting to set read-only variable";
		default:
			tags.put(tag, value);
		}
	}
	
	public Object get(Constants tag) {
		return tags.get(tag);
	}

	public Iterable<Constants> getAllTags() {
		return tags.keySet();
	}
	
	public int id() {
		return id;
	}

	public long persistentId() {
		return persistentId;
	}

}
