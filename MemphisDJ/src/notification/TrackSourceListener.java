package notification;


import java.util.Set;

import music.Track;


public interface TrackSourceListener extends Listener {

	public void tracksAvailable();
	public void tracksUnavailable(Set<? extends Track> available);
	
}
