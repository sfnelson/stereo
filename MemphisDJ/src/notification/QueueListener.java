package notification;

import api.notification.Listener;

public interface QueueListener extends Listener {

	public void queueEmpty();
	public void tracksAvailable();
	public void queueChanged();
	
}
