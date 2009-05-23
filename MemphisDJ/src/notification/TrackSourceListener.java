package notification;



import java.util.Set;

import api.notification.Listener;
import api.tracks.Track;



public interface TrackSourceListener extends Listener {

	public void tracksAvailable();
	public void tracksUnavailable(Set<? extends Track> available);
	
}
