package interfaces;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDJ implements DJInterface {

	private Set<LibraryListener> libraryListeners = new HashSet<LibraryListener>();
	
	public void registerLibraryListener(LibraryListener l) {
		libraryListeners.add(l);
	}

	public void removeLibraryListener(LibraryListener l) {
		libraryListeners.remove(l);
	}
	
	protected void notifyLibraryVersionChanged() {
		for (LibraryListener l: libraryListeners) {
			l.libraryVersionChanged(this);
		}
	}
	
	private Set<PlaylistStatusListener> playbackListeners = new HashSet<PlaylistStatusListener>();
	
	public void registerPlaybackStatusListener(PlaylistStatusListener l) {
		playbackListeners.add(l);
	}

	public void removePlaybackStatusListener(PlaylistStatusListener l) {
		playbackListeners.remove(l);
	}
	
	protected void notifyCurrentTrackChanged() {
		for (PlaylistStatusListener l: playbackListeners) {
			l.currentTrackChanged(this);
		}
	}

	
}
