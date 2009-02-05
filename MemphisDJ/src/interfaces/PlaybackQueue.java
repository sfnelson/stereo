package interfaces;

import interfaces.collection.Collection;
import interfaces.collection.Source;

import java.util.List;

import notification.EventGenerator;
import notification.QueueListener;

public interface PlaybackQueue extends EventGenerator<QueueListener> {
	
	public Track current();
	public int position();
	
	public void next();
	public void prev();
	public void clear();
	public void enqueue(List<? extends Track> tracks);
	public boolean hasSongs();
	public void setSource(Source<? extends Track> source);
	
	public Collection<? extends Track> playlist();
}
