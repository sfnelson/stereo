package daap;

import interfaces.Playlist;
import interfaces.Track;

import java.util.AbstractList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DAAPPlaylist extends AbstractList<DAAPTrack> implements Playlist<DAAPTrack> {

	private final String name;
	private final int id;
	private final long persistantId;
	private final Playlist parent;
	
	private Set<DAAPTrack> tracks = new HashSet<DAAPTrack>();
	
	public DAAPPlaylist(String name, int id, long persistantId, Playlist parent) {
		this.name = name;
		this.id = id;
		this.persistantId = persistantId;
		this.parent = parent;
	}

	public int id() {
		return id;
	}

	public String name() {
		return name;
	}

	public Playlist<Track> parent() {
		return parent;
	}
	
	public boolean isRoot() {
		return false;
	}

	public long persistantId() {
		return persistantId;
	}

	public boolean add(DAAPTrack o) {
		return tracks.add(o);
	}

	public void clear() {
		tracks.clear();
	}

	public boolean contains(Object o) {
		return tracks.contains(o);
	}

	public DAAPTrack get(int index) {
		int i = 0;
		for (DAAPTrack t: this) {
			if (i == index) return t;
		}
		return null;
	}

	public Iterator<DAAPTrack> iterator() {
		return tracks.iterator();
	}

	public boolean remove(Object o) {
		return tracks.remove(o);
	}

	public DAAPTrack set(int index, Track element) {
		throw new RuntimeException("unimplemented for this type");
	}

	public int size() {
		return tracks.size();
	}

}
