package music;

import interfaces.Album;
import interfaces.Constants;
import interfaces.Track;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CollectionTrack implements Track {

	private final Track track;
	private final int container;
	private final int containerItemId;
	
	public CollectionTrack(Track track, int container, int containerItemId) {
		this.track = track;
		this.container = container;
		this.containerItemId = containerItemId;
	}
	
	public int hashCode() {
		return track.id();
	}
	
	public boolean equals(Object o) {
		if (o == this) return true;
		else if (o instanceof Track) {
			return ((Track)o).id() == track.id();
		}
		return false;
	}

	public Object get(Constants tag) {
		switch (tag) {
		case dmap_containeritemid:
			return containerItemId;
		case dmap_parentcontainerid:
			return container;
		default:
			return track.get(tag);
		}
	}

	public Album getAlbum() {
		return track.getAlbum();
	}

	public Iterable<Constants> getAllTags() {
		ArrayList<Constants> tags = new ArrayList<Constants>();
		for (Constants c: track.getAllTags()) {
			tags.add(c);
		}
		tags.add(Constants.dmap_containeritemid);
		tags.add(Constants.dmap_parentcontainerid);
		return tags;
	}

	public InputStream getStream() throws IOException {
		return track.getStream();
	}

	public int id() {
		return track.id();
	}

	public long persistentId() {
		return track.persistentId();
	}

	public void put(Constants tag, Object value) {
		track.put(tag, value);
	}

	public void setAlbum(Album album) {
		track.setAlbum(album);
	}
	
	public String toString() {
		return track.toString();
	}
}