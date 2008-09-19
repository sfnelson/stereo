package interfaces;

import java.util.List;

import notification.EventGenerator;
import notification.QueueListener;

public interface PlaybackQueue extends EventGenerator<QueueListener> {
	public Track current();
	public void next();
	public void prev();
	public void clear();
	public void enqueue(List<? extends Track> tracks);
	public boolean hasSongs();
	
	public Playlist<? extends Track> playlist();
}
