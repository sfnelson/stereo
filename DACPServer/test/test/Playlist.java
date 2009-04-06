package test;

import interfaces.Track;
import interfaces.collection.AbstractCollection;
import interfaces.collection.Collection;
import interfaces.collection.EditableCollection;
import interfaces.collection.Source;

public class Playlist extends AbstractCollection<Track> implements EditableCollection<Track> {
	
	public static class PlaylistFactory implements api.nodes.PlaylistNode.PlaylistFactory {
		public EditableCollection<? extends Track> create(int id, long pid) {
			return new Playlist(id, pid);
		}
	}

	private int editStatus;
	private String name;
	private Playlist parent;
	private boolean root;
	private int size;

	public Playlist(int id, long persistentId, int editStatus, String name, int parent, boolean root, int size) {
		super(id, persistentId);

		this.editStatus = editStatus;
		this.name = name;
		this.parent = new Playlist(parent, parent);
		this.root = root;
		this.size = size;
	}

	public Playlist(int id, long persistentId) {
		super(id, persistentId);
	}

	public int editStatus() {
		return editStatus;
	}

	public boolean isRoot() {
		return root;
	}

	public String name() {
		return name;
	}

	public int size() {
		return size;
	}

	public Collection<? extends Track> parent() {
		return parent;
	}

	public void setEditStatus(int editStatus) {
		this.editStatus = editStatus;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParentId(int parentId) {
		this.parent = new Playlist(parentId, parentId);
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Source<Track> source() {
		// TODO Auto-generated method stub
		return null;
	}

}
