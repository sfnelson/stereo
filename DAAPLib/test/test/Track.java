package test;

import java.io.IOException;
import java.io.InputStream;

public class Track extends interfaces.AbstractTrack {

	public Track(int id, long pid) {
		super(id, pid);
	}
	
	public InputStream getStream() throws IOException {
		return null;
	}
	
	public static class TrackFactory implements interfaces.Track.TrackFactory {

		public Track create(int id, long persistentId) {
			return new test.Track(id, persistentId);
		}

	}

}
