package music;

import interfaces.Album;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LibraryTrack extends Track {
	
	private final Set<Track> backingTracks = new HashSet<Track>(2);
	
	private LibraryTrack(int id, long persistantId, Album album) {
		super(id, persistantId);
		this.setAlbum(album);
	}
	
	public int id() {
		Track current = current();
		if (current == null) return super.id();
		return current.id();
	}

	public Object get(int tag) {
		Track current = current();
		if (current == null) return null;
		return current.get(tag);
	}

	public Iterable<Integer> getAllTags() {
		Track current = current();
		if (current == null) return new ArrayList<Integer>(0);
		return current.getAllTags();
	}
	
	public InputStream getStream() throws IOException {
		Track current = current();
		if (current == null) return null;
		return current.getStream();
	}
	
	public String toString() {
		return current().toString();
	}
	
	synchronized void addBackingTrack(Track t) {
		backingTracks.add(t);
	}
	
	synchronized void removeBackingTrack(Track t) {
		backingTracks.remove(t);
	}
	
	synchronized int numBackingTracks() {
		return backingTracks.size();
	}
	
	private synchronized Track current() {
		if (backingTracks.isEmpty()) return null;
		return backingTracks.iterator().next();
	}
	
	private static Factory factory = new Factory();
	public static Factory factory() {
		return factory;
	}

	public static class Factory {
		private Map<Long, LibraryTrack> tracks = new HashMap<Long, LibraryTrack>();
		private volatile int id = 0;
		public synchronized LibraryTrack create(Track t) {
			LibraryTrack track = tracks.get(t.persistentId());
			if (track == null) {
				track = new LibraryTrack(++id, t.persistentId(), t.getAlbum());
				tracks.put(t.persistentId(), track);
			}
			return track;
		}
	}
}
