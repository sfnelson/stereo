package interfaces.collection;

import interfaces.HasMetadata;
import music.Track;

public interface Collection<T extends Track>
	extends HasMetadata, Source<T>, Iterable<T> {
	
	public static final int LIBRARY_ID = 1;
	public static final long LIBRARY_PERSISTENT_ID = 1;
	
	public static final int QUEUE_ID = 4;
	public static final long QUEUE_PERSISTENT_ID = 4;
	
	public static final int FIRST_AVAILABLE_ID = 7;
	public static final long FIRST_AVAILABLE_PERSISTENT_ID = 7;
	
	public String name();
	public boolean isRoot();
	public Collection<T> parent();
	public int size();
	
}
