package interfaces;

import api.collections.Collection;
import api.collections.Source;
import api.tracks.Album;
import api.tracks.Track;
import notification.LibraryListener;

public interface Library<T extends Track> extends Source<T> {

	public void connect(String path);
	
	public boolean addSource(Source<? extends Track> source);
	public boolean removeSource(Source<? extends Track> source);
	
	public boolean addCollection(Collection<? extends Track> collection);
	public boolean removeCollection(Collection<? extends Track> collection);
	public int numCollections();
	public Iterable<Collection<? extends Track>> collections();
	public int nextCollectionId();
	
	public Iterable<? extends Album> albums();
	public int numAlbums();
	
	public int version();
	public void registerLibraryListener(LibraryListener listener);
	public void removeLibraryListener(LibraryListener listener);
	
}
