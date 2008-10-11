package music;

import interfaces.Album;
import interfaces.collection.AbstractSetCollection;
import interfaces.collection.Collection;
import interfaces.collection.Source;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import notification.AbstractEventGenerator;
import notification.LibraryListener;

public class Library extends AbstractSetCollection<LibraryTrack>
		implements Source.Listener, interfaces.Library<LibraryTrack> {

	private final Set<Source<? extends Track>> sources = new HashSet<Source<? extends Track>>();
	private final Set<Collection<? extends Track>> collections = new HashSet<Collection<? extends Track>>();
	private final String name;
	
	private volatile int nextCollection = FIRST_AVAILABLE_ID;
	
	public Library(String name) {
		super(Collection.LIBRARY_ID, Collection.LIBRARY_PERSISTENT_ID);
		this.name = name;
		
		addCollection(this); //library is a collection in the library
		monitor.nextVersion();
	}
	
	public String name() {
		return name;
	}
	
	public int editStatus() {
		return NOT_EDITABLE;
	}
	
	public boolean addSource(Source<? extends Track> source) {
		if (sources.add(source)) {
			added(source.tracks());
			source.registerListener(this);
			return true;
		}
		return false;
	}
	
	public boolean removeSource(Source<? extends Track> source) {
		if (sources.remove(source)) {
			source.removeListener(this);
			Set<Track> trackSet = new HashSet<Track>();
			for (Track t: source.tracks()) {
				trackSet.add(t);
			}
			removed(trackSet);
			return true;
		}
		return false;
	}
	
	public void added(Iterable<? extends Track> tracks) {
		
		List<LibraryTrack> added = new ArrayList<LibraryTrack>();
		
		for (Track t: tracks) {
			LibraryTrack l = LibraryTrack.factory().create(t);
			l.addBackingTrack(t);
			added.add(l);
		}

		add(added);
		monitor.nextVersion();
	}

	public void removed(Set<? extends Track> tracks) {
		
		Set<LibraryTrack> removed = new HashSet<LibraryTrack>();
		
		for (Track t: tracks) {
			LibraryTrack l = LibraryTrack.factory().create(t);
			if (store.contains(l)) {
				l.removeBackingTrack(t);
				if (l.numBackingTracks() == 0) {
					removed.add(l);
				}
			}
		}

		remove(removed);
		monitor.nextVersion();
	}
	
	public synchronized int nextCollectionId() {
		return nextCollection++;
	}

	public boolean addCollection(Collection<? extends Track> collection) {
		boolean added = collections.add(collection);
		if (added) {
			monitor.nextVersion();
		}
		return added;
	}

	public Iterable<Collection<? extends Track>> collections() {
		return collections;
	}
	
	public int numCollections() {
		return collections.size();
	}

	public boolean removeCollection(Collection<? extends Track> collection) {
		boolean removed = collections.remove(collection);
		if (removed) {
			monitor.nextVersion();
		}
		return removed;
	}

	public boolean isRoot() {
		return true;
	}

	public Collection<LibraryTrack> parent() {
		return null;
	}

	public Iterable<? extends Album> albums() {
		
		Set<Album> albums = new HashSet<Album>();
		for (Track t: tracks()) {
			albums.add(t.getAlbum());
		}
		return albums;
	}

	public int numAlbums() {
		Set<Album> albums = new HashSet<Album>();
		for (Track t: tracks()) {
			albums.add(t.getAlbum());
		}
		return albums.size();
	}

	public void registerLibraryListener(LibraryListener listener) {
		monitor.registerListener(listener);
	}

	public void removeLibraryListener(LibraryListener listener) {
		monitor.removeListener(listener);
	}

	public int version() {
		return monitor.version();
	}
	
	private LibraryMonitor monitor = new LibraryMonitor();
	private class LibraryMonitor extends AbstractEventGenerator<LibraryListener> {
		private int version;
		public synchronized int version() {
			return version;
		}
		public synchronized void nextVersion() {
			version++;
			for (LibraryListener l: listeners()) {
				l.libraryVersionChanged(version);
			}
		}
	}
}
