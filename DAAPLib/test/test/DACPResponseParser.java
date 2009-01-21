package test;

import music.Track.TrackFactory;
import api.nodes.AlbumNode.AlbumFactory;
import api.nodes.PlaylistNode.PlaylistFactory;

public class DACPResponseParser extends reader.DACPResponseParser {

	@Override
	public AlbumFactory albumFactory() {
		return new Album.AlbumFactory();
	}

	@Override
	public PlaylistFactory playlistFactory() {
		return new Playlist.PlaylistFactory();
	}

	@Override
	public TrackFactory trackFactory() {
		return new Track.TrackFactory();
	}

}
