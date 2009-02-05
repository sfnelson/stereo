package interfaces;

import interfaces.collection.Collection;

import java.util.List;

import notification.EventGenerator;
import notification.PlaybackListener;

public interface PlaybackControl extends EventGenerator<PlaybackListener> {

	public void pause();
	public void play();
	public void next();
	public void prev();
	public void stop();
	public void jump(int pos);

	public void clear();
	public void enqueue(List<? extends Track> tracks);
	public void setCollection(Collection<? extends Track> collection);
	
	public int revision();
	
}