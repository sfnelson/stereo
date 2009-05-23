package music;

import interfaces.collection.ConcreteCollection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import api.collections.Collection;
import api.collections.Source;
import api.tracks.Track;

import notification.AbstractEventGenerator;

public class UserCollection extends AbstractEventGenerator<Source.Listener>
		implements Source<Track>, Source.Listener {
	
	private final Set<CollectionTrack> songs = new HashSet<CollectionTrack>();
	private final Set<CollectionTrack> available = new HashSet<CollectionTrack>();
	private final Collection<Track> collection;
	
	private int collectionId;

	public UserCollection(String name, int id, long persistent, Source<? extends Track> source) {
		
		UserCollection dis = this;
		collection = new ConcreteCollection<Track>(id, persistent, name, Collection.EDITABLE, false, null, 0, dis) {
			public int size() {
				return songs.size();
			}
		};
		
		this.collectionId = id;

		source.registerListener(this);
	}
	
	public Collection<Track> collection() {
		return collection;
	}
	
	public synchronized int size() {
		return songs.size();
	}

	public synchronized Iterator<Track> iterator() {
		return new ArrayList<Track>(songs).iterator();
	}
	
	public synchronized void add(Iterable<? extends Track> tracks) {
		List<Track> added = new ArrayList<Track>();
		for (Track t: tracks) {
			if (!songs.contains(t)) {
				CollectionTrack ct = new CollectionTrack(t, this.collection.id(), ++collectionId);
				added.add(ct);
				songs.add(ct);
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
	
	public synchronized List<Track> tracks() {
		return new ArrayList<Track>(available);
	}
	
	public void added(Iterable<? extends Track> tracks) {
		List<Track> added = new ArrayList<Track>();
		
		synchronized (this) {
			for (Track t: tracks) {
				if (songs.contains(t) && !available.contains(t)) {
					CollectionTrack ct = new CollectionTrack(t, this.collection.id(), ++collectionId);
					added.add(ct);
					available.add(ct);
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
