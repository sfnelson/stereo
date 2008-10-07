package interfaces;


import java.util.HashMap;
import java.util.Map;

public class AbstractAlbum implements Album {

	private final int id;
	private final long persistentId;
	
	private final Map<Integer, Object> tags = new HashMap<Integer, Object>();
	
	public AbstractAlbum(int id, long persistentId) {
		this.id = id;
		this.persistentId = persistentId;
	}
	
	public int id() {
		return id;
	}

	public long persistentId() {
		return persistentId;
	}

	public Object get(int tag) {
		return tags.get(tag);
	}
	
	protected void put(int tag, Object value) {
		tags.put(tag, value);
	}

	public Iterable<Integer> getAllTags() {
		return tags.keySet();
	}
}
