package api.impl;

import java.io.IOException;

import common.AbstractTrack;

public class Track extends AbstractTrack {

	public Track(int id, long pid) {
		super(id, pid);
	}
	
	public static class TrackFactory implements api.tracks.Track.TrackFactory {

		public Track create(int id, long persistentId) {
			return new api.impl.Track(id, persistentId);
		}

	}

	public void getStream(StreamReader reader) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
