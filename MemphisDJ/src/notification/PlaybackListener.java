package notification;

import interfaces.PlaybackQueue;
import interfaces.Track;

public interface PlaybackListener extends Listener {

	public void stateChanged(byte state);
	public void trackChanged(Track track);
	public void queueChanged(PlaybackQueue queue);
	
}
