
package music;

import interfaces.collection.AbstractSetSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.spi.ServiceRegistry;

import common.AbstractCollection;

import api.collections.Collection;
import api.collections.Source;
import api.tracks.Album;
import api.tracks.Track;

import spi.SourceProvider;

import notification.AbstractEventGenerator;
import notification.LibraryListener;

public class Library extends AbstractSetSource<LibraryTrack>
		implements Source.Listener, interfaces.Library<LibraryTrack> {

	private final Set<Source<? extends Track>> sources = new HashSet<Source<? extends Track>>();
	private final Set<Collection<? extends Track>> collections = new HashSet<Collection<? extends Track>>();
	private final Set<SourceProvider> providers = new HashSet<SourceProvider>();
	
	private volatile int nextCollection = Collection.FIRST_AVAILABLE_ID;
	
	public Library(String name) {
		super();
		this.name = name;
		this.lib = this;
		
		addCollection(this.collection()); //library is a collection in the library
		monitor.nextVersion();
		
		for (Iterator<SourceProvider> it = ServiceRegistry.lookupProviders(SourceProvider.class); it.hasNext();) {
			SourceProvider provider = it.next();
			System.out.println("using source provider: " + provider.getClass().getName());
			provider.create(this);
			providers.add(provider);
		}
	}
	
	private final String name;
	private final Library lib;
	private final Collection<LibraryTrack> collection = new AbstractCollection<LibraryTrack>(Collection.LIBRARY_ID, Collection.LIBRARY_PERSISTENT_ID) {

		public int editStatus() {
			return Collection.NOT_EDITABLE;
		}

		public boolean isRoot() {
			return true;
		}

		public String name() {
			return name;
		}

		public Collection<? extends LibraryTrack> parent() {
			return null;
		}

		public int size() {
			return lib.size();
		}

		public Source<LibraryTrack> source() {
			return lib;
		}
	};
	
	public Collection<LibraryTrack> collection() {
		return collection;
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
	
	public void connect(String path) {
		
		for (SourceProvider provider: providers) {
			provider.connect(path);
		}

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
