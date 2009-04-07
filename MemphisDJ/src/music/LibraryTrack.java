package music;

import interfaces.Album;
import interfaces.Constants;
import interfaces.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LibraryTrack implements Track {
	
	private final int id;
	private final long persistentId;
	private final Set<Track> backingTracks = new HashSet<Track>(2);
	
	private LibraryTrack(int id, long persistantId, Album album) {
		this.id = id;
		this.persistentId = persistantId;
		this.setAlbum(album);
	}
	
	public int id() {
		Track current = current();
		if (current == null) return id;
		return current.id();
	}
	
	public Album getAlbum() {
		Track current = current();
		if (current == null) return null;
		return current.getAlbum();
	}

	public long persistentId() {
		Track current = current();
		if (current == null) return persistentId;
		return current.persistentId();
	}

	public void put(Constants tag, Object value) {
		for (Track t: backingTracks) {
			t.put(tag, value);
		}
	}

	public void setAlbum(Album album) {
		for (Track t: backingTracks) {
			t.setAlbum(album);
		}
	}

	public Object get(Constants tag) {
		Track current = current();
		if (current == null) return null;
		return current.get(tag);
	}

	public Iterable<Constants> getAllTags() {
		Track current = current();
		if (current == null) return new ArrayList<Constants>(0);
		return current.getAllTags();
	}
	
	public void getStream(StreamReader reader) throws IOException {
		Track current = current();
		if (current == null) return;
		current.getStream(reader);
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
