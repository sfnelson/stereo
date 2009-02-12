package clinterface;

import interfaces.Track;
import interfaces.collection.AbstractCollection;
import interfaces.collection.AbstractSetSource;
import interfaces.collection.Collection;
import interfaces.collection.EditableCollection;
import interfaces.collection.Source;
import api.nodes.PlaylistNode.PlaylistFactory;

public class CLIPlaylist extends AbstractCollection<CLITrack> implements EditableCollection<CLITrack> {

	private final Source<CLITrack> source;
	
	private String name = null;
	private boolean root = false;
	private int parent = 0;
	private int size;
	private int editStatus;
	
	public CLIPlaylist(int id, long persistent) {
		super(id, persistent);
		
		source = new CLISource(this);
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
		if (source.size() == 0) return size; 
		else return source.size();
	}

	public boolean hasNext() {
		throw new RuntimeException("should not be called on cli");
	}

	public CLITrack next() {
		throw new RuntimeException("should not be called on cli");
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
	
	public Source<CLITrack> source() {
		return source;
	}
	
	private class CLISource extends AbstractSetSource<CLITrack> {
		
		private CLIPlaylist collection;
		
		public CLISource(CLIPlaylist collection) {
			this.collection = collection;
		}

		public Collection<CLITrack> collection() {
			return collection;
		}
	}
}
