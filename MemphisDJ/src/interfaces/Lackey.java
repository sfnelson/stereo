package interfaces;

import java.util.List;

import notification.EventGenerator;
import notification.LibraryListener;


public interface Lackey extends EventGenerator<LibraryListener> {

	public List<? extends Track> getLibrary();
	public List<? extends Album> getAlbums();
	public List<? extends Playlist<? extends Track>> getPlaylists();
	public int version();
	public TrackSource trackSource();
	
}
