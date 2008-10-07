package notification;

import music.Track;
import interfaces.PlaybackQueue;

public interface PlaybackListener extends Listener {

	public void stateChanged(byte state);
	public void trackChanged(Track track);
	public void queueChanged(PlaybackQueue queue);
	
}
