package notification;

public interface QueueListener extends Listener {

	public void queueEmpty();
	public void tracksAvailable();
	public void queueChanged();
	
}
