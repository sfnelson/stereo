package interfaces;

import notification.LibraryListener;
import interfaces.collection.Collection;
import interfaces.collection.Source;

public interface Library<T extends Track> extends Collection<T> {

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
