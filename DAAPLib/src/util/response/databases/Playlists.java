package util.response.databases;

import interfaces.Constants;
import interfaces.Track;
import interfaces.collection.Collection;

import java.util.ArrayList;
import java.util.List;

import api.Reader;
import api.Response;
import api.Writer;
import api.nodes.PlaylistNode;

public class Playlists extends Response {
	
	public final List<PlaylistNode> playlists;
	
	public Playlists(List<PlaylistNode> playlists) {
		super(Constants.daap_databaseplaylists, OK);
		
		this.playlists = playlists;
	}
	
	public Playlists(Iterable<? extends Collection<? extends Track>> playlists) {
		super(Constants.daap_databaseplaylists, OK);
		
		this.playlists = new ArrayList<PlaylistNode>();
		
		for (Collection<? extends Track> coll: playlists) {
			this.playlists.add(new PlaylistNode(coll));
		}
	}
	
	public static Playlists read(Reader reader, PlaylistNode.PlaylistFactory factory) {
		
		List<PlaylistNode> playlists = new ArrayList<PlaylistNode>();
		
		for (Constants code: reader) {
			if (code == Constants.dmap_listing) {
				Reader list = reader.nextComposite(code);
				for (Constants node: list) {
					if (node == Constants.dmap_listingitem) {
						playlists.add(PlaylistNode.read(list.nextComposite(node), factory));
					}
				}
			}
		}
		return new Playlists(playlists);
	}
	
	public List<PlaylistNode> playlists() {
		return playlists;
	}

	public void write(Writer writer) {
		super.write(writer);

		writer.appendList(Constants.dmap_listing, (byte)0, playlists);
	}

}
