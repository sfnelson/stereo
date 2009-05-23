package notification;

import api.notification.Listener;

public interface PlayerListener extends Listener {
	
	public void playbackStarted();
	public void playbackFinished();
	
}
