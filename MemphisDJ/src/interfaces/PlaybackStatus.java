package interfaces;

import api.collections.Collection;
import api.tracks.Track;


public interface PlaybackStatus {

	public byte state();
	
	public Track current();
	public int position();
	public byte[] getAlbumArt();
	public byte[] getCurrentSong();
	public int elapsedTime();
	
	public Collection<? extends Track> playlist();
}
