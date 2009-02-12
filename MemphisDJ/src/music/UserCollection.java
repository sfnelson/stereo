package music;

import interfaces.Album;
import interfaces.Constants;
import interfaces.Track;
import interfaces.collection.Collection;
import interfaces.collection.ConcreteCollection;
import interfaces.collection.Source;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
				CollectionTrack ct = new CollectionTrack(t, ++collectionId);
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
	
	public synchronized Iterable<Track> tracks() {
		return new ArrayList<Track>(available);
	}
	
	public void added(Iterable<? extends Track> tracks) {
		List<Track> added = new ArrayList<Track>();
		
		synchronized (this) {
			for (Track t: tracks) {
				if (songs.contains(t) && !available.contains(t)) {
					CollectionTrack ct = new CollectionTrack(t, ++collectionId);
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

	private final class CollectionTrack implements Track {

		private final Track track;
		private final int containerItemId;
		
		public CollectionTrack(Track track, int containerItemId) {
			this.track = track;
			this.containerItemId = containerItemId;
		}
		
		public int hashCode() {
			return track.id();
		}
		
		public boolean equals(Object o) {
			if (o == this) return true;
			else if (o instanceof Track) {
				return ((Track)o).id() == track.id();
			}
			return false;
		}

		public Object get(Constants tag) {
			if (tag == Constants.dmap_containeritemid) {
				return containerItemId;
			}
			else {
				return track.get(tag);
			}
		}

		public Album getAlbum() {
			return track.getAlbum();
		}

		public Iterable<Constants> getAllTags() {
			ArrayList<Constants> tags = new ArrayList<Constants>();
			for (Constants c: tags) {
				tags.add(c);
			}
			tags.add(Constants.dmap_containeritemid);
			return tags;
		}

		public InputStream getStream() throws IOException {
			return track.getStream();
		}

		public int id() {
			return track.id();
		}

		public long persistentId() {
			return track.persistentId();
		}

		public void put(Constants tag, Object value) {
			track.put(tag, value);
		}

		public void setAlbum(Album album) {
			track.setAlbum(album);
		}
		
	}
}
