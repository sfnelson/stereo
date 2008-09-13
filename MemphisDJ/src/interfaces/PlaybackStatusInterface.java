package interfaces;

import java.util.List;

public interface PlaybackStatusInterface {

	public Track currentTrack();
	public int getVolume();
	public byte playbackStatus();
	public int playbackRevision();
	public int playbackElapsedTime();
	public List<Track> getPlaylist();
	public byte[] getAlbumArt();
	
	public void registerPlaybackStatusListener(PlaylistStatusListener l);
	public void removePlaybackStatusListener(PlaylistStatusListener l);
}
