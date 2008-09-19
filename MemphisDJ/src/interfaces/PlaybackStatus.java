package interfaces;


public interface PlaybackStatus {

	public byte state();
	
	public Track currentTrack();
	public byte[] getAlbumArt();
	public int elapsedTime();
	
	public Playlist<? extends Track> getPlaylist();
	
}
