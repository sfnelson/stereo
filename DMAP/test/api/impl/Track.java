package api.impl;

import java.io.IOException;

public class Track extends interfaces.AbstractTrack {

	public Track(int id, long pid) {
		super(id, pid);
	}
	
	public static class TrackFactory implements interfaces.Track.TrackFactory {

		public Track create(int id, long persistentId) {
			return new api.impl.Track(id, persistentId);
		}

	}

	public void getStream(StreamReader reader) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
