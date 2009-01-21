package interfaces;

import java.util.ArrayList;
import java.util.List;

public class AbstractAlbum implements Album {

	public Object get(Constants code) {
		switch (code) {
		case dmap_itemid:
			return id;
		case dmap_persistentid:
			return persistentId;
		case dmap_itemname:
			return name;
		case daap_songalbumartist:
			return artist;
		case dmap_itemcount:
			return tracks;
		default:
			return null;
		}
	}

	public Iterable<Constants> getAllTags() {
		List<Constants> tags = new ArrayList<Constants>();
		tags.add(Constants.dmap_itemid);
		tags.add(Constants.dmap_persistentid);
		tags.add(Constants.dmap_itemname);
		tags.add(Constants.daap_songalbumartist);
		tags.add(Constants.dmap_itemcount);
		return tags;
	}

	private final int id;
	private final long persistentId;
	private final String name;
	private final String artist;
	
	private int tracks;
	
	public AbstractAlbum(int id, long persistentId, String name, String artist, int tracks) {
		this.id = id;
		this.persistentId = persistentId;
		this.name = name;
		this.artist = artist;
		this.tracks = tracks;
	}
	
	public int id() {
		return id;
	}

	public long persistentId() {
		return persistentId;
	}
	
	public String name() {
		return name;
	}
	
	public String artist() {
		return artist;
	}
	
	public int tracks() {
		return tracks;
	}
	
	protected void setTracks(int tracks) {
		this.tracks = tracks;
	}
}
