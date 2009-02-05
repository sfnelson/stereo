package interfaces;

import java.io.IOException;
import java.io.InputStream;

public interface Track extends HasMetadata {

	public int id();
	public long persistentId();
	public boolean equals(Object o);
	public int hashCode();
	public Album getAlbum();
	public void setAlbum(Album album);
	public Object get(Constants tag);
	public void put(Constants tag, Object value);
	public Iterable<Constants> getAllTags();
	public InputStream getStream() throws IOException;
	
	public interface TrackFactory {
		public Track create(int id, long persistentId);
	}
}
