package interfaces.collection;


import interfaces.Constants;

import java.util.HashMap;
import java.util.Map;

import music.Track;
import notification.AbstractEventGenerator;

public abstract class AbstractCollection<T extends Track> extends AbstractEventGenerator<Source.Listener> implements Collection<T> {

	private Map<Constants, Object> tags = new HashMap<Constants, Object>();
	private final int id;
	private final long persistentId;
	
	public AbstractCollection(int id, long persistentId) {
		this.id = id;
		this.persistentId = persistentId;
	}
	
	protected void put(Constants tag, Object value) {
		tags.put(tag, value);
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
