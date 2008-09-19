package interfaces;

import java.util.List;

public interface Playlist<T extends Track> extends List<T> {

	public int id();
	public long persistantId();
	public String name();
	public Playlist<? extends Track> parent();
	public boolean isRoot();
	
}
