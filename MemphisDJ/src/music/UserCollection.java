package music;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import interfaces.collection.AbstractCollection;
import interfaces.collection.Collection;
import interfaces.collection.Source;

public class UserCollection extends AbstractCollection<Track> implements Source.Listener {
	
	private final String name;
	
	public UserCollection(String name, int id, long persistent, Source<? extends Track> source) {
		super(id, persistent);
		this.name = name;
		source.registerListener(this);
	}

	public boolean isRoot() {
		return false;
	}

	public String name() {
		return name;
	}

	public Collection<Track> parent() {
		return null;
	}

	public synchronized int size() {
		return songs.size();
	}
	
	public int editStatus() {
		return EDITABLE;
	}

	public synchronized Iterator<Track> iterator() {
		return new ArrayList<Track>(songs).iterator();
	}
	
	public synchronized void add(Iterable<? extends Track> tracks) {
		List<Track> added = new ArrayList<Track>();
		for (Track t: tracks) {
			if (!songs.contains(t)) {
				added.add(t);
				songs.add(t);
			}
		}
		added(added); //assume tracks are available
	}
	
	public synchronized void remove(Iterable<? extends Track> tracks) {
		Set<Track> removed = new HashSet<Track>();
		for (Track t: tracks) {
			if (songs.contains(t)) {
				removed.add(t);
				songs.remove(t);
			}
		}
		removed(removed); //assume tracks are available
	}
	
	// Source methods: only returns available tracks
	
	public synchronized boolean hasNext() {
		return !available.isEmpty();
	}

	public synchronized Track next() {
		int size = available.size();
		int idx = ((int)(Math.random()*size))%size;
		int i = 0;
		for (Track t: available) {
			if (i == idx) return t;
			i++;
		}
		return null;
	}
	
	public synchronized Iterable<? extends Track> tracks() {
		return new ArrayList<Track>(available);
	}
	
	private Set<Track> songs = new HashSet<Track>();
	private Set<Track> available = new HashSet<Track>();

	public void added(Iterable<? extends Track> tracks) {
		List<Track> added = new ArrayList<Track>();
		
		synchronized (this) {
			for (Track t: tracks) {
				if (songs.contains(t) && !available.contains(t)) {
					added.add(t);
					available.add(t);
				}
			}
		}
		
		for (Source.Listener l: listeners()) {
			l.added(added);
		}
	}

	public synchronized void removed(Set<? extends Track> tracks) {
		Set<Track> removed = new HashSet<Track>();
		
		synchronized (this) {
			for (Track t: tracks) {
				if (available.contains(t)) {
					removed.add(t);
					available.remove(t);
				}
			}
		}
		
		for (Source.Listener l: listeners()) {
			l.removed(removed);
		}
	}

}
