package clinterface;

import interfaces.collection.AbstractCollection;
import interfaces.collection.Collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import music.Track;

public class CLIPlaylist extends AbstractCollection<CLITrack> {

	private final List<CLITrack> tracks = new ArrayList<CLITrack>();
	private final String name;
	
	private boolean root = false;
	private int parent = 0;
	private int specifiedSize;
	
	public CLIPlaylist(int id, long persistent, String name) {
		super(id, persistent);
		this.name = name;
	}
	
	public String name() {
		return name;
	}
	
	public void setRoot(boolean isRoot) {
		this.root = isRoot;
	}
	
	public boolean isRoot() {
		return root;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}
	
	public Collection<CLITrack> parent() {
		//TODO store parents in accessible place so they can be retrieved
		return null;
	}

	public void specifySize(int size) {
		this.specifiedSize = size;
	}
	
	public int size() {
		if (tracks.size() == 0) return specifiedSize; 
		else return tracks.size();
	}

	public boolean hasNext() {
		throw new RuntimeException("should not be called on cli");
	}

	public CLITrack next() {
		throw new RuntimeException("should not be called on cli");
	}
	
	public void add(CLITrack track) {
		tracks.add(track);
	}

	public Iterable<? extends Track> tracks() {
		return tracks;
	}

	public Iterator<CLITrack> iterator() {
		return tracks.iterator();
	}

	public int editStatus() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
