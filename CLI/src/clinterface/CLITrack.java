package clinterface;

import interfaces.AbstractTrack;
import interfaces.Constants;
import interfaces.Track;

import java.io.IOException;

public class CLITrack extends AbstractTrack {
	
	public CLITrack(int id, long persistent) {
		super(id, persistent);
	}

	public void getStream(StreamReader reader) throws IOException {
		//do nothing
	}
	
	public String toString() {
		String name = (String)get(Constants.dmap_itemname);
		String artist = (String)get(Constants.daap_songartist);
		return name + " - " + artist;
	}
	
	public static Track.TrackFactory factory = new Track.TrackFactory() {
		public CLITrack create(int id, long persistentId) {
			return new CLITrack(id, persistentId);
		}
	};
}
