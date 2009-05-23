package api.collections;

import api.tracks.HasMetadata;
import api.tracks.Track;

public interface Collection<T extends Track>
	extends HasMetadata {
	
	public static final int LIBRARY_ID = 1;
	public static final long LIBRARY_PERSISTENT_ID = 1;
	
	public static final int QUEUE_ID = 4;
	public static final long QUEUE_PERSISTENT_ID = 4;
	
	public static final int FIRST_AVAILABLE_ID = 16;
	public static final long FIRST_AVAILABLE_PERSISTENT_ID = 16;
	
	public static final int NOT_EDITABLE = 0;
	public static final int GENERATED = 0x60;
	public static final int EDITABLE = 0x67;
	
	public int id();
	public long persistentId();
	public String name();
	public boolean isRoot();
	public Collection<? extends T> parent();
	public int size();
	public int editStatus();
	
	/**
	 * The backing source for this collection.
	 * 
	 * May be null.
	 * 
	 * @return backing source
	 */
	public Source<T> source();
	
}
