package clinterface;

import interfaces.Track;
import interfaces.collection.AbstractCollection;
import interfaces.collection.Collection;
import interfaces.collection.EditableCollection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import api.nodes.PlaylistNode.PlaylistFactory;

public class CLIPlaylist extends AbstractCollection<CLITrack> implements EditableCollection<CLITrack> {

	private final List<CLITrack> tracks = new ArrayList<CLITrack>();
	
	private String name = null;
	private boolean root = false;
	private int parent = 0;
	private int size;
	private int editStatus;
	
	public CLIPlaylist(int id, long persistent) {
		super(id, persistent);
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
	
	public int parentId() {
		return parent;
	}
	
	public int size() {
		if (tracks.size() == 0) return size; 
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
		return editStatus;
	}
	
	public void setEditStatus(int editStatus) {
		this.editStatus = editStatus;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParentId(int parentId) {
		this.parent = parentId;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public static PlaylistFactory factory() {
		return factory;
	}
	private static CLIPlaylistFactory factory = new CLIPlaylistFactory();
	private static class CLIPlaylistFactory implements PlaylistFactory {
		public EditableCollection<? extends Track> create(int id, long pid) {
			return new CLIPlaylist(id, pid);
		}
	}
}
