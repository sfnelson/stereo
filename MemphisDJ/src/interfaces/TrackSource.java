package interfaces;

import java.util.List;

import notification.EventGenerator;
import notification.TrackSourceListener;

public interface TrackSource extends EventGenerator<TrackSourceListener> {

	public boolean hasNextTrack();
	public Track nextTrack();
	public void checkPlaylist(List<Track> check);
	
}
