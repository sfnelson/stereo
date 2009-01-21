package clinterface;

import interfaces.Constants;

import java.io.IOException;
import java.io.InputStream;

import music.Track;

public class CLITrack extends Track {
	
	public CLITrack(int id, long persistent) {
		super(id, persistent);
	}

	public InputStream getStream() throws IOException {
		return null;
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
