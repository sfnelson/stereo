package util.response;

import interfaces.Constants;
import interfaces.Track;

import java.util.ArrayList;
import java.util.List;

import api.Reader;
import api.Response;
import api.Writer;
import api.nodes.TrackNode;

public class PlaylistSongs extends Response {

	private final List<TrackNode> tracks;
	
	public static PlaylistSongs read(Reader reader, Track.TrackFactory factory) {
		
		List<TrackNode> tracks = new ArrayList<TrackNode>();
		
		for (Constants code: reader) {
			if (code == Constants.dmap_listing) {
				Reader list = reader.nextComposite(code);
				for (Constants node: list) {
					if (node == Constants.dmap_listingitem) {
						tracks.add(TrackNode.read(list.nextComposite(node), factory));
					}
				}
			}
		}
		return new PlaylistSongs(tracks);
	}
	
	public PlaylistSongs(Iterable<? extends Track> tracks) {
		super(Constants.daap_playlistsongs, Response.OK);
		
		this.tracks = new ArrayList<TrackNode>();
		for (Track t: tracks) {
			this.tracks.add(new TrackNode(t));
		}
	}
	
	public PlaylistSongs(List<TrackNode> tracks) {
		super(Constants.daap_playlistsongs, Response.OK);

		this.tracks = tracks;
	}
	
	public Iterable<TrackNode> tracks() {
		return tracks;
	}
	
	public int size() {
		return tracks.size();
	}

	public void write(Writer writer) {
		super.write(writer);
		
		writer.appendList(Constants.dmap_listing, (byte)0, tracks);
	}
}
