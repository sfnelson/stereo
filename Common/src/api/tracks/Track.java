package api.tracks;

import java.io.IOException;
import java.io.InputStream;

import api.Constants;

public interface Track extends HasMetadata {

	public int id();
	public long persistentId();
	public boolean equals(Object o);
	public int hashCode();
	public Album getAlbum();
	public void setAlbum(Album album);
	public Object get(Constants tag);
	public Object get(Constants tag, Object defaultValue);
	public void put(Constants tag, Object value);
	public Iterable<Constants> getAllTags();
	public void getStream(StreamReader reader) throws IOException;
	
	public interface TrackFactory {
		public Track create(int id, long persistentId);
	}
	
	public interface StreamReader {
		public void read(InputStream stream) throws IOException;
	}
}
