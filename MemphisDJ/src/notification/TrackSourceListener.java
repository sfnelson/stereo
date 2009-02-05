package notification;


import interfaces.Track;

import java.util.Set;



public interface TrackSourceListener extends Listener {

	public void tracksAvailable();
	public void tracksUnavailable(Set<? extends Track> available);
	
}
