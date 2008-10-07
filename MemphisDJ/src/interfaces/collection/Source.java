package interfaces.collection;


import java.util.Set;

import music.Track;
import notification.EventGenerator;

/**
 * This interface is designed to represent the notion of a playlist/collection as a
 * source of songs. It uses a publisher/subscriber model to handle synchronising
 * tracks between low-level sources (e.g. DAAPSources) and high-level sources
 * (e.g. Playlists).
 *
 * @param <T>
 */
public interface Source<T extends Track> extends EventGenerator<Source.Listener> {

	/**
	 * Returns the name of this source.
	 * 
	 * @return the name of this source
	 */
	public String name();
	
	/**
	 * Returns the id (internal) of this source.
	 * 
	 * @return a unique collection id
	 */
	public int id();
	
	/**
	 * Returns the persistent id of this source.
	 * 
	 * @return a unique, persistent id for the source
	 */
	public long persistentId();
	
	/**
	 * Returns the next song in this source. It is up to the implementor
	 * to determine what this means. For example, a collection may treat
	 * this as getRandom() while a playlist may maintain an internal
	 * iterator and provide a reset method.
	 *  
	 * @return the next track, whatever that means
	 */
	public T next();
	
	/**
	 * Returns true if there is a next track. @See(Source.next()).
	 * 
	 * @return true if there is a next track.
	 */
	public boolean hasNext();
	
	/**
	 * Returns an iterable collection of the tracks this source provides.
	 * 
	 * @return iterable collection of tracks provided by this source
	 */
	public Iterable<? extends Track> tracks();
	
	/**
	 * This interface allows clients of a source to register with it to
	 * receive updates when it changes.
	 *
	 * @param <T>
	 */
	public interface Listener extends notification.Listener {
		
		/**
		 * Notifies the listener that the source has new tracks, and provides
		 * an iterable collection of those tracks.
		 * 
		 * @param tracks The collection of tracks added to the source
		 */
		public void added(Iterable<? extends Track> tracks);
		
		/**
		 * Notifies the listener that tracks have been removed and provides
		 * an iterable collection of the removed tracks.
		 * 
		 * @return The tracks which the listener is using.
		 */
		public void removed(Set<? extends Track> tracks);
		
	}
}
