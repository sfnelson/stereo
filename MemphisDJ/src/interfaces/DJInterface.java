package interfaces;

import interfaces.collection.Collection;

public interface DJInterface {
	
	public int id();
	public String name();
	
	public PlaybackControl playbackControl();
	public PlaybackStatus playbackStatus();
	public Library<? extends Track> library();
	public VolumeControl volume();
	
	public boolean addCollection(Collection<? extends Track> collection);
	public boolean removeCollection(Collection<? extends Track> collection);
	public Iterable<Collection<? extends Track>> collections();

}
