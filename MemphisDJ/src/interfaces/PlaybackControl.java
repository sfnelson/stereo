package interfaces;

import java.util.List;

import notification.EventGenerator;
import notification.PlaybackListener;

public interface PlaybackControl extends EventGenerator<PlaybackListener> {

	public void pause();
	public void play();
	public void next();
	public void prev();
	public void stop();

	public void clear();
	public void enqueue(List<? extends Track> tracks);
	
	public int revision();
	
}