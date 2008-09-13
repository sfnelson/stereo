package interfaces;

import java.util.List;

public interface LibraryInterface {

	public List<Track> getLibrary();
	public int libraryVersion();
	
	public void registerLibraryListener(LibraryListener l);
}
