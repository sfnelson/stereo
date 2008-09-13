package sample;

import memphis.stereo.backend.impl.DatabaseSong;
import memphis.stereo.backend.sources.Source;
import memphis.stereo.song.metadata.Metadata;

public class DaapSong extends DatabaseSong {

	public DaapSong(Source source, long id, int reference, Metadata metadata) {
		super(source, id, metadata);
		
		this.reference = reference;
	}
	
	public int reference() {
		return this.reference;
	}
	
	private final int reference;
}
