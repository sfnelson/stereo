package notification;

import api.notification.Listener;


public interface LibraryListener extends Listener {

	public void libraryVersionChanged(int version);
	
}
