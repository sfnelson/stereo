package interfaces;

import interfaces.collection.Collection;


public interface PlaybackStatus {

	public byte state();
	
	public Track current();
	public int position();
	public byte[] getAlbumArt();
	public int elapsedTime();
	
	public Collection<? extends Track> playlist();
	
}
