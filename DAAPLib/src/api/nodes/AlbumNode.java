package api.nodes;

import interfaces.Album;
import interfaces.Constants;
import api.Node;
import api.Reader;
import api.Writer;

public class AlbumNode implements Node {

	private final Album album;
	
	public interface AlbumFactory {
		public Album create(int id, long pid, String name, String artist, int tracks);
	}
	
	public static AlbumNode read(Reader reader, AlbumFactory factory) {
		
		int id = 0;
		long pid = 0;
		String name = null;
		String artist = null;
		int tracks = 0;
		
		for (Constants c: reader) {
			switch (c) {
			case dmap_itemid: id = reader.nextInteger(c); break;
			case dmap_persistentid: pid = reader.nextLong(c); break;
			case dmap_itemname: name = reader.nextString(c); break;
			case daap_songalbumartist: artist = reader.nextString(c); break;
			case dmap_itemcount: tracks = reader.nextInteger(c); break;
			}
		}
		
		return new AlbumNode(factory.create(id, pid, name, artist, tracks));
	}
	
	public AlbumNode(Album album) {
		this.album = album;
	}

	public Constants type() {
		return Constants.dmap_listingitem;
	}
	
	public Album album() {
		return album;
	}

	public void write(Writer writer) {
		writer.appendInteger(Constants.dmap_itemid, album.id());
		writer.appendLong(Constants.dmap_persistentid, album.id());
		writer.appendString(Constants.dmap_itemname, album.name());
		writer.appendString(Constants.daap_songalbumartist, album.artist());
		writer.appendInteger(Constants.dmap_itemcount, album.tracks());
	}
	
	
}
