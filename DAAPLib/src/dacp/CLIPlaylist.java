package dacp;

import interfaces.Playlist;
import interfaces.Track;

import java.util.ArrayList;

public class CLIPlaylist extends ArrayList<CLITrack> implements Playlist<CLITrack> {

	private static final long serialVersionUID = 7054413946148938080L;

	private int id = 0;
	private long persistantId = 0;
	private boolean isRoot = false;
	private String name = "";
	private int parentId = 0;
	private Playlist<Track> parent = null;
	private int specifiedSize = 0;
	
	public void setId(int value) {
		this.id = value;
	}
	
	public int id() {
		return id;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public String name() {
		return name;
	}

	public void setParent(Playlist<Track> parent) {
		this.parent = parent;
	}
	
	public Playlist<Track> parent() {
		return this.parent;
	}
	
	public void setParentId(int id) {
		this.parentId = id;
	}
	
	public int parentId() {
		return parentId;
	}

	public long persistantId() {
		return persistantId;
	}
	
	public void setPersistantId(long value) {
		this.persistantId = value;
	}

	public void setName(String value) {
		this.name = value;
	}

	public void setRoot(boolean value) {
		this.isRoot = value;
	}

	public int specifiedSize() {
		return this.specifiedSize;
	}
	
	public void setSpecifiedSize(int value) {
		this.specifiedSize = value;
	}


}
