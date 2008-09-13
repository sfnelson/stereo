package interfaces;

import java.util.List;


public interface Lackey {

	public void checkPlaylist(List<Track> check);
	public List<Track> getAllTracks();
	
	public int version();
	
}
