package interfaces;

import interfaces.collection.Collection;
import music.Track;

public interface DJInterface {
	
	public PlaybackControl playbackControl();
	public PlaybackStatus playbackStatus();
	public Library<? extends Track> library();
	public VolumeControl volume();
	
	public boolean addCollection(Collection<? extends Track> collection);
	public boolean removeCollection(Collection<? extends Track> collection);
	public Iterable<Collection<? extends Track>> collections();

}
