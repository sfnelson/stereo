package interfaces;


import java.util.List;

import api.collections.Collection;
import api.collections.Source;
import api.notification.EventGenerator;
import api.tracks.Track;

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
	
	public Collection<? extends Track> queue();
	public Collection<? extends Track> shuffle();
	public Collection<? extends Track> playlist();
	public Collection<? extends Track> recent();
}
